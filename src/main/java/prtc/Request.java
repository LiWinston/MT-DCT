package prtc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Request {

    private static final Request instance = new Request();

    private Request() {}

    public static Request getInstance() {
        return instance;
    }
    public enum Action {
        SEARCH,
        ADD,
        DELETE,
        UPDATE
    }

    public static String createSearchRequest(String word) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("action", "search");
            jsonRequest.put("word", word);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest.toString();
    }

    public static String createAddRequest(String word, String[] meanings) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("action", "add");
            jsonRequest.put("word", word);
            JSONArray meaningsArray = new JSONArray(meanings);
            jsonRequest.put("meanings", meaningsArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest.toString();
    }

    public static String createDeleteRequest(String word) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("action", "delete");
            jsonRequest.put("word", word);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest.toString();
    }

    public static String createUpdateRequest(String word, String[] meanings) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("action", "update");
            jsonRequest.put("word", word);
            JSONArray meaningsArray = new JSONArray(meanings);
            jsonRequest.put("meanings", meaningsArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest.toString();
    }

    public static Action getAction(String jsonRequest) {
        try {
            JSONObject jsonObject = new JSONObject(jsonRequest);
            try {
                return Action.valueOf(jsonObject.getString("action").toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid action from request");
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getWord(String jsonRequest) {
        try {
            JSONObject jsonObject = new JSONObject(jsonRequest);
            return jsonObject.getString("word");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getMeanings(String jsonRequest) {
        try {
            JSONObject jsonObject = new JSONObject(jsonRequest);
            JSONArray meaningsArray = jsonObject.getJSONArray("meanings");
            return meaningsArray.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
