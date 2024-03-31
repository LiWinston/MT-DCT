package prtc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Response{
    public Response(boolean b, String msg) {
        status = b ? Status.SUCCESS : Status.FAIL;
        message = msg;
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

    volatile String meanings = null;

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getMeanings() {
        return meanings;
    }

    public String toResponse() {
        JSONObject jsonResponse = new JSONObject();
        try {
            jsonResponse.put("status", status.toString());
            jsonResponse.put("message", message);

            if (meanings != null) {
                JSONArray meaningsArray = new JSONArray(meanings);
                jsonResponse.put("meanings", meaningsArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonResponse.toString();
    }

    public static String getStatusString(String res) {
        try {
            JSONObject jsonObject = new JSONObject(res);
            return jsonObject.getString("status");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getMessageString(String res) {
        try {
            JSONObject jsonObject = new JSONObject(res);
            return jsonObject.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getMeaningsString(String res) {
        try {
            JSONObject jsonObject = new JSONObject(res);
            return jsonObject.getString("meanings");
        } catch (JSONException e) {
            return null;
        }
    }


}
