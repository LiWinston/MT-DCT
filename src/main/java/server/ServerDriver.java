package server;
public class ServerDriver {
    public static final int DefaultPORT = 8888;

    public static void main(String[] args) {
        int port = DefaultPORT;
        String dictFile = null;

        if (args.length < 2) {
            ServerLogger.logGeneralErr("Usage: java -jar server.jar <port> <dictionary file>");
            System.exit(1);
        }

        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            ServerLogger.logInvalidArgumentErr(args[0]);
            System.exit(1);
        }

        dictFile = args[1];
        Dict dict = new Dict(dictFile);
    }
}
