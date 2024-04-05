package client;

import prtc.Request;
import prtc.Response;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.lang.Thread.sleep;

public class MultiUserConcurrentRequestSimulator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    public static void main(String[] args) {
        // 设置服务器地址和端口
        String address = "localhost";
        int port = 8600;

        // 设置用户数量和每个用户发出请求的次数
        int numUsers = 300;
        int requestsPerUser = 2000;

        // 创建多个用户并发发送请求
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < numUsers; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    // 创建一个客户端实例
                    Client client = new Client();
                    client.address = address;
                    client.port = port;

                    // 连接服务器
                    client.connect();

                    // 发送请求
                    for (int j = 0; j < requestsPerUser; j++) {
                        Request reqHdl = new Request();
                        String rs = reqHdl.createAddRequest(RandomString(), RandomStringArray());
                        CompletableFuture<String> rer = client.sendRequest(rs);
                        System.out.println("User " + Thread.currentThread().getName() + " - Response:\n " + (rer.join()));
                    }

                    // 断开连接
                    client.disconnect();
                } catch (IOException e) {
                    System.out.println("Failed to connect to the server.");
                }
            });
            futures.add(future);
        }

        // 等待所有用户的请求完成
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOf.join();
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
