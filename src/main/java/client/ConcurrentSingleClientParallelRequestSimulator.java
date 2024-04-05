package client;

import prtc.Request;
import prtc.Response;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.lang.Thread.sleep;

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

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // chuan行发送请求
        for (int i = 0; i < 1700; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                Request reqHdl = new Request();
                String rs = reqHdl.createAddRequest(RandomString(), RandomStringArray());
                CompletableFuture<String> rer = client.sendRequest(rs);
                System.out.println("Response: " + rer.join());
            });
            futures.add(future);

        }

        // 等待所有请求完成
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOf.join();

        // 断开连接
//        client.disconnect();
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
