package server;
public class ServerDriver {
    public static final int DefaultPORT = 8888;

    public static void main(String[] args) {
        int port = DefaultPORT;
        String dictFile = null;
        String DEFAULTFILE = "dictionary.txt";

        if (args.length < 2) {
            if (args.length == 1) {
                dictFile = DEFAULTFILE;
                ServerLogger.logGeneralErr(STR."Dictionary file not specified, using default file: ./\{DEFAULTFILE}");
            }else {
                ServerLogger.logGeneralErr("Expected arguments: “port” “Dict file PATH”");
                System.exit(1);
            }
        }

        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            ServerLogger.logInvalidArgumentErr(args[0]);
            System.exit(1);
        }
        Dict dict = new Dict(dictFile);
//        Dict = new Dict("dictionary.txt");
        System.out.println(dict.search("banana"));
        System.out.println(dict.add("appleLLL", "a fruit"));
        System.out.println(dict.update("appleLLL", "a kind of fruit"));
        dict.printDictionaryInfo();
        dict.close();
    }
}
