package server;

import server.UI.MCDCT_ServerWindow;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class ServerDriver{
    public static final int DefaultPORT = 8500;

    boolean allowNewConnection = true;
    public static void main(String[] args) {
        ServerDriver serverDriver = new ServerDriver();
        int port = DefaultPORT;
        String dictFile = null;
        String DEFAULT_FILE = "dictionary.txt";
        int connectedClients = 0;

        if (args.length < 2) {
            if (args.length == 1) {
                dictFile = DEFAULT_FILE;
                ServerLogger.logGeneralErr(STR."Dictionary file not specified, using default file: ./\{DEFAULT_FILE}");
            } else {
                ServerLogger.logGeneralErr("Expected arguments: “port” “Dict file PATH”");
                System.exit(1);
            }
        } else {
            dictFile = args[1];
        }
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            ServerLogger.logInvalidArgumentErr(args[0]);
            System.exit(1);
        }

        Dict dict = new Dict(dictFile);


        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println(STR."Server started listening on port: \{port}");
//            MCDCT_ServerWindow window = new MCDCT_ServerWindow(serverDriver);
//            window.setVisible(true);
//            serverSocket.setSoTimeout(90000);
//            dict.add("server", serverSocket.toString());
            while (true) {
                    Socket clientSocket = serverSocket.accept();

                    ServerThread serverThread = new ServerThread(clientSocket, dict);
                    System.out.println(STR."Client connected: \{clientSocket.getRemoteSocketAddress()}");
                    Thread.Builder.OfVirtual thread = Thread.ofVirtual();
                    thread.start(serverThread);
                    connectedClients++;
            }
        } catch (IOException ioe) {
            ServerLogger.logGeneralErr(STR."Could not listen on port: \{port}");
            System.exit(1);
        } catch (IllegalArgumentException iae) {
            ServerLogger.logGeneralErr(STR."Port number out of range: \{port}");
            System.exit(1);
        }
//        dict.close();
    }

    public void setAllowNewConnection(boolean selected) {
        allowNewConnection = selected;
        System.out.println(STR."allowNewConnection set to: \{selected}");
    }
}
