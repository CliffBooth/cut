package cut;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.*;
import java.util.*;

public class Cut {

    @Option(name = "-o", metaVar = "OutputName", usage = "Output file name")
    private File outputName;

    @Option(name = "-c", metaVar = "IndentChars", usage = "indent in chars", forbids = {"-w"})
    private boolean indentChar;

    @Option(name = "-w", metaVar = "IndentWords", usage = "indent in words", forbids = {"-c"})
    private boolean indentWord;

    @Argument(required = true, metaVar = "Range", usage = "Range of chars or words")
    private String range;

    @Argument(metaVar = "InputName", index = 1, usage = "Input file name")
    private File inputName;


    public static void main(String[] args) throws IOException {
        new Cut().launch(args);
    }

    private void launch(String[] args) throws IOException {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java -jar cut.jar [-c IndentChars|-w IndentWords] [-o outputName] [InputName] range");
            parser.printUsage(System.err);
            return;
        }

        if (!indentChar && !indentWord) {
            System.err.print("-w or -c argument is required");
            return;
        }

        if (!range.matches("^(\\d+-\\d+)|(\\d+-)|(\\|-\\d+)$")) {
//надо воодить |-int для задания диапозона от начала строки, т.к. если написать -int это будет считаться флагом
            System.err.print("Incorrect range argument use int-int, |-int or int-");
            return;
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
        if (end < start) {
            System.err.print("start of the range cannot be bigger then the end");
            return;
        }

        Cutter cutter = new Cutter(start, end);
        String line;
        List<String> lines = new ArrayList<>();


        if (inputName == null) {
            System.out.println("Enter your text. Print \"-end\" from a new line in the end");
            Scanner scanner = new Scanner(System.in);
            line = scanner.nextLine();
            while (!line.equals("-end")) {
                lines.add(line);
                line = scanner.nextLine();
            }
        } else {
            try (BufferedReader reader = new BufferedReader(new FileReader(inputName))) {
                line = reader.readLine();
                while (line != null) {
                    lines.add(line);
                    line = reader.readLine();
                }
            }
        }

        if (indentChar)
            lines = cutter.cutChars(lines);
        else
            lines = cutter.cutWords(lines);

        if (outputName == null) {
            for (int i = 0; i < lines.size() - 1; i++)
                System.out.println(lines.get(i));
            if (lines.size() > 0)
                System.out.print(lines.get(lines.size() - 1));
        } else {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputName))) {
                for (int i = 0; i < lines.size() - 1; i++)
                    writer.write(lines.get(i) + System.lineSeparator());
                if (lines.size() > 0)
                    writer.write(lines.get(lines.size() - 1));
            }
        }

    }

}