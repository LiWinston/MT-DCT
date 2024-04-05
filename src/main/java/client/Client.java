package client;

import prtc.Request;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;

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
    private final java.util.concurrent.ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

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
            System.out.println(STR."Connected to server: \{address}:\{port}");
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
        return CompletableFuture.supplyAsync(() -> {
            try {
                out.writeBytes(STR + s + "\n");

                String response = in.readUTF();

                return response;
            } catch (IOException e) {
                System.out.println("Kazhele");

                int choice = JOptionPane.showConfirmDialog(ui,
                        " Connection error, press yes to retry, no to exit",
                        "Fail",
                        JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    try {
                        connect();
                        System.out.println("Kazhele 1");
                        JOptionPane.showMessageDialog(ui,
                                "Connection re-established",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        return sendRequest(s).join(); // 重新发送请求并等待结果
                    } catch (IOException ioException) {
                        System.out.println("Kazhele 2");
                        return connectionError(ioException.getMessage(), s).join();
                    }
                } else {
                    exit(1);
                    return null;
                }
            }
        }, executor);
    }


    @Override
    public void run() {
        //超时逻辑
//while (true) {
//            try {
//                connect();
//                break;
//            } catch (IOException e) {
//                connectionError(e.getMessage());
//            }
//        }
    }

    //非请求积压式重连 Non-request backlog reconnection
    public void connectionError(String msg) {
        int choice = JOptionPane.showConfirmDialog(ui,
                STR."Connection error: \{msg}, press yes to retry, no to exit",
                "Error",
                JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            try {
                connect();
                JOptionPane.showMessageDialog(ui,
                        "Connection established",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                connectionError(e.getMessage());
            }
        } else {
            exit(1);
        }
    }

    //请求积压 重连再发送 Request backlog reconnection
    public CompletableFuture<String> connectionError(String msg, String s) {
        int choice = JOptionPane.showConfirmDialog(ui,
                STR."Connection error: \{msg}, press yes to retry, no to exit",
                "Error",
                JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            try {
                connect();
                JOptionPane.showMessageDialog(ui,
                        "Connection re-established",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                return sendRequest(s);
            } catch (IOException e) {
                connectionError(e.getMessage(),s);
            }
        } else {
            exit(1);
        }
        return null;
    }

    public void formatWarning(String msg) {
        JOptionPane.showMessageDialog(ui,
                "Format warning: " + msg,
                "Warning",
                JOptionPane.WARNING_MESSAGE);
    }


    public void FailDialog(String msg) {
        JOptionPane.showMessageDialog(ui,
                msg,
                "Fail",
                JOptionPane.ERROR_MESSAGE);
    }
    public void FailDialog(String msg, String title) {
        JOptionPane.showMessageDialog(ui,
                msg,
                title,
                JOptionPane.WARNING_MESSAGE);
    }

    public void SuccessDialog(String msg, String title) {
        JOptionPane.showMessageDialog(ui,
                msg,
                title,
                JOptionPane.INFORMATION_MESSAGE);
    }
}
