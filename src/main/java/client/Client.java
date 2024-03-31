package client;

import prtc.Request;
import prtc.Response;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Future;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Client implements Runnable {
    BufferedReader b_iStream;
    private String address;
    private int port;
    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket;
    Request localReqHdl = new Request();

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Arg format : address + port");
            return;
        }

        Client client = new Client();
        Thread clientThread = new Thread(client);

        client.address = args[0];
        client.port = 8500;

        try {
            client.port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Format err : integer required for port number");
        }

//        new UI(client);
        try {
            client.connect();
        } catch (UnknownHostException e) {
            System.out.println("server address cannot be reached");
        } catch (IllegalArgumentException e) {
            System.out.println("port number over range");
        } catch (ConnectException e) {
            System.out.println("Connection declined or no server found, consider port number availability");
        } catch (IOException e) {
            System.out.println("The server is down, closing now");
        }
        if (client.socket != null) {
            System.out.println("Client socket not null");
            if (client.socket.isConnected()) {
//            new UI(client);

                clientThread.start();
            }
        }


    }

    protected void connect() throws IllegalArgumentException, IOException {
        socket = new Socket(address, port);
        if (socket.isConnected()) {
            System.out.println("Connected to server");
        }
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        b_iStream = new BufferedReader(new InputStreamReader(in));

    }

    protected void disconnect() {
        try {
//            in.close();
//            out.close();
            socket.close();
            in = null;
            out = null;
            socket = null;
        } catch (IOException e) {
            System.out.println("Err: Unable to close the connection");
        }
    }


    protected CompletableFuture<String> sendRequest(String s) {
        CompletableFuture<String> future = new CompletableFuture<>();
        try {
            out.writeBytes(STR."\{s}\n");
//            out.flush();
            // 异步接收服务器的响应
            CompletableFuture.runAsync(() -> {
                try {
//                    String response = b_iStream.readLine();
                    String response = b_iStream.readLine();
                    future.complete(Response.getStatusString(response) + ": " + Response.getMessageString(response) + " " + Response.getMeaningsString(response));
                } catch (IOException e) {
                    future.completeExceptionally(e);
                }
            });
        } catch (IOException e) {
            future.completeExceptionally(e);
//            Thread.currentThread().interrupt();
        }
        return future;
    }

    @Override
    public void run() {
        System.out.println("Client running");
        while (true) {
            try {
                String req = localReqHdl.createAddRequest("apple", new String[]{"a fruit; a kind of fruit"});
                CompletableFuture<String> res = sendRequest(req);
                System.out.println(STR."Request sent: \{req}");
                System.out.println(res.get());
                String req2 = localReqHdl.createDeleteRequest("apple");
                System.out.println(STR."Request sent: \{req2}");
                CompletableFuture<String> res2 = sendRequest(req2);
                System.out.println(res2.get());

            } catch (ExecutionException | InterruptedException e) {
                System.out.println("Err: connection lost, closing now");
                Thread.currentThread().interrupt();
                disconnect();
//                throw new RuntimeException(e);
            }
        }
    }
}
