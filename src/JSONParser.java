import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * Step 1: Display-only calendar (no events, no rules).
 *
 * Run with JavaFX modules on the VM options (same as you already set):
 *   --module-path "C:\\path\\to\\javafx-sdk-21.0.8\\lib" --add-modules=javafx.controls,javafx.web
 */
public class JSONParser extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();

        // Surface JS errors in your Run console so a blank screen is easy to debug
        engine.setOnAlert(e -> System.out.println("[JS alert] " + e.getData()));

        String html = "<!DOCTYPE html>" +
                "<html><head>" +
                "  <meta charset=\"utf-8\">" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">" +
                "  <title>Calendar (Step 1)</title>" +
                // FullCalendar CSS + JS (globals build) — display only
                "  <link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/fullcalendar@6.1.19/index.global.min.css\">" +
                "  <script src=\"https://cdn.jsdelivr.net/npm/fullcalendar@6.1.19/index.global.min.js\"></script>" +
                "  <style>html,body{height:100%;margin:0} #calendar{max-width:1100px;margin:20px auto} .fc{font-family:system-ui,-apple-system,Segoe UI,Roboto,Inter,Helvetica,Arial,sans-serif}</style>" +
                "</head><body>" +
                "  <div id=\"calendar\"></div>" +
                "  <script>\n" +
                "    // Show JS errors in the Java console\n" +
                "    window.onerror = (msg, src, line, col) => alert('JS error: '+msg+' @ '+line+':'+col);\n" +
                "    document.addEventListener('DOMContentLoaded', function () {\n" +
                "      const el = document.getElementById('calendar');\n" +
                "      const calendar = new FullCalendar.Calendar(el, {\n" +
                "        initialView: 'dayGridMonth',\n" +
                "        height: '100vh',\n" +
                "        headerToolbar: { left: 'prev,next today', center: 'title', right: 'dayGridMonth,timeGridWeek,timeGridDay' }\n" +
                "      });\n" +
                "      calendar.render();\n" +
                "    });\n" +
                "  </script>" +
                "</body></html>";

        engine.loadContent(html);

        Scene scene = new Scene(webView, 1100, 800);
        stage.setTitle("DSL Calendar — Step 1 (Display)");
        stage.setScene(scene);
        stage.show();
    }
}
