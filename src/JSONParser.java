import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class JSONParser extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();

        engine.setOnAlert(e -> System.out.println("[JS alert] " + e.getData()));

        String html = "<!DOCTYPE html>" +
                "<html><head>" +
                "  <meta charset=\"utf-8\">" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">" +
                "  <title>Calendar</title>" +
                "  <link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/fullcalendar@6.1.19/index.global.min.css\">" +
                "  <script src=\"https://cdn.jsdelivr.net/npm/fullcalendar@6.1.19/index.global.min.js\"></script>" +
                "  <style>html,body{height:100%;margin:0} #calendar{max-width:1100px;margin:20px auto} .fc{font-family:system-ui,-apple-system,Segoe UI,Roboto,Inter,Helvetica,Arial,sans-serif}</style>" +
                "</head><body>" +
                "  <div id=\"calendar\"></div>" +
                "  <script>\n" +
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
        stage.setTitle("Calendar");
        stage.setScene(scene);
        stage.show();
    }
}
