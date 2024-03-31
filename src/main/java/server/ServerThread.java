package server;

import prtc.Request;
import prtc.Response;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Executors;

public class ServerThread implements Runnable {
    //General listener monitoring ALL clients, employing single virtual thread to handle each request
    private final Dict dict;
    private final Socket socket;
    BufferedReader b_iStream;

    Request localReqHdl = new Request();

    public ServerThread(Socket clientSocket, Dict dict) {
        this.dict = dict;
        this.socket = clientSocket;
    }

    @Override
    public void run() {
        try (DataInputStream in = new DataInputStream(socket.getInputStream());
             BufferedReader b_iStream = new BufferedReader(new InputStreamReader(in));
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                while (true) {
//                System.out.println("Waiting for request...");
                    String req = b_iStream.readLine();
                    System.out.println("Request received: " + req);
                    // Decode the request
                    Request.Action action = localReqHdl.getAction(req);

                    // Process the request
                    if (action != null) {
                        String word = localReqHdl.getWord(req);
                        switch (action) {
                            case ADD:
                                executor.submit(() -> {
                                    String meanings = localReqHdl.getMeanings(req);
                                    try {
                                        Response res = dict.add(word, meanings);
//                                        out.writeChars (res.getMessage());
                                        out.writeUTF(res.toResponse() + "\n");
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                                break;
                            case DELETE:
                                executor.submit(() -> {
                                    Response res = dict.delete(word);
                                    try {
                                        out.writeUTF(res.toResponse() + "\n");
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                                break;
                            case UPDATE:
                                executor.submit(() -> {
                                    String meanings = localReqHdl.getMeanings(req);
                                    Response res = dict.update(word, meanings);
                                    try {
                                        out.writeUTF(res.toResponse() + "\n");
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                                break;
                            case SEARCH:
                                executor.submit(() -> {
                                    Response res = dict.search(word);
                                    try {
                                        out.writeUTF(res.toResponse() + "\n");
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                                break;
                            default:
                                out.writeUTF("Invalid request\n");
                        }
                    }
                    dict.saveToFile();
                }
            }
        } catch (IOException e) {
//            throw new RuntimeException(e);
            System.out.println("Err: Lost connection to Client " + socket.getRemoteSocketAddress());
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("Err: Unable to close the connection");
            }
        }
    }
}
