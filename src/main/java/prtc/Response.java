package prtc;

public class Response {
    public Response(boolean b, String msg) {
        status = b ? Status.SUCCESS : Status.FAIL;
        message = msg;
        meanings = null;
    }
    public Response(boolean b, String msg, String meanings) {
        status = b ? Status.SUCCESS : Status.FAIL;
        message = msg;
        this.meanings = meanings;
    }

    public enum Status {
        SUCCESS,
        FAIL
    }
    final Status status;
    final String message;

    final String meanings;
    //A response handler, not response class
    public static String failResponse(String message) {
        return "{\"status\":\"fail\",\"message\":\"" + message + "\"}";
    }
    public static String simpleSuccessResponse(String message) {
        return "{\"status\":\"success\",\"message\":\"" + message + "\"}";
    }


    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getMeanings() {
        return meanings;
    }
}
