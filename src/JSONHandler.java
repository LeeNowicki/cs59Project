import org.json.*;

import java.util.Date;
import java.util.HashMap;
// TODO: Write other handlers for reminders and invites and have them also get zipped up
public class JSONHandler {
    private static String objectName = new String();
    private static JSONObject singleObject = new JSONObject();
    private static HashMap<String, JSONObject> allObjects = new HashMap<>();

    // TODO: make functions: already exists in hashmap,
    public static void createThisObject() {
        singleObject = new JSONObject();
        objectName = new String();

        singleObject.put("Start_Times", new JSONArray());
        singleObject.put("End_Times", new JSONArray());
    }

    public static JSONObject getSingleObject() {
        return singleObject;
    }

    public static void setSingleObject(JSONObject singleObject1){
        singleObject = singleObject1;
    }

    public static HashMap<String, JSONObject> getAllObjects() {
        return allObjects;
    }

    public static void addStartTime(Date date){
        singleObject.getJSONArray("Start_Times").put(date);
    }

    public static void addEndTime(Date date){
        singleObject.getJSONArray("End_Times").put(date);
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

    // TODO: fill out the functions
    public static void cancel(String eventName, Date date){

    }
    public static void cancel(String eventName){

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

    public static void printAll() {
        System.out.println(makeObject());
    }
}
