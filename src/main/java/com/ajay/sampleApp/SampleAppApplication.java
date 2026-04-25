package com.ajay.sampleApp;

import com.ajay.sampleApp.db.entities.GuestEntity;
import com.ajay.sampleApp.db.entities.UserEntity;
import com.ajay.sampleApp.db.entities.WeddingEventEntity;
import com.ajay.sampleApp.resources.AdminResource;
import com.ajay.sampleApp.resources.UserResource;
import com.ajay.sampleApp.resources.WeddingResource;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.dropwizard.core.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterRegistration;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class SampleAppApplication extends Application<SampleAppConfiguration> {

    public static void main(final String[] args) throws Exception {
        new SampleAppApplication().run(args);
    }

    @Override
    public String getName() {
        return "SampleApp";
    }

    public static final List<Class<?>> RESOURCE_CLASSES = Arrays.asList(UserResource.class, WeddingResource.class, AdminResource.class);

    // Create a Hibernate bundle for database access
    private final HibernateBundle<SampleAppConfiguration> hibernateBundle =
            new HibernateBundle<SampleAppConfiguration>(UserEntity.class, GuestEntity.class, WeddingEventEntity.class) {
                @Override
                public DataSourceFactory getDataSourceFactory(SampleAppConfiguration configuration) {
                    return configuration.getDataSourceFactory();
                }
            };


    @Override
    public void initialize(final Bootstrap<SampleAppConfiguration> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false))
        );

        bootstrap.addBundle(new MigrationsBundle<SampleAppConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(SampleAppConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }

            @Override
            public String getMigrationsFileName() {
                return "db/migrations.xml";
            }
        });
        bootstrap.addBundle(hibernateBundle);
    }

    @Override
    public void run(final SampleAppConfiguration configuration,
                    final Environment environment) {
        // Configure CORS
        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, configuration.getCorsAllowedOrigins());
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin,Authorization,X-Admin-Secret");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        // Configure ObjectMapper for Java 8 Date/Time types
        environment.getObjectMapper().registerModule(new JavaTimeModule());
        environment.getObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Injector injector = Guice.createInjector(new SampleAppModule(configuration, hibernateBundle));
        registerResources(environment, injector);
    }

    private void registerResources(Environment environment, Injector injector){

        for ( Class<?> className : RESOURCE_CLASSES){
            environment.jersey().register(injector.getInstance(className));
        }
    }

}
