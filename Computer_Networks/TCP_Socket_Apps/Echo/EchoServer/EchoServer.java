import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
  // Set a initialization port
  private static final int port = 12345;

  public static void main(String[] args) {
    try (ServerSocket serverSocket = new ServerSocket(port)) {
      System.out.println("Echo Server is running of port: " + port);

      while (true) {
        // Accept client connection
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client connected: " + clientSocket.getInetAddress());

        // Create IOStreams fot communication
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        String receviedMessage;
        // Read message from the client echoing it back
        while ((receviedMessage = in.readLine()) != null) {
          System.out.println("Recevied: " + receviedMessage);
          out.println("Echo: " + receviedMessage);
        }
        clientSocket.close();
      }
    } catch (IOException e) {
      System.out.println("Error: " + e.getMessage());
    }
  }
}
