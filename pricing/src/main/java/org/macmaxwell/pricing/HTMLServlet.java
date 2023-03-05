package org.macmaxwell.pricing;

import java.io.File;
import java.io.IOException;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class HTMLServlet extends AbstractHandler {
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (target.endsWith(".html")) {
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            File file = new File("/Users/mac/Documents/nginxjetty/currency/src/main/java/org/macmaxwell/currency/" + target);
            response.getWriter().write(file.toString());
            baseRequest.setHandled(true);
        }
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        server.setHandler(new HTMLServlet());
        server.start();
        server.join();
    }
}