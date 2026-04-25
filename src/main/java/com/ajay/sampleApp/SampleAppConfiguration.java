package com.ajay.sampleApp;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import io.dropwizard.db.DataSourceFactory;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class SampleAppConfiguration extends Configuration {
    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    @JsonProperty("database")
    public void setDatabase(DataSourceFactory database) {
        this.database = database;
    }

    @Getter
    @Setter
    @JsonProperty("adminSecret")
    private String adminSecret;

    @Getter
    @Setter
    @JsonProperty("corsAllowedOrigins")
    private String corsAllowedOrigins = "http://localhost:3000";
}
