package com.redhat.quarkus.sre.order.service;

import java.time.LocalDateTime;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.redhat.quarkus.sre.order.domain.Order;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;
@Path("orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {
    
    @Inject
    @Channel("orders-out")
    Emitter<Order> orderEmitter;

    @Inject
    Logger logger;

    @POST
    public void order(Order order) {
        order.setCreationDateTime(LocalDateTime.now());
        orderEmitter.send(order).thenAcceptAsync(s -> {
            System.out.println("OrderResource.order(ENVIADO PRO KAFKA)");
        }).exceptionally(e -> {
            System.out.println("OrderResource.order(ERRO AO ENVIAR PRO KAFKA)");
            return null;
        });
        logger.infof("OrderResource.order() %s", order.getCustomer());
    }
    
}
