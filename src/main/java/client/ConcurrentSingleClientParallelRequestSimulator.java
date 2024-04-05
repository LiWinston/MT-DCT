package client;

import prtc.Request;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.concurrent.CompletableFuture;

public class ConcurrentSingleClientParallelRequestSimulator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    public static void main(String[] args) {
        // 创建一个客户端实例
        Client client = new Client();
        client.address = "localhost"; // 设置服务器地址
        client.port = 8600; // 设置服务器端口

        // 连接服务器
        try {
            client.connect();
        } catch (IOException e) {
            System.out.println("Failed to connect to the server.");
            return;
        }

        for (int i = 0; i < 10; i++) {
            Thread.ofPlatform().start(() -> {
                Request reqHdl = new Request();
                String req = reqHdl.createAddRequest(RandomString(), RandomStringArray());
                CompletableFuture<String> res = client.sendRequest(req);
                System.out.println("Response: " + res.join());
            });
        }
//        String req = client.localReqHdl.createAddRequest(
//                RandomString(), RandomStringArray());
//        CompletableFuture<String> res = client.sendRequest(req);
//        System.out.println("Response: " + res.join());


        // 断开连接
        client.disconnect();
    }

    private static String[] RandomStringArray() {
        int size = (int) (Math.random() * 10);
        String[] arr = new String[size];
        for (int i = 0; i < size; i++) {
            arr[i] = RandomString();
        }
        return arr;
    }

    private static String RandomString() {
        int size = (int) (Math.random() * 25);
        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }
}
