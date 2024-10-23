package HTTP_Socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class HtmlFetcher {
    public static void main(String[] args) {
        String host = "example.com"; // Replace with the target website's domain
        int port = 80; // Port 80 for HTTP

        try (Socket socket = new Socket(host, port)) {
            // Send HTTP GET request
            OutputStream os = socket.getOutputStream();
            String getRequest = "GET / HTTP/1.1\r\n"
                    + "Host: " + host + "\r\n"
                    + "Connection: close\r\n\r\n";
            os.write(getRequest.getBytes());
            os.flush();

            // Read the response
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line); // Print the HTML content line by line
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
