// Generated from C:/Users/aleek/IdeaProjects/CS59ProjectDir/Calendar.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CalendarParser}.
 */
public interface CalendarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link CalendarParser#start}.
	 * @param ctx the parse tree
	 */
	void enterStart(CalendarParser.StartContext ctx);
	/**
	 * Exit a parse tree produced by {@link CalendarParser#start}.
	 * @param ctx the parse tree
	 */
	void exitStart(CalendarParser.StartContext ctx);
	/**
	 * Enter a parse tree produced by {@link CalendarParser#line}.
	 * @param ctx the parse tree
	 */
	void enterLine(CalendarParser.LineContext ctx);
	/**
	 * Exit a parse tree produced by {@link CalendarParser#line}.
	 * @param ctx the parse tree
	 */
	void exitLine(CalendarParser.LineContext ctx);
	/**
	 * Enter a parse tree produced by {@link CalendarParser#keyword}.
	 * @param ctx the parse tree
	 */
	void enterKeyword(CalendarParser.KeywordContext ctx);
	/**
	 * Exit a parse tree produced by {@link CalendarParser#keyword}.
	 * @param ctx the parse tree
	 */
	void exitKeyword(CalendarParser.KeywordContext ctx);
	/**
	 * Enter a parse tree produced by {@link CalendarParser#action}.
	 * @param ctx the parse tree
	 */
	void enterAction(CalendarParser.ActionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CalendarParser#action}.
	 * @param ctx the parse tree
	 */
	void exitAction(CalendarParser.ActionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CalendarParser#event}.
	 * @param ctx the parse tree
	 */
	void enterEvent(CalendarParser.EventContext ctx);
	/**
	 * Exit a parse tree produced by {@link CalendarParser#event}.
	 * @param ctx the parse tree
	 */
	void exitEvent(CalendarParser.EventContext ctx);
	/**
	 * Enter a parse tree produced by {@link CalendarParser#frequency}.
	 * @param ctx the parse tree
	 */
	void enterFrequency(CalendarParser.FrequencyContext ctx);
	/**
	 * Exit a parse tree produced by {@link CalendarParser#frequency}.
	 * @param ctx the parse tree
	 */
	void exitFrequency(CalendarParser.FrequencyContext ctx);
	/**
	 * Enter a parse tree produced by {@link CalendarParser#weekday}.
	 * @param ctx the parse tree
	 */
	void enterWeekday(CalendarParser.WeekdayContext ctx);
	/**
	 * Exit a parse tree produced by {@link CalendarParser#weekday}.
	 * @param ctx the parse tree
	 */
	void exitWeekday(CalendarParser.WeekdayContext ctx);
	/**
	 * Enter a parse tree produced by {@link CalendarParser#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(CalendarParser.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link CalendarParser#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(CalendarParser.FileContext ctx);
	/**
	 * Enter a parse tree produced by {@link CalendarParser#duration}.
	 * @param ctx the parse tree
	 */
	void enterDuration(CalendarParser.DurationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CalendarParser#duration}.
	 * @param ctx the parse tree
	 */
	void exitDuration(CalendarParser.DurationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CalendarParser#time}.
	 * @param ctx the parse tree
	 */
	void enterTime(CalendarParser.TimeContext ctx);
	/**
	 * Exit a parse tree produced by {@link CalendarParser#time}.
	 * @param ctx the parse tree
	 */
	void exitTime(CalendarParser.TimeContext ctx);
	/**
	 * Enter a parse tree produced by {@link CalendarParser#date}.
	 * @param ctx the parse tree
	 */
	void enterDate(CalendarParser.DateContext ctx);
	/**
	 * Exit a parse tree produced by {@link CalendarParser#date}.
	 * @param ctx the parse tree
	 */
	void exitDate(CalendarParser.DateContext ctx);
}