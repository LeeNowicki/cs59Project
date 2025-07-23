// Generated from C:/Users/aleek/IdeaProjects/CS59ProjectDir/Calendar.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link CalendarParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface CalendarVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link CalendarParser#start}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStart(CalendarParser.StartContext ctx);
	/**
	 * Visit a parse tree produced by {@link CalendarParser#line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLine(CalendarParser.LineContext ctx);
	/**
	 * Visit a parse tree produced by {@link CalendarParser#keyword}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKeyword(CalendarParser.KeywordContext ctx);
	/**
	 * Visit a parse tree produced by {@link CalendarParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction(CalendarParser.ActionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CalendarParser#event}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEvent(CalendarParser.EventContext ctx);
	/**
	 * Visit a parse tree produced by {@link CalendarParser#frequency}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFrequency(CalendarParser.FrequencyContext ctx);
	/**
	 * Visit a parse tree produced by {@link CalendarParser#weekday}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWeekday(CalendarParser.WeekdayContext ctx);
	/**
	 * Visit a parse tree produced by {@link CalendarParser#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(CalendarParser.FileContext ctx);
	/**
	 * Visit a parse tree produced by {@link CalendarParser#duration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDuration(CalendarParser.DurationContext ctx);
	/**
	 * Visit a parse tree produced by {@link CalendarParser#time}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTime(CalendarParser.TimeContext ctx);
	/**
	 * Visit a parse tree produced by {@link CalendarParser#date}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDate(CalendarParser.DateContext ctx);
}