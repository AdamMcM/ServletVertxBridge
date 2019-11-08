package org.vertxservlet;

import java.io.PrintWriter;
import javax.servlet.http.*;
import javax.servlet.AsyncContext;
import java.util.*;

public class AsyncWriter {

    private HttpServletRequest request;
    private HttpServletResponse response;

    protected AsyncWriter(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public boolean writeAndComplete(String outputString) {
        try {
            AsyncContext aContext = request.getAsyncContext();
            final PrintWriter writer = aContext.getResponse().getWriter();
            writer.println(outputString);
            writer.flush();
            writer.close();
            aContext.complete();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean write(String outputString) {
        try {
            AsyncContext aContext = request.getAsyncContext();
            final PrintWriter writer = aContext.getResponse().getWriter();
            writer.println(outputString);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
     public boolean flush() {
        try {
            AsyncContext aContext = request.getAsyncContext();
            final PrintWriter writer = aContext.getResponse().getWriter();
            writer.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean complete() {
        try {
            AsyncContext aContext = request.getAsyncContext();
            final PrintWriter writer = aContext.getResponse().getWriter();
            writer.flush();
            writer.close();
            aContext.complete();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
