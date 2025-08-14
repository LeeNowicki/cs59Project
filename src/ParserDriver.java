import netscape.javascript.JSObject;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.json.*;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ParserDriver {
    public static void main(String[] args) throws Exception {
        //BufferedReader fileStream = new BufferedReader(new FileReader("test.txt"));

        //Enable this snippet to read the next line and open that file
        //Scanner scanner = new Scanner(System.in);
        //String line = scanner.nextLine();
        //BufferedReader fileStream = new BufferedReader(new FileReader(line));


        //Alternatively, read from args (May cause errors, no safeguards
        BufferedReader fileStream = new BufferedReader(new FileReader(args[0]));

        String l ;
        while((l = fileStream.readLine()) != null){
            l+= "\n"; //add back the newline - it matters for the parser

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


                // JSONHandler.printAll();
            }
            catch(ParseCancellationException e) {
                System.out.print("FAIL: " + l);
                System.out.println(e.getMessage());
            }
            //catch(Exception e){System.out.println("Runtime error on line: " +l);System.out.println("Error details:" + e);}
        }

        JSONHandler.printAll();

        try {
            //writes to jsonFile, change pathname to take from args or something if required
            String pathname = "testing/working.json";
            File jsonFile = new File(pathname);
            jsonFile.createNewFile();

            JSONHandler.printFile(pathname);

        }
        catch (Exception e){
            System.out.println("Exception occured during file creation: " + e);
        }
    };
}