package client;

public class ConcurrentClientSimulator {
    public static void main(String[] args) {
        //自动生成Client线程并随机生成请求
        for (int i = 0; i < 4; i++) {
            Client client = new Client();
            Client.main(new String[]{"localhost", "8500"});
        }
    }
}