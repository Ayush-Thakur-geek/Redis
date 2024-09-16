package handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private static final ConcurrentHashMap<String, String> keyValueStore = new ConcurrentHashMap<>();
    private static final Map<String, Command> commandMap = new HashMap<>();

    static {
        commandMap.put("PING", ClientHandler::handlePing);
        commandMap.put("ECHO", ClientHandler::handleEcho);
        commandMap.put("SET", ClientHandler::handleSet);
        commandMap.put("GET", ClientHandler::handleGet);
    }

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

                    if (args.length > 0) {
                        Command command = commandMap.get(args[0].toUpperCase());
                        if (command != null) {
                            command.execute(args, outWriter);
                        } else {
                            writeError(outWriter, "Unknown command");
                        }
                    }
                }
            }

            System.out.println("Client disconnected, closing connection.");
        } catch (IOException e) {
            System.out.println("IOException in processInfo: " + e.getMessage());
        }
    }

    private static void handlePing(String[] args, OutputStreamWriter outWriter) throws IOException {
        writeResponse(outWriter, "+PONG");
    }

    private static void handleEcho(String[] args, OutputStreamWriter outWriter) throws IOException {
        if (args.length > 1) {
            writeResponse(outWriter, "+" + args[1]);
        } else {
            writeError(outWriter, "Missing argument");
        }
    }

    private static void handleSet(String[] args, OutputStreamWriter outWriter) throws IOException {
        if (args.length > 2) {
            keyValueStore.put(args[1], args[2]);
            writeResponse(outWriter, "+OK");
        } else {
            writeError(outWriter, "Missing argument");
        }
    }

    private static void handleGet(String[] args, OutputStreamWriter outWriter) throws IOException {
        if (args.length > 1) {
            String value = keyValueStore.get(args[1]);
            if (value == null) {
                writeResponse(outWriter, "$-1");
            } else {
                writeResponse(outWriter, "$" + value.length() + "\r\n" + value);
            }
        } else {
            writeError(outWriter, "Missing argument");
        }
    }

    private static void writeResponse(OutputStreamWriter outWriter, String response) throws IOException {
        outWriter.write(response + "\r\n");
        outWriter.flush();
    }

    private static void writeError(OutputStreamWriter outWriter, String error) throws IOException {
        outWriter.write("-ERR " + error + "\r\n");
        outWriter.flush();
    }

    @FunctionalInterface
    private interface Command {
        void execute(String[] args, OutputStreamWriter outWriter) throws IOException;
    }
}