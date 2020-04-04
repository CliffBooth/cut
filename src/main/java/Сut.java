import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.*;
import java.util.*;

public class Cut {

    @Option(name = "-o", metaVar = "OutputName", usage = "Output file name")
    private String outputName;

    @Option(name = "-c", metaVar = "IndentChars", usage = "indent in chars", forbids = {"-w"})
    private boolean indentChar;

    @Option(name = "-w", metaVar = "IndentWords", usage = "indent in words", forbids = {"-c"})
    private boolean indentWord;

    @Argument(required = true, metaVar = "Range", usage = "Range of chars or words")
    private String range;

    @Argument(metaVar = "InputName", index = 1, usage = "Input file name")
    private String inputName;


    public static void main(String[] args) {
        new Cut().launch(args);
    }

    private void launch(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java -jar cut.jar [-c IndentChars|-w IndentWords] [-o outputName] [InputName] range");
            parser.printUsage(System.err);
            return;
        }

        if (!indentChar && !indentWord)
            throw new IllegalArgumentException("-w or -c argument is required");

        if (!range.matches("^(\\d+-\\d+)|(\\d+-)|(\\|-\\d+)$")) {
//надо воодить |-int для задания диапозона от начала строки, т.к. если написать -int это будет считаться флагом
            throw new IllegalArgumentException("Incorrect range argument use int-int, |-int or int-");
        }

        int start = 1;
        int end = Integer.MAX_VALUE;
        String[] s = range.split("-");
        if (range.charAt(0) == '|')
            end = Integer.parseInt(s[1]);
        else {
            if (s.length == 2) {
                start = Integer.parseInt(s[0]);
                end = Integer.parseInt(s[1]);
            } else
                start = Integer.parseInt(s[0]);
        }
        if (end < start)
            throw new IllegalArgumentException("start of the range cannot be bigger then the end");

        Cutter cutter = new Cutter(start, end);
        String text;
        String result;

        try {

            if (inputName == null) {
                System.out.println("Enter your text. Print \"-end\" from a new line in the end");
                Scanner scanner = new Scanner(System.in);
                StringBuilder sb = new StringBuilder();
                String line = scanner.nextLine();
                while (!line.equals("-end")) {
                    sb.append(line);
                    sb.append("\n");
                    line = scanner.nextLine();
                }
                text = sb.deleteCharAt(sb.length() - 1).toString();
            } else {
                BufferedReader reader = new BufferedReader(new FileReader(inputName));
                StringBuilder sb = new StringBuilder();
                String line = reader.readLine();
                while (line != null) {
                    sb.append(line);
                    line = reader.readLine();
                    if (line != null)
                        sb.append("\n");
                }
                reader.close();
                text = sb.toString();
            }

            if (indentChar)
                result = cutter.cutChars(text);
            else
                result = cutter.cutWords(text);

            if (outputName == null)
                System.out.print(result);
            else {
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputName));
                writer.write(result);
                writer.close();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

}
