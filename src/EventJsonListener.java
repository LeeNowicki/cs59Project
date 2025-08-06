import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;


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

    }

    @Override
    public void exitAction(CalendarParser.ActionContext ctx) {

    }

    @Override
    public void enterEvent(CalendarParser.EventContext ctx) {
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
