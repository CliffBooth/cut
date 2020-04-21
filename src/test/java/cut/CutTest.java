package cut;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;


class CutTest {

    private Path outputFile = Paths.get("temp.txt");
    private Path inputFile = Paths.get("src","test", "resources", "testFile.txt");
    private Path emptyFile = Paths.get("src","test", "resources", "emptyFile.txt");
    private String ls = System.lineSeparator();
    private PrintStream err = System.err;
    private PrintStream out = System.out;
    private InputStream in = System.in;

    private void assertFileContent(Path path, String expectedContent) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {
            String line = reader.readLine();
            while (line != null) {
                sb.append(line);
                line = reader.readLine();
                if (line != null)
                    sb.append(ls);
            }
        }
        String content = sb.toString().replaceAll(ls + "$", "");
        assertEquals(expectedContent, content);
    }

    @Test
    void cutWords() throws IOException {
        Cut.main(new String[]{"-w", "|-6", inputFile.toString(), "-o", outputFile.toString()});
        assertFileContent(outputFile, "one two three four five six" + ls + ls +
                "hey Hey hooray! this is a");

        Cut.main(new String[]{"-w", "4-7", inputFile.toString(), "-o", outputFile.toString()});
        assertFileContent(outputFile, "four five six seven" + ls + ls + "this is a test");

        Cut.main(new String[]{"-w", "5-", inputFile.toString(), "-o", outputFile.toString()});
        assertFileContent(outputFile, "five six seven eight nine ten elven!" + ls + ls +
                "is a test file...");

        Cut.main(new String[]{"-w", "1-", emptyFile.toString(), "-o", outputFile.toString()});
        assertFileContent(outputFile, "");

        new File(outputFile.toString()).delete();
    }

    @Test
    void cutChars() throws IOException {
        Cut.main(new String[]{"-c", "-o", outputFile.toString(), "3-10", inputFile.toString()});
        assertFileContent(outputFile, "e two th" + ls + ls + "y Hey   ");

        Cut.main(new String[]{"-c", "-o", outputFile.toString(), "|-7", inputFile.toString()});
        assertFileContent(outputFile, "one two" + ls + ls + "hey Hey");

        Cut.main(new String[]{"-c", "-o", outputFile.toString(), "30-", inputFile.toString()});
        assertFileContent(outputFile, "even eight nine ten elven!" + ls + ls +
                "s a test file...");

        new File(outputFile.toString()).delete();
    }

    @Test
    void errors() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setErr(new PrintStream(output));

        Cut.main(new String[] {"-o", outputFile.toString(), "3-10", inputFile.toString()});
        assertEquals("-w or -c argument is required", output.toString());
        output.reset();

        Cut.main(new String[] {"-c", "-o", outputFile.toString(), "20-10", inputFile.toString()});
        assertEquals("start of the range cannot be bigger then the end", output.toString());
        output.reset();

        Cut.main(new String[] {"-w", "-o", outputFile.toString(), "five-six", inputFile.toString()});
        assertEquals("Incorrect range argument use int-int, |-int or int-", output.toString());
        output.reset();

        System.setErr(err);
    }

    @Test
    void console() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        ByteArrayInputStream input = new ByteArrayInputStream(("one two three four five six seven eight nine ten elven!" + ls +
                ls + "hey Hey        hooray! this is a test file..." + ls + "-end").getBytes());
        System.setIn(input);
        Cut.main(new String[]{"-c", "10-20"});
        assertEquals("Enter your text. Print \"-end\" from a new line in the end" + ls +
                        "hree four f" + ls + ls + "      hoora",
                output.toString());

        output.reset();
        input = new ByteArrayInputStream(("-end").getBytes());
        System.setIn(input);
        Cut.main(new String[]{"-c", "10-20"});
        assertEquals("Enter your text. Print \"-end\" from a new line in the end" + ls, output.toString());

        System.setIn(in);
        System.setOut(out);
    }
}