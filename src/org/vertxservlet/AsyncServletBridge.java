package org.vertxservlet;

import io.vertx.core.eventbus.EventBus;
import java.io.PrintWriter;
import javax.servlet.http.*;
import javax.servlet.AsyncContext;
import java.util.*;
import java.util.concurrent.*;
import io.vertx.core.*;
import java.util.function.*;

public class AsyncServletBridge {

    private Vertx vertx;
    private HttpServletRequest request;
    private HttpServletResponse response;

    public AsyncServletBridge(Vertx vertx, HttpServletRequest request, HttpServletResponse response) {
        this.vertx = vertx;
        this.request = request;
        this.response = response;
    }

    /**
     * *
     * Send a route and message to the EventBus of this.vertx. the reply sent
     * back from the EventBus is written back to the http connection of
     * HttpServletReponse. This method starts the Async Context on the servlet
     * and automatically completes the Async Context when the reply is written
     * back to client.
     *
     * @param route the route for the EventBus
     * @param message the message sent along the route
     */
    public void asyncSend(String route, String message) {
        AsyncServletBridge.send(this.vertx, request, response, route, message, (aWriter, body) -> {
            aWriter.writeAndComplete(body);
        });
    }

    /**
     * *
     * Send a route and message to the EventBus of this.vertx. the reply sent
     * back from the EventBus and passed to the handler to be formated and sent
     * through the servlet's httpConnection This method starts the Async Context
     * on the servlet and completes the asyncContext when the handler's
     * asyncWriter calls writeAndComplete() or complete().
     *
     * <p>
     * example<p>
     * <
     * pre>
     * asyncServletBridge.asyncSend("myRoute", "messageContent", (asyncWriter,
     * reply) -&#62; { String finalOutput = reply + ", modified before sending";
     * asyncWriter.writeAndComplete(finalOutput); });
     * </pre>
     *
     *
     * @param route the route for the EventBus
     * @param message the message sent along the route
     * @param handler passed the AsyncWriter to write back along the servlet's
     * http connection, is called on a Vert.x execute blocking thread
     */
    public void asyncSend(String route, String message, BiConsumer<AsyncWriter, String> handler) {
        AsyncServletBridge.send(this.vertx, request, response, route, message, handler);
    }

    /**
     * The servlet parameters "route" and "message" are used for the route and
     * message. The reply sent back from the EventBus and passed to the handler
     * to be formated and sent through the servlet's httpConnection This method
     * starts the Async Context on the servlet and completes the asyncContext
     * when the handler's asyncWriter calls writeAndComplete() or complete().
     *
     * <p>
     * example<p>
     * <
     * pre>
     * asyncServletBridge.asyncSend("myRoute", "messageContent", (asyncWriter,
     * reply) -&#62; { String finalOutput = reply + ", modified before sending";
     * asyncWriter.writeAndComplete(finalOutput); });
     * </pre>
     *
     *
     * @param route the route for the EventBus
     * @param message the message sent along the route
     * @param handler passed the AsyncWriter to write back along the servlet's
     * http connection, is called on a Vert.x execute blocking thread
     */
    public static void asyncPassByParams(Vertx vertx, HttpServletRequest request, HttpServletResponse response) {
        String route = request.getParameter("route");
        String message = request.getParameter("message");
        send(vertx, request, response, route, message, (aWriter, body) -> {
            aWriter.writeAndComplete(body);
        });
    }

    static void send(Vertx vertx, HttpServletRequest request, HttpServletResponse response, String route, String message, BiConsumer<AsyncWriter, String> consumer) {
        AsyncContext aContext = request.startAsync(request, response);
        EventBus eb = vertx.eventBus();

        aContext.start(() -> {

            if (route != null) {
                eb.request(route, message, ar -> {

                    String body = "";
                    AsyncContext context = aContext;

                    if (ar.succeeded()) {
                        body = ar.result().body().toString();
                    } else {
                        body = "AsyncServletBridge.send error on EventBus reply: " + ar.toString();
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
            } else {
                String outputString = "AsyncServletBridge.send null route";
                AsyncWriter aWriter = new AsyncWriter(request, response);
                consumer.accept(aWriter, outputString);
            }
        });

    }

}
