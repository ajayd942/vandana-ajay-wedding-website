package com.ajay.sampleApp.resources;

import com.ajay.sampleApp.SampleAppConfiguration;
import com.ajay.sampleApp.core.logging.Loggable;
import com.ajay.sampleApp.db.GuestDAO;
import com.ajay.sampleApp.db.WeddingEventDAO;
import com.ajay.sampleApp.db.entities.GuestEntity;
import com.ajay.sampleApp.db.entities.WeddingEventEntity;
import com.google.inject.Inject;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

@Path("/api/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Loggable
public class AdminResource {

    private final GuestDAO guestDAO;
    private final WeddingEventDAO weddingEventDAO;
    private final SampleAppConfiguration configuration;

    @Inject
    public AdminResource(GuestDAO guestDAO, WeddingEventDAO weddingEventDAO, SampleAppConfiguration configuration) {
        this.guestDAO = guestDAO;
        this.weddingEventDAO = weddingEventDAO;
        this.configuration = configuration;
    }

    private void checkAuth(String secret) {
        if (secret == null || !secret.equals(configuration.getAdminSecret())) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
    }

    @POST
    @Path("/events")
    @UnitOfWork
    public Response createEvent(@HeaderParam("X-Admin-Secret") String secret, WeddingEventEntity event) {
        checkAuth(secret);
        WeddingEventEntity savedEvent = weddingEventDAO.create(event);
        return Response.ok(savedEvent).build();
    }

    @GET
    @Path("/events")
    @UnitOfWork
    public Response getAllEvents(@HeaderParam("X-Admin-Secret") String secret) {
        checkAuth(secret);
        List<WeddingEventEntity> events = weddingEventDAO.findAll();
        return Response.ok(events).build();
    }

    @DELETE
    @Path("/events/{id}")
    @UnitOfWork
    public Response deleteEvent(@HeaderParam("X-Admin-Secret") String secret, @PathParam("id") UUID id) {
        checkAuth(secret);
        weddingEventDAO.delete(id);
        return Response.noContent().build();
    }

    @PUT
    @Path("/events/reorder")
    @UnitOfWork
    public Response reorderEvents(@HeaderParam("X-Admin-Secret") String secret, List<UUID> eventIds) {
        checkAuth(secret);
        for (int i = 0; i < eventIds.size(); i++) {
            UUID id = eventIds.get(i);
            WeddingEventEntity event = weddingEventDAO.findById(id);
            if (event != null) {
                event.setDisplayOrder(i);
                weddingEventDAO.update(event);
            }
        }
        return Response.ok().build();
    }

    @GET
    @Path("/rsvps")
    @UnitOfWork
    public Response getAllRsvps(@HeaderParam("X-Admin-Secret") String secret) {
        checkAuth(secret);
        List<GuestEntity> guests = guestDAO.findAll();
        return Response.ok(guests).build();
    }

    @DELETE
    @Path("/rsvps/{id}")
    @UnitOfWork
    public Response deleteRsvp(@HeaderParam("X-Admin-Secret") String secret, @PathParam("id") UUID id) {
        checkAuth(secret);
        guestDAO.delete(id);
        return Response.noContent().build();
    }

    @PUT
    @Path("/rsvps/{id}")
    @UnitOfWork
    public Response updateRsvp(@HeaderParam("X-Admin-Secret") String secret, @PathParam("id") UUID id, GuestEntity guest) {
        checkAuth(secret);
        if (!id.equals(guest.getId())) {
            throw new WebApplicationException("ID mismatch", Response.Status.BAD_REQUEST);
        }
        GuestEntity updatedGuest = guestDAO.update(guest);
        return Response.ok(updatedGuest).build();
    }
}
