import java.io.*;
import java.net.*;

public class App {
  public static void main(String[] args) throws Exception {
    String hostName = "localhost"; // Dirección del servidor
    int port = 4000; // Puerto del servidor

    try (Socket socket = new Socket(hostName, port);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        DataInputStream in = new DataInputStream(socket.getInputStream());
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

      int randomNumberInRange = (int) (Math.random() * 100) + 1;
      System.out.println("The random number is: " + randomNumberInRange);

      out.writeInt(randomNumberInRange);

      int distributedMul = in.readInt();
      System.out.println("Distributed multiplication: " + distributedMul);

    } catch (UnknownHostException e) {
      System.err.println("Unknown host: " + hostName);
    } catch (IOException e) {
      System.err.println("Cannot connect to the server: " + e.getMessage());
    }
  }
}
