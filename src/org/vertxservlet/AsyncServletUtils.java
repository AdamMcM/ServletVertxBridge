package org.vertxservlet;

import io.vertx.core.eventbus.EventBus;
import java.io.PrintWriter;
import javax.servlet.http.*;
import javax.servlet.AsyncContext;
import java.util.*;
import java.util.concurrent.*;
import io.vertx.core.*;
import java.util.function.*;

public class AsyncServletUtils {

//    public static void writeAndComplete(HttpServletRequest servletRequest, String outputString) {
//        try {
//            AsyncContext aContext = servletRequest.getAsyncContext();
//            final PrintWriter writer = aContext.getResponse().getWriter();
//            writer.println(outputString);
//            writer.flush();
//            writer.close();
//            aContext.complete();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
    public static void asyncPassByParams(Vertx vertx, HttpServletRequest request, HttpServletResponse response) {
        String route = request.getParameter("route");
        String message = request.getParameter("message");
        send(vertx,request, response, route, message, (aWriter, body)-> {aWriter.writeAndComplete(body);});
    }

    static void send(Vertx vertx, HttpServletRequest request, HttpServletResponse response, String route, String message, BiConsumer<AsyncWriter, String> consumer) {
        AsyncContext aContext = request.startAsync(request, response);
        EventBus eb = vertx.eventBus();
        
        aContext.start(() -> {
            eb.request(route, message, ar -> {

                String body = "No Reply";
                AsyncContext context = aContext;

                if (ar.succeeded()) {
                    body = ar.result().body().toString();
                } else {
                    body = "Error on Event Bus " + ar.toString();
                }

                try {
                    final String outputString = body;

                    vertx.executeBlocking(promise -> {
                        AsyncWriter aWriter = new AsyncWriter(request, response);
                        consumer.accept(aWriter, outputString);
                    }, false, res -> {

                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }
}
