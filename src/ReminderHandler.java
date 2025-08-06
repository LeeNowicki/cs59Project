import org.json.*;
import java.util.Date;
import java.util.HashSet;

public class ReminderHandler {
    private static JSONObject singleObject;
    private static HashSet<JSONObject> allObjects = new HashSet<>();

    public static void createReminder(String reminderString, Date date) {
        singleObject = new JSONObject();
        singleObject.put("Reminder", reminderString);
        singleObject.put("Date", date);
        allObjects.add(singleObject);
    }

    public static JSONObject getSingleObject() {
        return singleObject;
    }

    public static HashSet<JSONObject> getAllObjects() {
        return allObjects;
    }

    public static void main(String[] args) {
        Date testDate = DateHandler.getFromNumericDate("8/6/2025");
        Date testDate2 = DateHandler.getFromNumericDate("1/25/1925", "14:22");
        ReminderHandler.createReminder("Get laundry", testDate);
        ReminderHandler.createReminder("Feed fish", testDate2);
        System.out.println(getSingleObject());
        System.out.println(getAllObjects());
    }
}
