package client;

import prtc.Request;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

import static java.lang.System.exit;

public class Client implements Runnable {
    BufferedReader b_iStream;
    String address;
    int port;
    Request localReqHdl = new Request();
    Socket socket;
    private UI ui;
    private DataInputStream in;
    private DataOutputStream out;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Arg format : address + port");
            return;
        }

        Client client = new Client();

        client.address = args[0];
        client.port = 8500;

        try {
            client.port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Format err : integer required for port number");
        }
        client.ui = new UI(client);


    }

    protected void connect() throws IllegalArgumentException, IOException {
        socket = new Socket(address, port);
        if (socket.isConnected()) {
            System.out.println("Connected to server: " + address + ":" + port);
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
                    String response = in.readUTF();
                    future.complete(response);
//                    future.complete(Response.getStatusString(response) + ": " + Response.getMessageString(response) + " " + Response.getMeaningsString(response));
                } catch (IOException e) {
                    future.completeExceptionally(e);
                }
            });
        } catch (IOException e) {
            JOptionPane.showMessageDialog(ui,
                    e.getMessage(),
                    "Connection error, please restart the client.",
                    JOptionPane.WARNING_MESSAGE);
            try {
//                Thread.sleep(1000);
                ui.dispose();
                exit(1);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return future;
    }

    @Override
    public void run() {
//        System.out.println("Client running");
//        ArrayList<String> Str = new ArrayList<>();
//        Str.add("ININININI;");
//        while (true) {
//
//            String req1 = localReqHdl.createSearchRequest("apple");
//            CompletableFuture<String> res1 = sendRequest(req1);
//            System.out.println(STR."Request sent: \{req1}");
//            System.out.println(res1.join());
//
//
//            String req2 = localReqHdl.createUpdateRequest("apple", Str.toArray(new String[0]));
//            Str.add(new Random().nextInt(100) + ";");
//            CompletableFuture<String> res = sendRequest(req2);
//            System.out.println(STR."Request sent: \{req2}");
//            System.out.println(res.join());
//
//            String req3 = localReqHdl.createDeleteRequest("apple");
//            CompletableFuture<String> res3 = sendRequest(req3);
//            System.out.println(STR."Request sent: \{req3}");
//            System.out.println(res3.join());
//
//            String req4 = localReqHdl.createAddRequest("apple", Str.toArray(new String[0]));
//            CompletableFuture<String> res4 = sendRequest(req4);
//            System.out.println(STR."Request sent: \{req4}");
//            System.out.println(res4.join());
//
//        }
    }

    public void argError(String msg) {
        System.err.println("Argument error: " + msg);
        exit(1);
    }


    public void connectionError(String msg) {
        JOptionPane.showMessageDialog(ui,
                "Connection error: " + msg,
                "Error",
                JOptionPane.ERROR_MESSAGE);
        exit(1);
    }


    public void formatWarning(String msg) {
        JOptionPane.showMessageDialog(ui,
                "Format warning: " + msg,
                "Warning",
                JOptionPane.WARNING_MESSAGE);
    }


    public void FailDialog(String msg, String title) {
        JOptionPane.showMessageDialog(ui,
                msg,
                "Fail",
                JOptionPane.WARNING_MESSAGE);
    }


    public void successMessage() {
        JOptionPane.showMessageDialog(ui,
                "Thanks for contributing,",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
