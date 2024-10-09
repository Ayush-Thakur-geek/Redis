package handlers;

import javax.xml.crypto.dsig.keyinfo.KeyValue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientHandler implements Runnable {
    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final Socket clientSocket;
    private static String dir;
    private static String dbfilename;
    private static final ConcurrentHashMap<String, String> keyValueStore = new ConcurrentHashMap<>();
    private static final Map<String, Command> commandMap = new HashMap<>();

    static {
        commandMap.put("PING", ClientHandler::handlePing);
        commandMap.put("ECHO", ClientHandler::handleEcho);
        commandMap.put("SET", ClientHandler::handleSet);
        commandMap.put("GET", ClientHandler::handleGet);
        commandMap.put("CONFIG", ClientHandler::handleConfig);
        commandMap.put("KEYS", ClientHandler::handleKeys);
    }

    public ClientHandler(Socket clientSocket, String dir, String dbfilename) {
        this.clientSocket = clientSocket;
        ClientHandler.dir = dir;
        ClientHandler.dbfilename = dbfilename;
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
            if (args.length == 5) {
                if (args[3].equalsIgnoreCase("PX")) {
                    long delay = Long.parseLong(args[4]);
                    executorService.schedule(() -> keyValueStore.remove(args[1]), delay, TimeUnit.MILLISECONDS);
                } else {
                    writeError(outWriter, "Invalid argument");
                }
            } else if (args.length == 4) {
                writeError(outWriter, "Invalid argument");
            }
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

    private static void handleConfig(String[] args, OutputStreamWriter outWriter) throws IOException {
        if (args.length > 1) {
            if (args[1].equals("GET")) {
                String param = args[2].toLowerCase();
                String value = null;
                if (param.equals("dir")) {
                    value = dir;
                } else if (param.equals("dbfilename")) {
                    value = dbfilename;
                }
                if (value != null) {
                    writeResponse(outWriter, "*2\r\n$" + param.length() + "\r\n" + param + "\r\n$" + value.length() + "\r\n" + value + "\r\n");
                } else {
                    writeError(outWriter, "Unknown parameter");
                }
            } else {
                writeError(outWriter, "Unknown subcommand or wrong number of arguments");
            }
        } else {
            writeError(outWriter, "Missing argument");
        }
    }

    private static void handleKeys(String[] args, OutputStreamWriter outWriter) throws IOException {
        if (args.length > 1) {
            String pattern = args[1];
            int count = 0;
            StringBuilder response = new StringBuilder();
            for (String key : keyValueStore.keySet()) {
                if (key.matches(pattern.replace("*", ".*"))) {
                    response.append("$").append(key.length()).append("\r\n").append(key).append("\r\n");
                    count++;
                }
            }
            response.insert(0, "*" + count + "\r\n");
            writeResponse(outWriter, response.toString());
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