import org.json.*;

import java.io.File;
import java.io.FileWriter;
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
        singleObject.put("invitees", new JSONArray());
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
        allObjects.put(singleObject.get("Name").toString(), singleObject);
    }

    public static void addToMap(JSONObject toAdd) {
        allObjects.put(toAdd.get("Name").toString(), toAdd);
    }

    // TODO: fill out the functions
    public static void cancel(String eventName, Date date){
        JSONObject toCancel = allObjects.get(eventName);
        if(toCancel != null){
            int index = 0;
            JSONArray dates = toCancel.getJSONArray("Start_Times");
            while(index < dates.length()){
                Date dateToCancel = new Date(dates.get(index).toString());
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

    public static void printFile(String filename) {
        try{
            FileWriter jsonWriter = new FileWriter(filename);
            jsonWriter.write(makeObject().toString(4));
            jsonWriter.close();
        }
        catch (Exception e){
            System.out.println("Error occured when writing to file:" + e);
        }
    }

    public static void printInviteFiles(String directory) {
        try{
            HashMap<String, JSONObject> inviteMap = new HashMap<>();
            for(JSONObject obj : allObjects.values()){
                JSONArray invitees = obj.getJSONArray("invitees");
                if(!invitees.isEmpty()){
                    for(int ind = 0; ind < invitees.length(); ind++){
                        //write to file
                        // TODO: currently no checks if the specific instance of the event exists
                        String name = invitees.getJSONArray(ind).get(0).toString();
                        Date date = new Date(invitees.getJSONArray(ind).get(1).toString());

                        JSONObject inviteParent = inviteMap.get(name);

                        if(inviteParent == null){
                            //new person
                            inviteParent = new JSONObject();
                        }
                        else {
                             inviteParent = inviteMap.get(name);
                        }

                        //Find out if this is a repeat event
                        JSONObject invite;
                        try {
                            //this throws an error if it does not exist
                            invite = inviteParent.getJSONObject(obj.get("Name").toString());
                        }
                        catch (Exception e) {
                            invite = new JSONObject();
                            invite.put("Type", obj.get("Type"));
                            invite.put("Name", obj.get("Name"));
                            invite.put("Start_Times", new JSONArray());
                            invite.put("End_Times", new JSONArray());
                            invite.put("invitees", new JSONArray());
                            invite.put("RepeatDay", new JSONArray());
                        }
                        //This is for a new event, so we can make a new invite


                        //Loop through the all the start times and add the ones that occur on the same day
                        JSONArray dates = obj.getJSONArray("Start_Times");
                        int index = 0;

                        while(index < dates.length()){
                            Date dateToInvite = new Date(dates.get(index).toString());
                            if(date.getDate() == dateToInvite.getDate()
                                    && date.getMonth() == dateToInvite.getMonth()
                                    && date.getYear() == dateToInvite.getYear()){
                                invite.getJSONArray("Start_Times").put(dateToInvite);
                                invite.getJSONArray("End_Times")
                                        .put(obj.getJSONArray("End_Times").get(index));


                            }

                            index++;

                        }

                        //Invite json complete - add to the map
                        //Wrap the invite one more time, or add it
                        inviteParent.put(invite.get("Name").toString(), invite);
                        inviteMap.put(name, inviteParent);

                    }
                }
            }

            //Now iterate through this newly made hashmap and write them to a
            for(String key : inviteMap.keySet()) {
                //have to cut out the escaped double quotes in the names
                String filename = directory + key.replaceAll("^\\\"+|\\\"$","") + ".json";
                //System.out.println(filename);
                File inviteFile = new File(filename);
                inviteFile.createNewFile();
                FileWriter inviteWriter = new FileWriter(filename);
                inviteWriter.write(inviteMap.get(key).toString(4));
                inviteWriter.close();
                //System.out.println(key + "is invited to:");
                //System.out.println(inviteMap.get(key).toString(4));
            }
        }
        catch (Exception e){
            System.out.println("Error occured when writing invitation:" + e);
        }
    }


}
