# AsyncServletVertx-Bridge (not ready for production)
This repository may be useful when integrating Vert.x (via the EventBus) to an existing servlet-based application.  Requests are passed form a servlet, to the Vertx EventBus, and then back through the servlet's http connection. Using Servlet AsyncContext, this all happens minimizing the number of threads and not keeping a servlet thread blocking the entire time.


**Dependencies and install:** 

To use these classes, download ServletVertxBridge.jar from the dist directory of this repository:  https://github.com/AdamMcM/ServletVertxBridge/blob/master/dist/ServletVertxBridge.jar.  To use the jar file, you will need the minimum Vert.x jars on your class path, along with the libaries typically found in a servlet app.  For example, if you are using tomcat, put ServletVertxBridge.jar into your WEB-INF/lib folder, along with the rest of the Vert.x jar files.


# Examples and Usage

The following code snippets assume a servlet with asyncSupported = true

There are two main ways to use this bridge: by using the static mehod asyncPassByParams() or by building and isntance of AsyncServletBridge. We will consider each method:

**1. asyncPassByParams()**

Use the static method AsyncServletBridge.asyncPassByParams() to send a route/message from a servlet to a vertx EventBus using the servlet's request parameters "route" and "message".  The reply from the EventBus is sent back to the servlet's HttpConnection unmodified as a string.  Calling this method will start the servlet's AsyncContext and the context will be completed once the response is sent back to the clinent.

For example, in an async servlet, you could using the following call to 

```
import import org.vertxservlet.*;

...

Vertx vertx = getMyVertxInstance() // get your Vertx instance
AsyncServletBridge.asyncPassByParams(vertx, servletRequest, servletRequest);
```

**2. Instance of AsyncServletBridge**

A more flexible way to bridge between a servlet and vertx is to create an instance of AsyncServletBridge. Creating an instance allows one to spcify the route and messsage (as opposed to automatically routed from the servlet request paramters).  It also provides a handler that is called before sending the response back to the client.  This handler provides an opportunity to format/modify the reply sent from the EventBus before sending back to the client.

Typical usage might look like this:

```
  import import org.vertxservlet.*;
  
  Vertx vertx = getMyVertxInstance() // get your Vertx instance
  
  AsyncServletBridge asb = new AsyncServletBridge(vertx, servletRequest, servletResponse);
  asb.asyncSend("myRoute", "messageContent", (asyncWriter, replyBody) -> {  
    // if necessary, format the output before sending back to teh client. 
    String finalOutput = replyBody + ", modified before sending";
    asyncWriter.writeAndComplete(finalOutput);
  });
  
  ```


