import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.converter.LocalDateTimeStringConverter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;

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
            }

        }

        CalendarView calendarView = new CalendarView(); // (1)

        Calendar birthdays = new Calendar("Birthdays"); // (2)
        Calendar holidays = new Calendar("Holidays");

        birthdays.setStyle(Calendar.Style.getStyle(0)); // (3)
        holidays.setStyle(Calendar.Style.getStyle(1));

        CalendarSource myCalendarSource = new CalendarSource("My Calendars"); // (4)
        myCalendarSource.getCalendars().addAll(birthdays, holidays);
        myCalendarSource.getCalendars().addAll(types.values());

        Entry entry = new Entry();
        LocalDateTime ldt = LocalDateTime.of(2025, 8, 16, 10, 20, 0);
        LocalTime lt = LocalTime.of(ldt.getHour(), ldt.getMinute());
        entry.changeStartTime(lt);
        entry.changeEndTime(LocalTime.of(lt.getHour() + 1, lt.getMinute()));
        entry.changeStartDate(LocalDate.of(ldt.getYear(), ldt.getMonth(), ldt.getDayOfMonth()));
        entry.setTitle("Soccer");

        Entry recur = entry.createRecurrence();
        recur.setRecurrenceRule("FREQ=WEEKLY;BYDAY=MO,TU");
        recur.setTitle(entry.getTitle());

        System.out.println(entry);
        //birthdays.addEntry(entry);
        //holidays.addEntry(recur);

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
}
