import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.BufferedReader;
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

            try {
                parser.line();

                System.out.print("PASS: " + l);

            }
            catch(ParseCancellationException e) {
                System.out.print("FAIL: " + l);
            }
        }


    };
}