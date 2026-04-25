package com.ajay.sampleApp.resources;

import com.ajay.sampleApp.core.logging.Loggable;
import com.ajay.sampleApp.data.RsvpRequest;
import com.ajay.sampleApp.db.GuestDAO;
import com.ajay.sampleApp.db.WeddingEventDAO;
import com.ajay.sampleApp.db.entities.GuestEntity;
import com.ajay.sampleApp.db.entities.WeddingEventEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/wedding")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Loggable
public class WeddingResource {

    private final GuestDAO guestDAO;
    private final WeddingEventDAO weddingEventDAO;
    private final ObjectMapper objectMapper;

    @Inject
    public WeddingResource(GuestDAO guestDAO, WeddingEventDAO weddingEventDAO, ObjectMapper objectMapper) {
        this.guestDAO = guestDAO;
        this.weddingEventDAO = weddingEventDAO;
        this.objectMapper = objectMapper;
    }

    @POST
    @Path("/rsvp")
    @UnitOfWork
    public Response rsvp(RsvpRequest request) {
        GuestEntity guest = GuestEntity.builder()
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .rsvpStatus(request.isAttending() ? "ATTENDING" : "NOT_ATTENDING")
                .needsCab(request.isNeedsCab())
                .guestCount(request.getGuestCount())
                .build();

        GuestEntity savedGuest = guestDAO.create(guest);
        return Response.ok(savedGuest).build();
    }

    @GET
    @Path("/events")
    @UnitOfWork
    public Response getEvents() {
        List<WeddingEventEntity> events = weddingEventDAO.findAll();
        return Response.ok(events).build();
    }
}
