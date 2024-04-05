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
        try (BufferedInputStream  in = new BufferedInputStream(socket.getInputStream());
             BufferedReader b_iStream = new BufferedReader(new InputStreamReader(in));
             BufferedOutputStream  out = new BufferedOutputStream(socket.getOutputStream())) {

            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                while (true) {
//                System.out.println("Waiting for request...");
                    String req = b_iStream.readLine();
                    System.out.println(STR."Request received: \{req}");
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
                                        dict.saveToFile();
                                        out.write((res.toResponse() + "\n").getBytes());
                                        out.flush();
                                        System.out.println(STR."Response sent: \{res.toResponse()}");
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                                break;
                            case DELETE:
                                executor.submit(() -> {
                                    Response res = dict.delete(word);
                                    dict.saveToFile();
                                    try {
                                        out.write((res.toResponse() + "\n").getBytes());
                                        out.flush();
                                        System.out.println(STR."Response sent: \{res.toResponse()}");
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                                break;
                            case UPDATE:
                                executor.submit(() -> {
                                    String meanings = localReqHdl.getMeanings(req);
                                    Response res = dict.update(word, meanings);
                                    dict.saveToFile();
                                    try {
                                        out.write((res.toResponse() + "\n").getBytes());
                                        out.flush();
                                        System.out.println(STR."Response sent: \{res.toResponse()}");
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                                break;
                            case SEARCH:
                                executor.submit(() -> {
                                    Response res = dict.search(word);
                                    try {
                                        out.write((res.toResponse() + "\n").getBytes());
                                        out.flush();
                                        System.out.println(STR."Response sent: \{res.toResponse()}");
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                                break;
                            default:
                                out.write("Invalid request\n".getBytes());
                                out.flush();
                        }
                    }
//                    dict.saveToFile();
                }
            }
        } catch (UTFDataFormatException ue){

        } catch(IOException e) {
//            throw new RuntimeException(e);
            System.out.println(STR."Err: Lost connection to Client \{socket.getRemoteSocketAddress()} Closing connection thread");
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("Err: Unable to close the connection");
            }
        }
    }
}
