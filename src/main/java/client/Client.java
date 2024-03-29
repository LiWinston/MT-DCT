package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private String address;
    private int port;
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
            System.out.println("second argument must be integer");
        }

//        new UI(client);
        try {
            client.connect();
        } catch (UnknownHostException e) {
            System.out.println("server address cannot be reached");
        } catch (IllegalArgumentException e) {
            System.out.println("port number must be between 0 and 65535");
        } catch (ConnectException e) {
            System.out.println("please try another port number");
        } catch (IOException e) {
            System.out.println("The server is down, closing now");
        }
    }

    protected void connect() throws IllegalArgumentException, IOException {
        Socket socket = new Socket(address, port);
        if (socket != null) {
            System.out.println("Connected to server");
        }
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    protected String sendRequestForResponse(String s) {
        try {
            out.writeUTF(s);
            return in.readUTF();
        } catch (IOException e) {
            return "Error//Unable to connect to the server, please restart the client";
        }
    }

}
