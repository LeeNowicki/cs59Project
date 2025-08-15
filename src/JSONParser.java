import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

// CalendarFX
import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;


/*************THIS IS ALL A CHAT EXAMPLE**********************/


/**
 *
 *
 * CalendarFX renderer for your DSL JSON with MANUAL repeat expansion.
 *
 * Usage:
 *   java JSONParser <path-to-json>
 *
 * JavaFX VM options (Windows example):
 *   --module-path "C:\\path\\to\\javafx-sdk-21.0.8\\lib" --add-modules=javafx.controls,javafx.web
 */
public class JSONParser extends Application {

    private static JSONObject INPUT = new JSONObject();
    private static final ZoneId ZONE = ZoneId.of("America/New_York"); // choose your display TZ

    // Example Date.toString(): "Wed Aug 13 16:00:00 EDT 2025"
    private static final DateTimeFormatter JAVA_UTIL_DATE =
            DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);

    // Try common formats your pipeline might emit
    private static ZonedDateTime parseZDT(String s) {
        if (s == null || s.isEmpty()) return null;
        try { return ZonedDateTime.parse(s, JAVA_UTIL_DATE); } catch (Exception ignored) {}
        try { return ZonedDateTime.parse(s, DateTimeFormatter.ISO_ZONED_DATE_TIME); } catch (Exception ignored) {}
        try { return OffsetDateTime.parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toZonedDateTime(); } catch (Exception ignored) {}
        try { return LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME).atZone(ZONE); } catch (Exception ignored) {}
        return null;
    }

    private static String sanitizeTitle(String s) {
        if (s == null) return "";
        return s.replaceAll("^\\\"+|\\\"+$", ""); // strip stored leading/trailing quotes
    }

    private static String fmtTime(LocalTime t) {
        String s = t.format(DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH));
        return s.toLowerCase(Locale.ENGLISH);
    }

    private static String fmtRange(LocalDateTime start, LocalDateTime end) {
        return fmtTime(start.toLocalTime()) + " \u2013 " + fmtTime(end.toLocalTime());
    }

    private static final DayOfWeek[] IDX_TO_DOW = {
            DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
    };

    public static void main(String[] args) {
        if (args.length > 0) {
            try {
                String raw = Files.readString(Path.of(args[0]), StandardCharsets.UTF_8);
                INPUT = new JSONObject(raw);
            } catch (IOException e) {
                System.err.println("Failed to read JSON file: " + e.getMessage());
                INPUT = new JSONObject();
            } catch (Exception je) {
                System.err.println("Invalid JSON: " + je.getMessage());
                INPUT = new JSONObject();
            }
        } else {
            System.err.println("No JSON file path provided. Opening an empty CalendarFX view.");
        }
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Map<String, Calendar> calendarsByType = new LinkedHashMap<>();
        Calendar remindersCal = new Calendar("Reminders");
        remindersCal.setReadOnly(true);
        remindersCal.setStyle(Calendar.Style.getStyle(6)); // STYLE7 (getStyle cycles 0..6)

        List<ZonedDateTime> allStarts = new ArrayList<>();

        // Optional top-level Reminders array
        if (INPUT.optJSONArray("Reminders") != null) {
            JSONArray arr = INPUT.getJSONArray("Reminders");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject r = arr.optJSONObject(i);
                if (r == null) continue;
                addReminder(remindersCal, allStarts, r);
            }
        }

        for (String key : INPUT.keySet()) {
            Object node = INPUT.get(key);

            // Inline reminder object: {"Reminder": "...", "Date": "..."} and no Start_Times
            if (node instanceof JSONObject) {
                JSONObject jobj = (JSONObject) node;
                if (jobj.has("Reminder") && jobj.has("Date") && !jobj.has("Start_Times")) {
                    addReminder(remindersCal, allStarts, jobj);
                    continue;
                }
            }

            if (!(node instanceof JSONObject)) continue;
            JSONObject obj = (JSONObject) node;

            String rawName = obj.optString("Name", key);
            String title = sanitizeTitle(rawName);
            String type = obj.optString("Type", "Other");

            Calendar cal = calendarsByType.computeIfAbsent(type, t -> {
                Calendar c = new Calendar(t);
                int idx = calendarsByType.size();         // stable order
                c.setStyle(Calendar.Style.getStyle(idx)); // STYLE1..STYLE7 cycling
                return c;
            });

            // Gather concrete pairs (we’ll also use the first pair as the “base”)
            List<ZonedDateTime> sList = readZonedTimes(obj.optJSONArray("Start_Times"));
            List<ZonedDateTime> eList = readZonedTimes(obj.optJSONArray("End_Times"));
            int n = Math.max(sList.size(), eList.size());

            // Filter/normalize to valid pairs
            List<ZonedDateTime> starts = new ArrayList<>();
            List<ZonedDateTime> ends   = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                ZonedDateTime zs = i < sList.size() ? sList.get(i) : null;
                ZonedDateTime ze = i < eList.size() ? eList.get(i) : null;
                if (zs == null || ze == null) continue;
                if (zs.getYear() > 2100 || ze.getYear() > 2100) continue;
                if (!ze.isAfter(zs)) continue;
                starts.add(zs);
                ends.add(ze);
            }
            if (starts.isEmpty()) continue;

            // Base occurrence (anchor)
            ZonedDateTime baseStartZ = starts.get(0);
            ZonedDateTime baseEndZ   = ends.get(0);
            LocalDateTime baseStart  = baseStartZ.withZoneSameInstant(ZONE).toLocalDateTime();
            LocalDateTime baseEnd    = baseEndZ.withZoneSameInstant(ZONE).toLocalDateTime();
            Duration baseDur         = Duration.between(baseStart, baseEnd);

            // Manual repeats
            String repeat = obj.optString("Repeat", "").toLowerCase(Locale.ENGLISH).trim();
            List<Integer> repeatDaysIdx = readRepeatDays(obj.optJSONArray("RepeatDay"));

            // Range: 100 years before/after base
            LocalDate rangeStart = baseStart.toLocalDate().minusYears(100);
            LocalDate rangeEnd   = baseStart.toLocalDate().plusYears(100);

            // Add expanded repeats (and also include *all* provided concrete pairs)
            switch (repeat) {
                case "every":
                case "weekly":
                case "biweekly": {
                    boolean biweekly = repeat.equals("biweekly");
                    Set<DayOfWeek> days = new LinkedHashSet<>();
                    if (!repeatDaysIdx.isEmpty()) {
                        for (int idx : repeatDaysIdx) {
                            if (0 <= idx && idx <= 6) days.add(IDX_TO_DOW[idx]);
                        }
                    }
                    if (days.isEmpty()) {
                        days.add(baseStart.getDayOfWeek()); // default to base day if none provided
                    }
                    // Align biweekly to the base week's Sunday
                    LocalDate baseWeekStart = baseStart.toLocalDate()
                            .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

                    for (LocalDate d = rangeStart; !d.isAfter(rangeEnd); d = d.plusDays(1)) {
                        DayOfWeek dow = d.getDayOfWeek();
                        if (!days.contains(dow)) continue;
                        if (biweekly) {
                            LocalDate weekStart = d.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
                            long weeksBetween = ChronoUnit.WEEKS.between(baseWeekStart, weekStart);
                            if ((weeksBetween % 2) != 0) continue;
                        }
                        LocalDateTime s = d.atTime(baseStart.toLocalTime());
                        LocalDateTime e = s.plus(baseDur);
                        addEntry(cal, title, s, e);
                        allStarts.add(s.atZone(ZONE));
                    }
                    break;
                }
                case "monthly": {
                    // Repeat on same day-of-month/time, skipping invalid months (e.g., Feb 30)
                    LocalDate anchor = baseStart.toLocalDate();
                    int baseDom = anchor.getDayOfMonth();
                    LocalTime t = baseStart.toLocalTime();
                    // go from (base year - 100) Jan to (base year + 100) Dec
                    LocalDate cursor = LocalDate.of(anchor.getYear() - 100, anchor.getMonth(), 1);
                    LocalDate endCursor = LocalDate.of(anchor.getYear() + 100, anchor.getMonth(), 1).plusMonths(12);
                    while (!cursor.isAfter(endCursor)) {
                        int y = cursor.getYear();
                        int m = cursor.getMonthValue();
                        // if month has the day
                        if (isValidDayOfMonth(y, m, baseDom)) {
                            LocalDate d = LocalDate.of(y, m, baseDom);
                            LocalDateTime s = d.atTime(t);
                            LocalDateTime e = s.plus(baseDur);
                            addEntry(cal, title, s, e);
                            allStarts.add(s.atZone(ZONE));
                        }
                        cursor = cursor.plusMonths(1);
                    }
                    break;
                }
                case "annually": {
                    // Same month/day/time each year
                    LocalDate anchor = baseStart.toLocalDate();
                    int baseDom = anchor.getDayOfMonth();
                    Month baseMonth = anchor.getMonth();
                    LocalTime t = baseStart.toLocalTime();
                    for (int y = anchor.getYear() - 100; y <= anchor.getYear() + 100; y++) {
                        if (!isValidDayOfMonth(y, baseMonth.getValue(), baseDom)) continue;
                        LocalDate d = LocalDate.of(y, baseMonth, baseDom);
                        LocalDateTime s = d.atTime(t);
                        LocalDateTime e = s.plus(baseDur);
                        addEntry(cal, title, s, e);
                        allStarts.add(s.atZone(ZONE));
                    }
                    break;
                }
                default: {
                    // No repeat: just add concrete pairs
                    // (Also applies to unsupported repeat keywords)
                }
            }

            // Always add concrete pairs from JSON (so overrides/one-offs show)
            for (int i = 0; i < starts.size(); i++) {
                LocalDateTime s = starts.get(i).withZoneSameInstant(ZONE).toLocalDateTime();
                LocalDateTime e = ends.get(i).withZoneSameInstant(ZONE).toLocalDateTime();
                addEntry(cal, title, s, e);
                allStarts.add(starts.get(i));
            }
        }

        // Build view
        CalendarView view = new CalendarView();
        CalendarSource source = new CalendarSource("DSL Calendars");
        for (Calendar c : calendarsByType.values()) source.getCalendars().add(c);
        if (!remindersCal.findEntries("").isEmpty()) source.getCalendars().add(remindersCal);
        view.getCalendarSources().setAll(source);

        LocalDate focusDate = allStarts.isEmpty()
                ? LocalDate.now()
                : allStarts.stream().min(Comparator.naturalOrder()).get()
                .withZoneSameInstant(ZONE).toLocalDate();

        view.setRequestedTime(LocalTime.now());
        view.showWeekPage();
        view.setDate(focusDate);

        Scene scene = new Scene(view, 1200, 850);
        stage.setTitle("CalendarFX — DSL Calendar (Manual Repeats)");
        stage.setScene(scene);
        stage.show();

        // Optional refresh (CalendarFX pattern)
        Thread refresher = new Thread(() -> {
            while (true) {
                try { Thread.sleep(10_000); } catch (InterruptedException ignored) {}
                Platform.runLater(view::refreshData);
            }
        });
        refresher.setDaemon(true);
        refresher.start();
    }

    // ===== Helpers =====

    private static List<ZonedDateTime> readZonedTimes(JSONArray arr) {
        List<ZonedDateTime> out = new ArrayList<>();
        if (arr == null) return out;
        for (int i = 0; i < arr.length(); i++) {
            String s = String.valueOf(arr.opt(i));
            ZonedDateTime z = parseZDT(s);
            if (z != null) out.add(z);
        }
        return out;
    }

    private static List<Integer> readRepeatDays(JSONArray arr) {
        if (arr == null) return Collections.emptyList();
        List<Integer> out = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            try { out.add(Integer.parseInt(String.valueOf(arr.get(i)))); } catch (Exception ignored) {}
        }
        // De-dup while preserving order
        return out.stream().distinct().collect(Collectors.toList());
    }

    private static boolean isValidDayOfMonth(int year, int month, int day) {
        try {
            LocalDate.of(year, month, 1).withDayOfMonth(day);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void addEntry(Calendar cal, String title, LocalDateTime start, LocalDateTime end) {
        Entry<?> e = new Entry<>(fmtRange(start, end) + "  " + title);
        e.setInterval(start, end);
        cal.addEntry(e);
    }

    private static void addReminder(Calendar remindersCal, List<ZonedDateTime> allStarts, JSONObject r) {
        String text = r.optString("Reminder", "").trim();
        String dateStr = r.optString("Date", "").trim();
        ZonedDateTime z = parseZDT(dateStr);
        if (z == null || z.getYear() > 2100) return;
        LocalDateTime start = z.withZoneSameInstant(ZONE).toLocalDateTime();
        LocalDateTime end = start.plusMinutes(30);
        Entry<?> e = new Entry<>("🔔 " + text);
        e.setInterval(start, end);
        remindersCal.addEntry(e);
        allStarts.add(z);
    }
}
