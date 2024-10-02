import handlers.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final int port = 6379;

    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            2, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1000), r -> {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
    }, new ThreadPoolExecutor.DiscardPolicy());

  public static void main(String[] args){
    System.out.println("Logs from your program will appear here!");

    String dir = null;
    String dbfilename = null;

    for (int i = 0; i < args.length; i++) {
        if (args[i].equals("--dir")) {
            dir = args[i + 1];
        } else if (args[i].equals("--dbfilename")) {
            dbfilename = args[i + 1];
        }
    }

      if (dir == null || dbfilename == null) {
          System.out.println("Missing required arguments: --dir and --dbfilename");
          return;
      }

        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        try {
          serverSocket = new ServerSocket(port);
          // Since the tester restarts your program quite often, setting SO_REUSEADDR
          // ensures that we don't run into 'Address already in use' errors
          serverSocket.setReuseAddress(true);
          // Wait for connection from client.
          while (true) {
              clientSocket = serverSocket.accept();
              System.out.println("Accepted connection fromn client: " + clientSocket.getInetAddress());
              Socket finalClientSocket = clientSocket;
//              threadPoolExecutor.execute(new Runnable() {
//                  @Override
//                  public void run() {
//                      processInfo(finalClientSocket);
//                  }
//              });

              //or

              threadPoolExecutor.execute(new ClientHandler(finalClientSocket, dir, dbfilename));
          }

        } catch (IOException e) {
          System.out.println("IOException: " + e.getMessage());
        } finally {
          try {
            if (clientSocket != null) {
              clientSocket.close();
            }
          } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
          }
        }
  }
}
