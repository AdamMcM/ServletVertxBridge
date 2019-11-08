# AsyncServletVertx-Bridge (not ready for production)
This repository may be useful when integrating Vert.x (via the EventBus) in an existing servlet-based application.  Http traffic passes through the servlet's http connection.

This packages provides a few small classes and utility methods to pass a message from a servlet to a vert.x EventBus, and then send the EventBus reply back through the servlet's original Http connection.  Using Servlet AsyncContext, this all happens minimizing the number of threads and not keeping a servlet thread blocking the entire time.


**Dependencies and Usage:** 

To use these classes, download ServletVertxBridge.jar from the bin directory of this repository:  https://github.com/AdamMcM/AsyncServletVertx-Bridge/blob/master/bin/ServletVertxBridge.jar.  To use the jar file, you will need the minimum Vert.x jars on your class path, along with the libaries typically found in a servlet app.  For example, if you are using tomcat, put ServletVertxBridge.jar into your WEB-INF/lib folder, along with the rest of the Vert.x jar files.


# Examples

The following code snippets assume a servlet with asyncSupported = true



**AsyncServletBridge **


```
import import org.vertxservlet.*;

...
...

  // inside a servlet's process method
  
  Vertx vertx = getMyVertxInstance) // get your Vertx instance
  
  AsyncServletBridge asb = new AsyncServletBridge(vertx, servletRequest, servletResponse);
  
  // send a messagse down the Vertx EventBus with a route and message. the EventBus's reply is captured in a BiFunction handler
  // calling asyncSend will "start" the async Context
  
  asb.asyncSend("myRoute", "messageContent", (asyncWriter, replyBody) -> {
    // this handler is executed with a Vertx execute blocking thread.
    // asyncWriter is an instance of AsyncWriter. This class writes down through the servlet's orignal httpConnection
    // the String replyBody is the String sent as a reply on the EventBus route
   
    // if necessary, format the output before sending back to teh client. 
    String finalOutput = replyBody + ", modified before sending";
    
    // AsyncWriter.writeAndComplete() will write, flush, and close 
    // the http connection.  It will also complete the servlet async context
    asyncWriter.writeAndComplete(finalOutput);
  });
  
```
