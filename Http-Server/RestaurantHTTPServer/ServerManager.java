import java.net.ServerSocket;
import java.io.IOException;

class ServerManager implements Runnable {

  static final int PORT = 8070;
  private volatile boolean flag = true;

  public void stopRunning() {
    flag = false;
  }

  @Override
  public void run() {
    try {
      ServerSocket serverConnect = new ServerSocket(PORT);
      System.out.println("Server strated. Listening to connections at port " + String.valueOf(PORT) + "...");
      // listening for connections at the port
      while (flag) {
          JavaHTTPServer server = new JavaHTTPServer(serverConnect.accept());
          // create and start a new thread
          Thread thread = new Thread(server);
          thread.start();
      }
      System.out.println("Server stopped.");
      serverConnect.close();
    } catch (IOException e) {
      System.err.println("IOException: " + e.getMessage());
    }
  }
}
