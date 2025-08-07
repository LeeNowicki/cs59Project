import org.json.*;

import java.util.Date;
import java.util.HashMap;
// TODO: Write other handlers for reminders and invites and have them also get zipped up
public class JSONHandler {
    private static String objectName = new String();
    private static JSONObject singleObject = new JSONObject();
    private static HashMap<String, JSONObject> allObjects = new HashMap<>();

    // TODO: make functions: alreadyexists in hashmap (redundant with get),
    public static void createThisObject() {
        singleObject = new JSONObject();
        objectName = new String();

        singleObject.put("Start_Times", new JSONArray());
        singleObject.put("End_Times", new JSONArray());

        singleObject.put("RepeatDay", new JSONArray());
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
        JSONObject toCancel = allObjects.get(eventName);
        if(toCancel != null){
            int index = 0;
            JSONArray dates = toCancel.getJSONArray("Start_Times");
            while(index < dates.length()){
                Date dateToCancel = (Date)dates.get(index);
                if(date.getDate() == dateToCancel.getDate()
                        && date.getMonth() == dateToCancel.getMonth()
                        && date.getYear() == dateToCancel.getYear()){
                    dates.remove(index);
                    toCancel.getJSONArray("End_Times").remove(index);
                }
                else {
                    index++;
                }
            }

        }
        return;
    }
    public static void cancel(String eventName){
        allObjects.remove(eventName);
    }

    // This will add an "invitees" array to the JSON Object if it doesn't already
    // exist, then add a JSONArray (essentially a tuple) to invitees containing the
    // invitee's name and the date of the event.
    public static void invite(String invitee, String eventName, Date date) {
        JSONObject event = allObjects.get(eventName);
        if (event != null) {
            if (event.get("invitees") == null) {
                event.put("invitees", new JSONArray());
            } // TODO: throw exception
            JSONArray nameDate = new JSONArray();
            nameDate.put(invitee);
            nameDate.put(date);
            event.getJSONArray("invitees").put(nameDate);
        }
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
        System.out.println(makeObject().toString(4));
    }
}
