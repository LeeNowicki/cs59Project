import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.Date;


public class EventJsonListener implements CalendarListener {

    //Helper functions
    public String getDateString(CalendarParser.DateContext ctx){
        if(!ctx.isEmpty()){
            //if it's numeric
            if(ctx.NUMERICDATE() != null){
                return ctx.NUMERICDATE().getText();
            }

            //Otherwise loop through the children

            StringBuilder dateString = new StringBuilder();

            for(int i = 0; i<ctx.getChildCount();i++){
                dateString.append(ctx.getChild(i).getText()+ " ");
            }

            return dateString.toString();
        }
        return null;
    }

    @Override
    public void enterStart(CalendarParser.StartContext ctx) {
        JSONHandler.createThisObject();
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

    }

    private static int toDayIndex(String txt) {
        switch (txt.toLowerCase()) {
            case "sun": case "sunday": return 0;
            case "mon": case "monday": return 1;
            case "tues": case "tuesday": return 2;
            case "wed": case "wednesday": return 3;
            case "thurs": case "thursday": return 4;
            case "fri": case "friday": return 5;
            case "sat": case "saturday": return 6;
            default: return -1;
        }
    }

    @Override
    public void exitAction(CalendarParser.ActionContext ctx) {
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
                        date = DateHandler.getFromDate(getDateString(ctx.date()));
                    }
                    JSONHandler.cancel(eventString, date); // remove event from hashset
                } else { // if no date was included
                    JSONHandler.cancel(eventString);
                }
            } else if (actionText.equals("Reminder")) {
                String reminderString = ctx.NAME().toString();
                Date date;
                if (ctx.date() != null && ctx.TIME() != null) {
                    if (ctx.date().NUMERICDATE() != null) {
                        date = DateHandler.getFromNumericDate(getDateString(ctx.date()), ctx.TIME().getText());
                    } else {
                        date = DateHandler.getFromDate(getDateString(ctx.date()), ctx.TIME().getText());
                    }
                } else if (ctx.date() != null) {
                    if (ctx.date().NUMERICDATE() != null) {
                        date = DateHandler.getFromNumericDate(getDateString(ctx.date()));
                    } else {
                        date = DateHandler.getFromDate(getDateString(ctx.date()));
                    }
                } else {
                    date = DateHandler.getTimeNoDate(ctx.TIME().getText());
                }
                ReminderHandler.createReminder(reminderString, date);
            } else if (actionText.equals("Extend")) {
                // Pull event by name
                String eventName = ctx.NAME(0).getText();
                JSONObject obj = JSONHandler.getObject(eventName);
                if (obj == null) {
                    throw new ParseCancellationException("Event " + eventName + " not found for Extend");
                }

                // Grab given date
                CalendarParser.DateContext dateCtx = ctx.date();
                Date targetDate;
                if (dateCtx.NUMERICDATE() != null) {
                    targetDate = DateHandler.getFromNumericDate(dateCtx.NUMERICDATE().getText());
                } else {
                    targetDate = DateHandler.getFromDate(getDateString(dateCtx));
                }

                // Get how much to extend
                int amount = Integer.parseInt(ctx.duration().num().getText());
                String unit = ctx.duration().getChild(1).getText();
                boolean isHours = unit.startsWith("hour");

                // Get starts and ends for event
                JSONArray starts = obj.getJSONArray("Start_Times");
                JSONArray ends = obj.getJSONArray("End_Times");

                for (int i = 0; i < starts.length(); i++) {
                    Date start = (Date) starts.get(i);

                    if (sameDay(start, targetDate)) {
                        Date end = (Date) ends.get(i);

                        if (isHours) {
                            end.setHours(end.getHours() + amount);
                        } else {
                            end.setMinutes(end.getMinutes() + amount);
                        }

                        ends.put(i, end);

                    }
                }


            } else if (actionText.equals("Repeat")) {
                String eventName = ctx.NAME(0).getText();
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

            } else if (actionText.equals("Invite")) { // Grammar: 'Invite' NAME ('to')? NAME ('on')? date (file)?
                String invitee = ctx.NAME(0).getText();
                String eventName = ctx.NAME(1).getText();
                Date date;
                if (ctx.date().NUMERICDATE() != null) { // if date is numeric
                    date = DateHandler.getFromNumericDate(ctx.date().getText());
                } else {
                    date = DateHandler.getFromDate(getDateString(ctx.date()));
                }
                JSONHandler.invite(invitee, eventName, date);
            }
        }
    }

    private static boolean sameDay(Date d1, Date d2) {
        return d1.getDate() == d2.getDate() && d1.getMonth() == d2.getMonth() && d1.getYear() == d2.getYear();
    }

    @Override
    public void enterEvent(CalendarParser.EventContext ctx) {
        // TODO:
        // Start_Times and End_Times are intialized with the object
        // IF IT ALREADY EXISTS (lee will make command to check)
        // Then .getJSONArray("Start_TImes").put(date);
//        if (ctx.TYPE() != null) {
//            System.out.println("Type"+ctx.TYPE().toString());
//            JSONHandler.put("Type", ctx.TYPE().toString());
//        }
//        if (ctx.NAME() != null) {
//            JSONHandler.put("Name", ctx.NAME().toString());
//            System.out.println("Name"+ctx.NAME().toString());
//        }
//        if (ctx.time() != null) {
//        }
    }

    @Override public void exitEvent(CalendarParser.EventContext ctx) {

        //Do name first - it is what existing events are referenced by
        if (ctx.NAME() != null) {
            //Check if it already exists, load it into the current object if so
            if(JSONHandler.getObject(ctx.NAME().toString()) != null){
                JSONHandler.setSingleObject(JSONHandler.getObject(ctx.NAME().toString()));
            }
            JSONHandler.put("Name", ctx.NAME().toString());
            JSONHandler.setObjectName(ctx.NAME().toString());
        }
        if (ctx.TYPE() != null) {
            JSONHandler.put("Type", ctx.TYPE().toString());
        }
        if(ctx.time().TIME(0) != null){
            String timeString = ctx.time().TIME(0).toString();
            String dateString = "";

            Date startTime = null;
            Date endTime = null;
            //Case 1 - no date provided
            if(ctx.date() == null) {
                startTime = DateHandler.getTimeNoDate(timeString);

                if (ctx.time().TIME(1) != null) {
                    endTime = DateHandler.getTimeNoDate(ctx.time().TIME(1).toString());
                }
            }
            else {
                dateString = getDateString(ctx.date());
                if(ctx.date().NUMERICDATE() != null){
                    startTime = DateHandler.getFromNumericDate(dateString, timeString);
                    if (ctx.time().TIME(1) != null) {
                        endTime = DateHandler.getFromNumericDate(dateString, ctx.time().TIME(1).toString());
                    }
                }
                else {
                    startTime = DateHandler.getFromDate(dateString, timeString);
                    if (ctx.time().TIME(1) != null) {
                        endTime = DateHandler.getFromDate(dateString, ctx.time().TIME(1).toString());
                    }
                }


            }

            if (endTime == null){
                endTime = DateHandler.getDefaultEndTime(startTime);
            }

            JSONHandler.addStartTime(startTime);
            JSONHandler.addEndTime(endTime);
        }
        JSONHandler.addToMap();
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

    public static void main(String[] args) {

    }
}
