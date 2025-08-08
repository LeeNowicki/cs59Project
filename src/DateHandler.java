import java.util.*;
// TODO: This does not accept dates without year included

public class DateHandler {

    private static final Map<String, Integer> monthValues = createMonthValues();

    private static Map<String, Integer> createMonthValues() {
        Map<String, Integer> map = new HashMap<>();
        map.put("Jan", 0); map.put("January", 0);
        map.put("Feb", 1); map.put("February", 1);
        map.put("Mar", 2); map.put("March", 2);
        map.put("Apr", 3); map.put("April", 3);
        map.put("May", 4);
        map.put("Jun", 5); map.put("June", 5);
        map.put("Jul", 6); map.put("July", 6);
        map.put("Aug", 7); map.put("August", 7);
        map.put("Sep", 8); map.put("September", 8);
        map.put("Oct", 9); map.put("October", 9);
        map.put("Nov", 10); map.put("November", 10);
        map.put("Dec", 11); map.put("December", 11);
        return map;
    }

    public static Date getTimeNoDate(String timeString) {
        Date today = new Date();
        String[] hourTime = timeString.split(":");

        return new Date(today.getYear(), today.getMonth(), today.getDate(), Integer.parseInt(hourTime[0]), Integer.parseInt(hourTime[1]));
    }

    public static Date getDefaultEndTime(Date startTime) {
        return new Date(startTime.getYear(), startTime.getMonth(), startTime.getDate(), startTime.getHours() + 1, startTime.getMinutes());
    }

    public static Date getFromNumericDate(String numericDate) {
        Date today = new Date();
        String[] monthDayYear = numericDate.split("/");

        return new Date(Integer.parseInt(monthDayYear[2]) - 1900,
                Integer.parseInt(monthDayYear[0]) - 1,
                Integer.parseInt(monthDayYear[1]),
                today.getHours(), today.getMinutes());
    }

    public static Date getFromNumericDate(String numericDate, String Time) {
        String[] hourTime = Time.split(":");
        String[] monthDayYear = numericDate.split("/");

        return new Date(Integer.parseInt(monthDayYear[2]) - 1900,
                Integer.parseInt(monthDayYear[0]) - 1,
                Integer.parseInt(monthDayYear[1]),
                Integer.parseInt(hourTime[0]),
                Integer.parseInt(hourTime[1]));
    }

    public static Date getFromDate(String dateString) {
        Date today = new Date();
        String[] monthDayYear = dateString.split(" ");
        System.out.println(monthDayYear[0]);
        monthDayYear[0] = (DateHandler.monthValues.get((monthDayYear[0]))).toString();

        if (monthDayYear.length == 3) {
            return new Date(Integer.parseInt(monthDayYear[2]) - 1900,
                    Integer.parseInt(monthDayYear[0]),
                    Integer.parseInt(monthDayYear[1]),
                    today.getHours(), today.getMinutes());
        } else {
            return new Date(today.getYear(),
                    Integer.parseInt(monthDayYear[0]),
                    Integer.parseInt(monthDayYear[1]),
                    today.getHours(), today.getMinutes());
        }

    }

    public static Date getFromDate(String dateString, String Time) {
        Date today =  new Date();
        String[] hourTime = Time.split(":");
        String[] monthDayYear = dateString.split(" ");
        monthDayYear[0] = (DateHandler.monthValues.get((monthDayYear[0]))).toString();

        if (monthDayYear.length == 3) {
            return new Date(Integer.parseInt(monthDayYear[2]) - 1900,
                    Integer.parseInt(monthDayYear[0]),
                    Integer.parseInt(monthDayYear[1]),
                    Integer.parseInt(hourTime[0]),
                    Integer.parseInt(hourTime[1]));
        } else {
            return new Date(today.getYear(),
                    Integer.parseInt(monthDayYear[0]),
                    Integer.parseInt(monthDayYear[1]),
                    Integer.parseInt(hourTime[0]),
                    Integer.parseInt(hourTime[1]));
        }
    }

    public static void main(String[] args) {
        // Class "Arabic" 14:30 to 19:00 Aug 14 2023
        System.out.println(DateHandler.getFromNumericDate("5/30/2025"));
        System.out.println(DateHandler.getFromNumericDate("2/20/1997", "12:00"));
        System.out.println(DateHandler.getFromDate("August 12 2024"));
        System.out.println(DateHandler.getFromDate("August 12 2024", "12:00"));
    }
}
