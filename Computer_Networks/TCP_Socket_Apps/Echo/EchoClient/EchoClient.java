import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class EchoClient {
  // Known Echo Server IP
  private static String serverIp = "localhost";

  // Known Echo Server port
  private static int port = 12345;

  public static void main(String[] args) {
    try (Socket socket = new Socket(serverIp, port)) {
      System.out.println("Connected to the Echo Server");

      // Create IOStreams for communication
      BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

      String userInput;
      // Read user input and send it to the server
      while (true) {
        System.out.println("Enter a message: ");
        userInput = inputReader.readLine();

        if (userInput.equalsIgnoreCase("exit"))
          break;

        out.println(userInput);
        System.out.println("Server echo: " + in.readLine());
      }

    } catch (IOException e) {
      System.out.println("Error: " + e.getMessage());
    }
  }
}
