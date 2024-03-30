package prtc;

public class Response {
    //A response handler, not response class
    public static String failResponse(String message) {
        return "{\"status\":\"fail\",\"message\":\"" + message + "\"}";
    }
    public static String simpleSuccessResponse(String message) {
        return "{\"status\":\"success\",\"message\":\"" + message + "\"}";
    }

    public static String searchResponse(String word, String[] meanings) {
        StringBuilder response = new StringBuilder("{\"status\":\"success\",\"word\":\"" + word + "\",\"meanings\":[");
        for (String meaning : meanings) {
            response.append("\"").append(meaning).append("\",");
        }
        response.deleteCharAt(response.length() - 1);
        response.append("]}");
        return response.toString();
    }
    public static String addResponse(String word, String[] meanings) {
        StringBuilder response = new StringBuilder("{\"status\":\"success\",\"word\":\"" + word + "\",\"meanings\":[");
        for (String meaning : meanings) {
            response.append("\"").append(meaning).append("\",");
        }
        response.deleteCharAt(response.length() - 1);
        response.append("]}");
        return response.toString();
    }

    public static void parseResponse(String response) {
        switch (response.split(":")[1]){
            case "success", "fail":
                //TODO: log to caller

                break;
            default:
                System.out.println("Invalid response");
        }
    }
}
