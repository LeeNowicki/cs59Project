import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.json.JSONObject;

import java.util.Date;


public class EventJsonListener implements CalendarListener {

    @Override
    public void enterStart(CalendarParser.StartContext ctx) {
    }

    @Override
    public void exitStart(CalendarParser.StartContext ctx) {
    }

    @Override
    public void enterLine(CalendarParser.LineContext ctx) {

    }

    @Override
    public void exitLine(CalendarParser.LineContext ctx) {

    }

    @Override
    public void enterKeyword(CalendarParser.KeywordContext ctx) {
    }

    @Override
    public void exitKeyword(CalendarParser.KeywordContext ctx) {

    }

    @Override
    public void enterAction(CalendarParser.ActionContext ctx) {
        if (!ctx.isEmpty()) {
            ParseTree actionType = ctx.getChild(0);
            String actionText = actionType.getText();
            if (actionText.equals("Cancel")) { // if action word is "cancel"
                ParseTree eventName = ctx.getChild(1);
                String eventString = eventName.getText();
                if (ctx.date() != null) { // if a date is included
                    Date date;
                    if (ctx.date().NUMERICDATE() != null) { // if the date is in numeric format
                        date = DateHandler.getFromNumericDate(ctx.date().getText());
                    } else {
                        date = DateHandler.getFromDate(ctx.date().getText());
                    }
                    JSONHandler.cancel(eventString, date); // remove event from hashset
                } else { // if no date was included
                    JSONHandler.cancel(eventString);
                }
            } else if (actionText.equals("Reminder")) {
                String reminderString = ctx.NAME().toString();
                Date date;
                if (ctx.date() != null && ctx.TIME() != null) {
                    date = DateHandler.getFromDate(ctx.date().getText(), ctx.TIME().getText());
                } else if (ctx.date() != null) {
                    date = DateHandler.getFromDate(ctx.date().getText());
                } else {
                    date = DateHandler.getTimeNoDate(ctx.TIME().getText());
                }
                ReminderHandler.createReminder(reminderString, date);
            } else if (actionText.equals("Extend")) {

            } else if (actionText.equals("Repeat")) {
                String eventName = ctx.NAME().toString();
                JSONObject obj = JSONHandler.getObject(eventName);
                if (obj == null) {
                    throw new ParseCancellationException("Event " + eventName + " not found for Repeat");
                }

                CalendarParser.FrequencyContext f = ctx.frequency();
                String word = f.getChild(0).getText();
                obj.put("Repeat", word);

                for (CalendarParser.WeekdayContext wc : f.weekday()) {
                    int day = toDayIndex(wc.getText());
                    obj.getJSONArray("RepeatDay").put(day);
                }

                JSONHandler.setSingleObject(obj);
                JSONHandler.addToMap();

            } else if (actionText.equals("Invite")) {

            }
        }
    }

    private static int toDayIndex(String txt) {
        switch (txt.toLowerCase()) {
            case "sun": case "sunday": return 0;
            case "mon": case "monday": return 1;
            case "tue": case "tuesday": return 2;
            case "wed": case "wednesday": return 3;
            case "thurs": case "thursday": return 4;
            case "fri": case "friday": return 5;
            case "sat": case "saturday": return 6;
            default: return -1;
        }
    }

    @Override
    public void exitAction(CalendarParser.ActionContext ctx) {

    }

    @Override
    public void enterEvent(CalendarParser.EventContext ctx) {
        // TODO:
        // Start_Times and End_Times are intialized with the object
        // IF IT ALREADY EXISTS (lee will make command to check)
        // Then .getJSONArray("Start_TImes").put(date);
        if (ctx.TYPE() != null) {
            JSONHandler.put("Type", ctx.TYPE().toString());
        }
        if (ctx.NAME() != null) {
            JSONHandler.put("Name", ctx.NAME().toString());
        }
        if (ctx.time() != null) {
        }
    }

    @Override public void exitEvent(CalendarParser.EventContext ctx) {
    }

    @Override
    public void enterFrequency(CalendarParser.FrequencyContext ctx) {

    }

    @Override
    public void exitFrequency(CalendarParser.FrequencyContext ctx) {

    }

    @Override
    public void enterWeekday(CalendarParser.WeekdayContext ctx) {

    }

    @Override
    public void exitWeekday(CalendarParser.WeekdayContext ctx) {

    }

    @Override
    public void enterFile(CalendarParser.FileContext ctx) {

    }

    @Override
    public void exitFile(CalendarParser.FileContext ctx) {

    }

    @Override
    public void enterDuration(CalendarParser.DurationContext ctx) {

    }

    @Override
    public void exitDuration(CalendarParser.DurationContext ctx) {

    }

    @Override
    public void enterTime(CalendarParser.TimeContext ctx) {

    }

    @Override
    public void exitTime(CalendarParser.TimeContext ctx) {

    }

    @Override
    public void enterDate(CalendarParser.DateContext ctx) {

    }

    @Override
    public void exitDate(CalendarParser.DateContext ctx) {

    }

    @Override
    public void enterNum(CalendarParser.NumContext ctx) {

    }

    @Override
    public void exitNum(CalendarParser.NumContext ctx) {

    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {

    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {

    }

    @Override
    public void enterEveryRule(ParserRuleContext parserRuleContext) {

    }

    @Override
    public void exitEveryRule(ParserRuleContext parserRuleContext) {

    }
}
