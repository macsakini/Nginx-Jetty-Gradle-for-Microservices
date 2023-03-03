package org.macmaxwell.currency;

import org.eclipse.jetty.http.*;
import org.eclipse.jetty.http2.api.Stream;
import org.eclipse.jetty.http2.frames.DataFrame;
import org.eclipse.jetty.http2.frames.HeadersFrame;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.http2.api.server.ServerSessionListener;
import org.eclipse.jetty.http2.server.RawHTTP2ServerConnectionFactory;
import org.eclipse.jetty.util.Callback;

import java.nio.ByteBuffer;


public class Main {
    public static void main(String[] args) throws Exception {
        // Create a Server instance.
        Server server = new Server();

        ServerSessionListener sessionListener = new ServerSessionListener.Adapter()
        {
            @Override
            public Stream.Listener onNewStream(Stream stream, HeadersFrame frame)
            {
                // Send a response after reading the request.
                MetaData.Request request = (MetaData.Request)frame.getMetaData();
                if (frame.isEndStream())
                {
                    respond(stream, request);
                    return null;
                }
                else
                {
                    return new Stream.Listener.Adapter()
                    {
                        @Override
                        public void onData(Stream stream, DataFrame frame, Callback callback)
                        {
                            // Consume the request content.
                            callback.succeeded();
                            if (frame.isEndStream())
                                respond(stream, request);
                        }
                    };
                }
            }

            private void respond(Stream stream, MetaData.Request request)
            {
                // Prepare the response HEADERS frame.

                // The response HTTP status and HTTP headers.
                MetaData.Response response = new MetaData.Response(HttpVersion.HTTP_2, HttpStatus.OK_200, HttpFields.EMPTY);

                if (HttpMethod.GET.is(request.getMethod()))
                {
                    // The response content.
//                    ByteBuffer resourceBytes = getResourceBytes(request);

                    // Send the HEADERS frame with the response status and headers,
                    // and a DATA frame with the response content bytes.
//                    stream.headers(new HeadersFrame(stream.getId(), response, null, false))
//                            .thenCompose(s -> s.data(new DataFrame(s.getId(), resourceBytes, true)));
                    System.out.println("Hellow");
//
                }
                else
                {
                    // Send just the HEADERS frame with the response status and headers.
                    stream.headers(new HeadersFrame(stream.getId(), response, null, true));
                }
            }

        };

        // Create a ServerConnector with RawHTTP2ServerConnectionFactory.
        RawHTTP2ServerConnectionFactory http2 = new RawHTTP2ServerConnectionFactory(sessionListener);

        // Configure RawHTTP2ServerConnectionFactory, for example:
        // Configure the max number of concurrent requests.
        http2.setMaxConcurrentStreams(128);
        // Enable support for CONNECT.
        http2.setConnectProtocolEnabled(true);

        // Create the ServerConnector.
        ServerConnector connector = new ServerConnector(server, http2);

        // Add the Connector to the Server
        server.addConnector(connector);

        // Start the Server so it starts accepting connections from clients.
        server.start();

        System.out.println("Hello world!");
    }
}