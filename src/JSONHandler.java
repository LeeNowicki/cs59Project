import org.json.*;

import java.util.HashMap;

public class JSONHandler {
    private static String objectName = new String();
    private static JSONObject singleObject = new JSONObject();
    private static HashMap<String, JSONObject> allObjects = new HashMap<>();

    public static void clearThisObject() {
        singleObject = new JSONObject();
        objectName = new String();
    }

    public static void setObjectName(String objectName1) {
        objectName = objectName1;
    }

    public static void put(String key, Object val){
        singleObject.put(key, val);
    }

    public static void addToMap() {
        allObjects.put(objectName, singleObject);
    }

    public static JSONObject getObject(String objectName) {
        return allObjects.get(objectName);
    }

    //Implement print for the objects
    //Implement a function to put them all in one object
    public static JSONArray makeArray() {
        JSONArray allArray = new JSONArray();
        for(JSONObject j : allObjects.values()){
            allArray.put(j);
        }

        return allArray;
    }

    public static JSONObject makeObject() {
        JSONObject collectedObject = new JSONObject();
        for(String j : allObjects.keySet()){
            collectedObject.put(j, allObjects.get(j));
        }

        return collectedObject;
    }
}
