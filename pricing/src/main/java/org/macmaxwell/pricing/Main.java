package org.macmaxwell.pricing;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Random;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;


public class Main{

    public static void main(String[] args) throws Exception {
        int port = 8080;
        Server server = new Server(port);

        ServletContextHandler handler = new ServletContextHandler();
        handler.setContextPath("/");

        ServletHolder servletHolder = new ServletHolder(new RSAEncryptServlet());
        handler.addServlet(servletHolder, "/encrypt");

        server.setHandler(handler);
        server.start();
        server.join();
    }
}

class RSAEncryptServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private BigInteger p;
    private BigInteger q;
    private BigInteger n;
    private BigInteger phi;
    private BigInteger e;
    private BigInteger d;

    public RSAEncryptServlet() {
        Random rnd = new Random();
        int bitLength = 512;
        p = BigInteger.probablePrime(bitLength, rnd);
        q = BigInteger.probablePrime(bitLength, rnd);
        n = p.multiply(q);
        phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        e = BigInteger.probablePrime(bitLength/2, rnd);
        while (phi.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(phi) < 0) {
            e.add(BigInteger.ONE);
        }
        d = e.modInverse(phi);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("<title>RSA Encryption</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>RSA Encryption</h1>");
        out.println("<form method=\"POST\">");
        out.println("<label for=\"message\">Message:</label>");
        out.println("<input type=\"text\" id=\"message\" name=\"message\"><br>");
        out.println("<input type=\"submit\" value=\"Encrypt\">");
        out.println("</form>");
        out.println("</body>");
        out.println("</html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("<title>RSA Encryption</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>RSA Encryption</h1>");

        String message = request.getParameter("message");
        if (message == null || message.isEmpty()) {
            out.println("<p>Please enter a message.</p>");
        } else {
            byte[] encrypted = encrypt(message.getBytes());
            out.println("<p>Encrypted message: " + new String(encrypted) + "</p>");
        }

        out.println("<form method=\"GET\" action=\"/encrypt\">");
        out.println("<input type=\"submit\" value=\"Back\">");
        out.println("</form>");
        out.println("</body>");
        out.println("</html>");
    }

    private byte[] encrypt(byte[] message) {
        BigInteger m = new BigInteger(message);
        BigInteger c = m.modPow(e, n);
        return c.toByteArray();
    }
}
