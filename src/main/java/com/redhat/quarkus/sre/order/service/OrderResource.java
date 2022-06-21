package com.redhat.quarkus.sre.order.service;

import java.time.Duration;
import java.time.LocalDateTime;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.redhat.quarkus.sre.order.domain.Order;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.logging.Logger;

import io.micrometer.core.annotation.Counted;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
@Path("orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {
    
    @Inject
    @Channel("orders-out")
    MutinyEmitter<Order> orderEmitter;

    @Inject
    Logger logger;

    @POST
    @Counted
    public Uni<Void> order(Order order) {
        order.setCreationDateTime(LocalDateTime.now());
        logger.infof("OrderResource.order() %s", order.getCustomer());
        // FIXME is not the best way to return after 3s
        return orderEmitter.send(order).ifNoItem().after(Duration.ofSeconds(3)).fail();
    }
    
}
