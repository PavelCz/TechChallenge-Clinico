import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;

public class Server {

    public static void main(String[] args) throws Exception {
        int port = 9000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        System.out.println("server started at " + port);
        server.createContext("/", Server::handle);
        server.setExecutor(null);
        server.start();
    }

    public static void handle(HttpExchange he) throws IOException {
        File file = new File("Clinico.html");
        he.sendResponseHeaders(200, file.length());
        // Write file to client
        try (OutputStream os = he.getResponseBody()) {
            Files.copy(file.toPath(), os);
        }
        try(InputStream in = he.getRequestBody()) {

            final byte[] buffer = new byte[in.available() == 0 ? 1024 : in.available()];
            final ByteArrayOutputStream baos = new ByteArrayOutputStream(buffer.length);
            int length;
            while ((length = in.read(buffer, 0, buffer.length)) >= 0) {
                baos.write(buffer, 0, length);
            }
            //he.sendResponseHeaders(200, baos.size());
            baos.writeTo(System.out);
        }
    }
}
