package handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private static final ConcurrentHashMap<String, String> keyValueStore = new ConcurrentHashMap<>();

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
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
                    else if (args.length > 0 && args[0].equalsIgnoreCase("ECHO")) {
                        if (args.length > 1) {
                            outWriter.write("+" + args[1] + "\r\n");
                            outWriter.flush();
                        } else {
                            outWriter.write("-ERR Missing argument\r\n");
                            outWriter.flush();
                        }
                    }
                    else if (args.length > 0 && args[0].equals("SET")) {
                        if (args.length > 2) {
                            SET(Arrays.copyOfRange(args, 1, args.length));
                            outWriter.write("+OK\r\n");
                            outWriter.flush();
                        } else {
                            outWriter.write("-ERR Missing argument\r\n");
                            outWriter.flush();
                        }
                    } else if (args.length > 0 && args[0].equals("GET")) {
                        if (args.length > 1) {
                            String value = GET(args[1]);
                            if (value == null) {
                                outWriter.write("$-1\r\n");
                            } else {
                                outWriter.write("$" + value.length() + "\r\n" + value + "\r\n");
                            }
//                            outWriter.write("+" + value + "\r\n");
                            outWriter.flush();
                        } else {
                            outWriter.write("-ERR Missing argument\r\n");
                            outWriter.flush();
                        }
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

    private void SET(String[] args) {
        // Implement the SET command here
        if (keyValueStore.containsKey(args[0])) {
            keyValueStore.replace(args[0], args[1]);
        } else {
            keyValueStore.put(args[0], args[1]);
        }
    }

    private String GET(String key) {
        return keyValueStore.get(key);
    }
}
