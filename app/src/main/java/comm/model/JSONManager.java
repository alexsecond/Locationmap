package comm.model;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Alexander on 27/09/2017.
 */

public class JSONManager {
    public static LocationMap[] jsonToLocationMap(String jsonValue) {
        LocationMap[] locations = new Gson().fromJson(jsonValue, LocationMap[].class);
        return locations;
    }

    public static String locationMapToJson(LocationMap loc) {
        String jsonLocation = new Gson().toJson(loc);
        return jsonLocation;
    }

    public static JSONObject locationMapToJSONObject(LocationMap lm) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSONManager.locationMapToJson(lm));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static User instanceUserFromJson(String jsonUser) {
        User res = null;
        try {
            JSONObject json = new JSONObject(jsonUser);
            String idUser = json.getString("ID_USER");
            String name = json.getString("NAME_USER");
            res = new User(Integer.parseInt(idUser), name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }
}
