import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {

  private ArrayList<ConnectionHandler> connections;
  private ServerSocket server;
  private boolean done;
  private ExecutorService pool;

  public Server() {
    connections = new ArrayList<>();
    done = false;
  }

  @Override
  public void run() {
    int serverPort = 9999;

    try {

      server = new ServerSocket(serverPort);
      pool = Executors.newCachedThreadPool();
      while (!done) {
        Socket client = server.accept();
        ConnectionHandler handler = new ConnectionHandler(client);
        connections.add(handler);
        pool.execute(handler);
      }
    } catch (IOException ex) {
      shutdown();
    }
  }

  public void shutdown() {
    try {
      done = true;
      pool.shutdown();
      if (!server.isClosed()) {
        server.close();
      }
    } catch (IOException ex) {
      // just ignore it!
    }
    for (ConnectionHandler ch : connections) {
      ch.shutdown();
    }
  }

  public void broadcast(String message) {
    for (ConnectionHandler ch : connections) {
      if (ch != null) {
        ch.sendMessage(message);
      }
    }
  }

  class ConnectionHandler implements Runnable {

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private String nickname;

    public ConnectionHandler(Socket client) {
      this.client = client;
    }

    @Override
    public void run() {

      try {
        out = new PrintWriter(client.getOutputStream(), true); // set auto flush to true
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out.println("Please enter a nickname: ");

        nickname = in.readLine();
        System.out.println(nickname + " connected!");
        broadcast(nickname + " joined the chat!");

        String message;
        while ((message = in.readLine()) != null) {
          if (message.startsWith("/nick ")) {
            String[] messageSplit = message.split(" ", 2);
            if (messageSplit.length == 2) {
              broadcast(nickname + " renamed themselves to " + messageSplit[1]);
              System.out.println(nickname + " renamed themselves to " + messageSplit[1]);
              nickname = messageSplit[1];

              out.println("Successfully changed nickname to " + nickname);
            } else {
              out.println("No nickname provided!");
            }
          } else if (message.startsWith("/quit")) {
            broadcast(nickname + " left the chat!");
            shutdown();
          } else {
            broadcast(nickname + ": " + message);
          }
        }
      } catch (IOException ex) {
        shutdown();

      }
    }

    public void sendMessage(String message) {
      out.println(message);
    }

    public void shutdown() {
      try {
        in.close();
        out.close();
        if (!client.isClosed()) {
          client.close();
        }
      } catch (IOException ex) {
        // just ignore it!
      }
    }
  }

  public static void main(String[] args) {
    Server server = new Server();
    server.run();
  }
}
