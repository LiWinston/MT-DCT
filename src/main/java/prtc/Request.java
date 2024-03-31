package prtc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Request {

//    private static final Request instance = new Request();


//    public static Request getInstance() {
//        return instance;
//    }
    public enum Action {
        SEARCH,
        ADD,
        DELETE,
        UPDATE
    }

    public String createSearchRequest(String word) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("action", "search");
            jsonRequest.put("word", word);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest.toString();
    }

    public String createAddRequest(String word, String[] meanings) {
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

    public String createDeleteRequest(String word) {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("action", "delete");
            jsonRequest.put("word", word);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRequest.toString();
    }

    public String createUpdateRequest(String word, String[] meanings) {
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

    public Action getAction(String jsonRequest) {
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

    public String getWord(String jsonRequest) {
        try {
            JSONObject jsonObject = new JSONObject(jsonRequest);
            return jsonObject.getString("word");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getMeanings(String jsonRequest) {
        try {
            JSONObject jsonObject = new JSONObject(jsonRequest);
            JSONArray meaningsArray = jsonObject.getJSONArray("meanings");
            //add ; to separate meanings
            for (int i = 0; i < meaningsArray.length(); i++) {
                meaningsArray.put(i, meaningsArray.getString(i) + ";");
            }
            return meaningsArray.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
