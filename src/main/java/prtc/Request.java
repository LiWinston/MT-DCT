package prtc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Request {

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

    public static String getAction(String jsonRequest) {
        try {
            JSONObject jsonObject = new JSONObject(jsonRequest);
            return jsonObject.getString("action");
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

    public static String[] getMeanings(String jsonRequest) {
        try {
            JSONObject jsonObject = new JSONObject(jsonRequest);
            JSONArray meaningsArray = jsonObject.getJSONArray("meanings");
            String[] meanings = new String[meaningsArray.length()];
            for (int i = 0; i < meaningsArray.length(); i++) {
                meanings[i] = meaningsArray.getString(i);
            }
            return meanings;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
