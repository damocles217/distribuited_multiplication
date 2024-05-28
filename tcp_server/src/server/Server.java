package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
  private static AtomicInteger sharedAdd = new AtomicInteger(1);
  private static AtomicInteger counter = new AtomicInteger(0);
  private int port = 4000;

  public void run() {

    try (ServerSocket server = new ServerSocket(port)) {
      System.out.println("Server running on: " + server.getLocalPort());
      while (true) {
        Socket client = server.accept();
        new ClienteHandler(client).start();
      }

    } catch (IOException e) {
      System.err.println("Error when create the server: " + e.getMessage());
    }
  }

  private static class ClienteHandler extends Thread {
    private Socket clientSocket;

    public ClienteHandler(Socket socket) {
      this.clientSocket = socket;
    }

    public void run() {
      try (DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
          DataInputStream in = new DataInputStream(clientSocket.getInputStream())) {

        int number = in.readInt();
        System.out.println("Received number: " + number);

        int actualValue = sharedAdd.get();
        int result = actualValue * number;
        sharedAdd.set(result);

        while (result == number) {
          Thread.sleep(10);
          result = sharedAdd.get();
        }

        int actualCount = counter.addAndGet(1);

        out.writeInt(result);
        System.out.println("Result sent: " + result);

        if (actualCount >= 2) {
          counter.set(0);
          sharedAdd.set(0);
        }

      } catch (IOException e) {
        System.out.println("Error in the client stablishing connection: " + e.getMessage());
      } catch (InterruptedException e) {
        System.out.println("Error while sleep process: " + e.getMessage());
      } finally {
        try {
          clientSocket.close();
        } catch (IOException e) {
          System.out.println("Error while client socket close: " + e.getMessage());
        }
      }
    }
  }
}
