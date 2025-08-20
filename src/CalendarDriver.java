import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class CalendarDriver {

    public static void main(String[] args) throws Exception{
        System.out.println("Welcome to the Calendar Language application!");
        Scanner input = new Scanner(System.in);

        while (true){
            int index = 1;
            System.out.println("Enter the associated number for the action you want:");
            System.out.println(index + ") Load a calendar from new file (.txt)");index++;
            System.out.println(index + ") Add to/Create a calendar from the command line");index++;
            System.out.println(index + ") Export the current Calendar to a JSON file");index++;
            System.out.println(index + ") Display the calendar that got loaded");index++;
            System.out.println(index + ") Display a calendar from a JSON file");index++;
            System.out.println(index + ") Export the invitations from the currently loaded calendar");index++;
            System.out.println("q) quit the program");

            String selection = input.nextLine();

            //Load a calendar from a file matching the syntax
            if(selection.equals("1")){
                System.out.println("What file do you want to read?");
                String pathname = input.nextLine();

                try {

                    parseCalendarFile(pathname);
                }
                catch (Exception e){
                    System.out.println("Something went wrong when reading the file:" + e);
                }

            }
            //Load a calendar from inputs
            else if (selection.equals("2")){
                // TODO: string up a bunch of inputs and parse them
                System.out.println("Write out what you want to input to the calendar");
                System.out.println("Input q on a line by itself to quit");
                ArrayList<String> toparse = new ArrayList<>();

                String line ;

                while (true) {
                    line = input.nextLine();

                    if(line.equals("q")){
                        break;
                    }

                    line+= "\n";
                    toparse.add(line);
                }

                //System.out.println(toparse);

                parseCalendarArray(toparse);
            }
            //Export the current calendar to a JSON file
            else if (selection.equals("3")){
                System.out.println("What file do you want the calendar in?");
                String pathname = input.nextLine();
                try {
                    File jsonFile = new File(pathname);
                    jsonFile.createNewFile();

                    JSONHandler.printFile(pathname);


                }
                catch (Exception e){
                    System.out.println("Error in creating file: "+ e);
                }



            }
            //Display the currently loaded calendar
            else if (selection.equals("4")){
                System.out.println("Displaying Current Calendar");
                JSONtoCalendar.display(JSONHandler.makeObject());

            }
            else if (selection.equals("5")){
                System.out.println("What JSON file do you want to display?");
                String pathname = input.nextLine();

                try {

                    JSONtoCalendar.display(pathname);
                }
                catch (Exception e){
                    System.out.println("Something went wrong when reading the file:" + e);
                }

            }
            else if (selection.equals("6")){
                System.out.println("What directory do you want the invites to go in?");
                System.out.println("(Don't forget the ending /)");
                String pathname = input.nextLine();

                try {
                    JSONHandler.printInviteFiles(pathname);

                }
                catch (Exception e){
                    System.out.println("Something went wrong when reading the file:" + e);
                }

            }
            else if(selection.equals("q")){
                break;
            }




        }

    }

    public static void parseCalendarFile(String toParse){
        try {
            BufferedReader fileStream = new BufferedReader(new FileReader(toParse));

            String l;
            while ((l = fileStream.readLine()) != null) {
                l += "\n"; //add back the newline - it matters for the parser

                CharStream input = CharStreams.fromString(l);

                //Exception stuff taken from: https://stackoverflow.com/questions/18132078/handling-errors-in-antlr4
                CalendarLexer lexer = new CalendarLexer(input);
                lexer.removeErrorListeners();
                lexer.addErrorListener(ThrowingErrorListener.INSTANCE);

                CommonTokenStream tokens = new CommonTokenStream(lexer);
                CalendarParser parser = new CalendarParser(tokens);
                parser.removeErrorListeners();
                parser.addErrorListener(ThrowingErrorListener.INSTANCE);

                EventJsonListener eventListener = new EventJsonListener();
                parser.removeParseListeners();
                parser.addParseListener(eventListener);

                try {
                    //JSONHandler.createThisObject(); // now does this upon entering start instead
                    parser.start();


                    //JSONHandler.printAll();
                } catch (ParseCancellationException e) {
                    System.out.print("Line does not match syntax: " + l);
                    System.out.println(e.getMessage());
                }
                //catch(Exception e){System.out.println("Runtime error on line: " +l);System.out.println("Error details:" + e);}
            }
        }
        catch (Exception e){
            System.out.println("Something went wrong when parsing" + toParse + ": " +e);
        }
    }

    public static void parseCalendarArray(ArrayList<String> toParse){
        try {


                for(String l : toParse) {
                //l += "\n"; //add back the newline - it matters for the parser

                CharStream input = CharStreams.fromString(l);

                //Exception stuff taken from: https://stackoverflow.com/questions/18132078/handling-errors-in-antlr4
                CalendarLexer lexer = new CalendarLexer(input);
                lexer.removeErrorListeners();
                lexer.addErrorListener(ThrowingErrorListener.INSTANCE);

                CommonTokenStream tokens = new CommonTokenStream(lexer);
                CalendarParser parser = new CalendarParser(tokens);
                parser.removeErrorListeners();
                parser.addErrorListener(ThrowingErrorListener.INSTANCE);

                EventJsonListener eventListener = new EventJsonListener();
                parser.removeParseListeners();
                parser.addParseListener(eventListener);

                try {
                    //JSONHandler.createThisObject(); // now does this upon entering start instead
                    parser.start();


                    //JSONHandler.printAll();
                } catch (ParseCancellationException e) {
                    System.out.print("Line does not match syntax: " + l);
                    System.out.println(e.getMessage());
                }
                //catch(Exception e){System.out.println("Runtime error on line: " +l);System.out.println("Error details:" + e);}
            }
        }
        catch (Exception e){
            System.out.println("Something went wrong when parsing" + toParse + ": " +e);
        }
    }
}
