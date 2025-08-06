import java.util.Arrays;
import java.util.Date;

public class DateHandler {

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
        System.out.println();

        return new Date(Integer.parseInt(monthDayYear[1]),
                Integer.parseInt(monthDayYear[0]),
                Integer.parseInt(monthDayYear[2]),
                today.getHours(), today.getMinutes());
    }

    public static Date getFromNumericDate(String numericDate, String Time) {
        return new Date();
    }

    public static Date getFromDate(String dateString) {
        return new Date();
    }

    public static Date getFromDate(String dateString, String Time) {
        return new Date();
    }

    public static void main(String[] args) {
        System.out.println(DateHandler.getFromNumericDate("2/20/1997"));
    }
}
