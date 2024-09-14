import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            2, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1000), r -> {
                
    });

  public static void main(String[] args){
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

    //  Uncomment this block to pass the first stage
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        int port = 6379;
        try {
          serverSocket = new ServerSocket(port);
          // Since the tester restarts your program quite often, setting SO_REUSEADDR
          // ensures that we don't run into 'Address already in use' errors
          serverSocket.setReuseAddress(true);
          // Wait for connection from client.
          clientSocket = serverSocket.accept();

          processInfo(clientSocket);

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
    private static void processInfo(Socket clientSocket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                OutputStreamWriter outWriter = new OutputStreamWriter(clientSocket.getOutputStream())
        ) {
            String line;
            System.out.println("Waiting for input...");
            while ((line = in.readLine()) != null) {
                System.out.println("Received: " + line);  // Debugging: print received line

                if (line.startsWith("*")) {  // RESP array
                    int argCount = Integer.parseInt(line.substring(1));
                    String[] args = new String[argCount];

                    for (int i = 0; i < argCount; i++) {
                        in.readLine();  // Skip the length indicator line ($<length>)
                        args[i] = in.readLine();  // Read actual argument
                    }

                    // Handle commands like PING
                    if (args.length > 0 && args[0].equalsIgnoreCase("PING")) {
                        outWriter.write("+PONG\r\n");
                        outWriter.flush();
                        System.out.println("Responded with: +PONG");
                    }
                    // Handle other commands as needed (e.g., COMMAND, DOCS)
                    else {
                        outWriter.write("-ERR Unknown command\r\n");
                        outWriter.flush();
                        System.out.println("Responded with: ERR Unknown command");
                    }
                }


            }

            // If readLine() returns null, the client has closed the connection
            System.out.println("Client disconnected, closing connection.");
        } catch (IOException e) {
            System.out.println("IOException in processInfo: " + e.getMessage());
        }
    }
}
