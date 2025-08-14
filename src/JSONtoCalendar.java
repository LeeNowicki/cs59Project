import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;

public class JSONtoCalendar extends Application {

    private static JSONObject EVENTS = new JSONObject();

    public static void main(String[] args) {
        try {
            String raw = Files.readString(Path.of(args[0]), StandardCharsets.UTF_8);
            EVENTS = new JSONObject(raw);
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException(e);
        }
        launch(args);
    }

    public static void display(String pathname){
        try {
            String raw = Files.readString(Path.of(pathname), StandardCharsets.UTF_8);
            EVENTS = new JSONObject(raw);
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException(e);
        }

        launch(pathname);
    }

    public static void display(JSONObject calendar){
        try {
            EVENTS = calendar;
        }catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        HashMap<String, Calendar> types = new HashMap<>();

        int style = 0;
        for (String key : EVENTS.keySet()) {
            JSONObject event = EVENTS.getJSONObject(key);
            if (!types.containsKey(event.getString("Type"))) {
                String type = event.getString("Type");
                Calendar newCal = new Calendar(type);
                newCal.setStyle(Calendar.Style.getStyle(style));
                types.put(type, newCal);

                style++;
                if (style == 8) {
                    style = 0;
                }
            }
            Calendar currCal = types.get(event.getString("Type"));
            if (event.getString("Type").equals("Reminders")) {
                Entry currEntry = new Entry();
                currEntry.setTitle(event.getString("Name"));

                Date startDate = new Date(event.getString("Date"));
                LocalDate calStartDate = LocalDate.of(startDate.getYear() + 1900, startDate.getMonth() + 1, startDate.getDate());
                LocalTime localStartTime = LocalTime.of(startDate.getHours(), startDate.getMinutes());
                LocalTime localEndTime = LocalTime.of(startDate.getHours(), startDate.getMinutes() + 15);

                currEntry.changeStartDate(calStartDate);
                currEntry.changeEndDate(calStartDate);
                currEntry.changeStartTime(localStartTime);
                currEntry.changeEndTime(localEndTime);

                currCal.addEntry(currEntry);
                continue;
            }
            for (int i = 0; i < event.getJSONArray("Start_Times").length(); i++) {
                Entry currEntry = new Entry();
                currEntry.setTitle(event.getString("Name"));


                JSONArray startTimes = event.getJSONArray("Start_Times");
                JSONArray endTimes = event.getJSONArray("End_Times");
                Date currStart = new Date(startTimes.getString(i));
                Date currEnd = new Date(endTimes.getString(i));


                LocalDate calDateStart = LocalDate.of(currStart.getYear() + 1900, currStart.getMonth() + 1, currStart.getDate());
                LocalTime calTimeStart = LocalTime.of(currStart.getHours(), currStart.getMinutes());
                LocalDate calDateEnd = LocalDate.of(currEnd.getYear() + 1900, currEnd.getMonth() + 1, currEnd.getDate());
                LocalTime calTimeEnd = LocalTime.of(currEnd.getHours(), currEnd.getMinutes());

                currEntry.changeStartTime(calTimeStart);
                currEntry.changeEndTime(calTimeEnd);
                currEntry.changeStartDate(calDateStart);
                currEntry.changeEndDate(calDateEnd);

                currCal.addEntry(currEntry);
                if (event.keySet().contains("Repeat")) {
                    String repeat = event.getString("Repeat");
                    JSONArray repeatDays = event.getJSONArray("RepeatDay");

                    Entry recur = currEntry.createRecurrence();
                    recur.setTitle(currEntry.getTitle());
                    recur.changeStartTime(calTimeStart);
                    recur.changeEndTime(calTimeEnd);
                    recur.changeStartDate(calDateStart);
                    recur.changeEndDate(calDateEnd);

                    String rrule = null;
                    String byDay = buildDaySegment(repeatDays, calDateStart);

                    if (repeat.equals("Weekly")) {
                        rrule = "FREQ=WEEKLY" + byDay;
                    } else if (repeat.equals("Every")) {
                        if (hasDays(repeatDays)) {
                            rrule = "FREQ=WEEKLY" + byDay;
                        } else {
                            rrule = "FREQ=DAILY";
                        }
                    }
                    else if (repeat.equals("Biweekly")) {
                        rrule = "FREQ=WEEKLY;INTERVAL=2" + byDay;
                    } else if (repeat.equals("Monthly")) {
                        rrule = "FREQ=MONTHLY;BYMONTHDAY=" + calDateStart.getDayOfMonth();
                    } else if (repeat.equals("Annually")) {
                        rrule = "FREQ=YEARLY;BYMONTH=" + calDateStart.getMonthValue() +
                                ";BYMONTHDAY=" + calDateStart.getDayOfMonth();
                    }

                    if (rrule != null) {
                        recur.setRecurrenceRule(rrule);
                        recur.changeStartDate(recur.getStartDate().plusDays(1));
                        currCal.addEntry(recur);
                    }
                }
            }

        }

        CalendarView calendarView = new CalendarView(); // (1)

        CalendarSource myCalendarSource = new CalendarSource("My Calendars"); // (4)
        myCalendarSource.getCalendars().addAll(types.values());
        calendarView.getCalendarSources().addAll(myCalendarSource); // (5)
        calendarView.setRequestedTime(LocalTime.now());

        Thread updateTimeThread = new Thread("Calendar: Update Time Thread") {
            @Override
            public void run() {
                while (true) {
                    Platform.runLater(() -> {
                        calendarView.setToday(LocalDate.now());
                        calendarView.setTime(LocalTime.now());
                    });

                    try {
                        // update every 10 seconds
                        sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        updateTimeThread.setPriority(Thread.MIN_PRIORITY);
        updateTimeThread.setDaemon(true);
        updateTimeThread.start();

        Scene scene = new Scene(calendarView);
        primaryStage.setTitle("Calendar");
        primaryStage.setScene(scene);
        primaryStage.setWidth(1300);
        primaryStage.setHeight(1000);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private static boolean hasDays(JSONArray arr) {
        return arr != null && arr.length() > 0;
    }

    private static String buildDaySegment(JSONArray repeatDays, LocalDate date) {
        List<String> tokens  = new ArrayList<>();
        if (hasDays(repeatDays)) {
            for (int i = 0; i < repeatDays.length(); i++) {
                int v = repeatDays.optInt(i, -1);
                String t = dayTokenFromInt(v);
                if (t != null) {
                    tokens.add(t);
                }
            }
        }
        if (tokens.isEmpty() && date != null) {
            tokens.add(dayTokenFromDOW(date.getDayOfWeek()));
        }
        return tokens.isEmpty() ? "" : ";BYDAY=" + String.join(",", tokens);
    }

    private static String dayTokenFromDOW(DayOfWeek d) {
        switch (d) {
            case MONDAY: return "MO";
            case TUESDAY: return "TU";
            case WEDNESDAY: return "WE";
            case THURSDAY: return "TH";
            case FRIDAY: return "FR";
            case SATURDAY: return "SA";
            case SUNDAY: return "SU";
            default: return null;
        }
    }

    private static String dayTokenFromInt(int n) {
        switch (n) {
            case 0: return "SU";
            case 1: return "MO";
            case 2: return "TU";
            case 3: return "WE";
            case 4: return "TH";
            case 5: return "FR";
            case 6: return "SA";
            default: return null;
        }
    }
}
