package org.vertxservlet;

import io.vertx.core.*;
import io.vertx.core.eventbus.*;

public class TestVertx {

    private static Vertx vertx = null;
    private static final Object lock = new Object();

    private TestVertx() {

    }

    public static Vertx getVertx() {

        synchronized (lock) {
            if (vertx == null) {
                
                System.out.println("creating new Test Vertx Instance");

                vertx = Vertx.vertx();
                EventBus eb = vertx.eventBus();

                eb.consumer("myRoute", message -> {
                    message.reply("My Route: got message : " + message.body());
                });

                eb.consumer("sayHi", message -> {
                    message.reply("sayHi: got message : " + message.body());
                });
            }
            return vertx;
        }

    }

}
