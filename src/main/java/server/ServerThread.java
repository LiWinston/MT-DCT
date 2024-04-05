package server;

import prtc.Request;
import prtc.Response;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerThread implements Runnable {
    //General listener monitoring ALL clients, employing single virtual thread to handle each request
    private final Dict dict;
    private final Socket socket;
    BufferedReader b_iStream;
    Thread.Builder.OfVirtual reqReceiver;
    BlockingQueue<String> reqQueue = new LinkedBlockingQueue<>();

    Request localReqHdl = new Request();

    public ServerThread(Socket clientSocket, Dict dict) {
        this.dict = dict;
        this.socket = clientSocket;
        reqReceiver = Thread.ofVirtual();;
        reqReceiver.start(() -> {
            try(DataInputStream in = new DataInputStream(socket.getInputStream());
                BufferedReader b_iStream = new BufferedReader(new InputStreamReader(in));){
                this.b_iStream = b_iStream;
                while (true){
                    if(socket.isClosed()){
                        System.out.println(STR."Err: Connection to Client \{socket.getRemoteSocketAddress()} closed. Closing connection thread");
                        synchronized (reqReceiver){
                            try {
                                reqReceiver.wait();
                            } catch (InterruptedException e) {
                                System.out.println(STR."Err: Unable to wait for connection thread to close: \{e.getMessage()}");
                            }
                        }
                    }
                    String req = null;
                    try {
                        req = b_iStream.readLine();
                        reqQueue.put(req);
                    } catch (IOException e) {
                        System.out.println(STR."Err: Lost connection to Client \{socket.getRemoteSocketAddress()} Closing connection thread");
                        try {
                            socket.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            System.out.println("Err: Unable to close the connection");
                        }
                        Thread.currentThread().interrupt();
                    } catch (InterruptedException e) {//Queue put exception
                        System.out.println(STR."Err: Unable to put request in queue: \{e.getMessage()}");
                    }

                    System.out.println("Request received: " + req);
                }
            }catch (IOException e){
                System.out.println(STR."Err: Unable to get input stream from client socket: \{e.getMessage()}");
            }catch (NullPointerException e){
                System.out.println(STR."Err: Unable to create input stream from client socket: \{e.getMessage()}");
            }
        });
    }

    @Override
    public void run() {
        try (DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                while (true) {
//                System.out.println("Waiting for request...");
                    String req = reqQueue.take();
                    System.out.println(STR."Request Proccessing: \{req}");
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
                                        System.out.println("Response sent: " + res.toResponse());
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
                                        System.out.println("Response sent: " + res.toResponse());
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
                                        System.out.println("Response sent: " + res.toResponse());
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
                                        System.out.println("Response sent: " + res.toResponse());
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
            } catch (InterruptedException e) {//Queue take exception
                System.out.println(STR."Err: Unable to take request from queue: \{e.getMessage()}");
                throw new RuntimeException(e);
            }
        } catch (UTFDataFormatException _){

        } catch(IOException e) {
            System.out.println(STR."Err: Lost connection to Client \{socket.getRemoteSocketAddress()} Closing connection thread");
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("Err: Unable to close the connection");
            }
        }
    }

    public void notifyReqReceiver(){
        synchronized (reqReceiver){
            reqReceiver.notify();
        }
    }
}
