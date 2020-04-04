import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;


class CutTest {

    private String outputFile = "files/temp.txt";
    private String inputFile = "files/testFile.txt";
    private String ls = System.lineSeparator();

    private void assertFileContent(String name, String expectedContent) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(name));
            String line = reader.readLine();
            while (line != null) {
                sb.append(line);
                line = reader.readLine();
                if (line != null)
                    sb.append("\n");
            }
            reader.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        String content = sb.toString();
        assertEquals(expectedContent, content);
    }

    @Test
    void cutWords() {
        Cut.main(new String[]{"-w", "|-6", inputFile, "-o", outputFile});
        assertFileContent("files/temp.txt", "one two three four five six\n" + "\n" +
                "hey Hey hooray! this is a");

        Cut.main(new String[]{"-w", "4-7", inputFile, "-o", outputFile});
        assertFileContent("files/temp.txt", "four five six seven\n" + "\n" + "this is a test");

        Cut.main(new String[]{"-w", "5-", inputFile, "-o", outputFile});
        assertFileContent("files/temp.txt", "five six seven eight nine ten elven!\n" + "\n" +
                "is a test file...");

        new File("files/temp.txt").delete();
    }

    @Test
    void cutChars() {
        Cut.main(new String[]{"-c", "-o", outputFile, "3-10", inputFile});
        assertFileContent("files/temp.txt", "e two th\n" + "\n" + "y Hey   ");

        Cut.main(new String[]{"-c", "-o", outputFile, "|-7", inputFile});
        assertFileContent("files/temp.txt", "one two\n" + "\n" + "hey Hey");

        Cut.main(new String[]{"-c", "-o", outputFile, "30-", inputFile});
        assertFileContent("files/temp.txt", "even eight nine ten elven!\n" + "\n" +
                "s a test file...");

        new File("files/temp.txt").delete();
    }

    @Test
    void errors() {

        assertThrows(IllegalArgumentException.class, () -> {
            Cut.main(new String[]{"-o", outputFile, "3-10", inputFile});
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Cut.main(new String[]{"-c", "-o", outputFile, "20-10", inputFile});
        });

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setErr(new PrintStream(output));
        Cut.main(new String[]{});
        assertEquals("Argument \"Range\" is required" + ls +
                        "java -jar cut.jar [-c IndentChars|-w IndentWords] [-o outputName] [InputName] range" + ls +
                        " Range          : Range of chars or words" + ls +
                        " InputName      : Input file name" + ls +
                        " -c IndentChars : indent in chars (default: false)" + ls +
                        " -o OutputName  : Output file name" + ls +
                        " -w IndentWords : indent in words (default: false)" + ls,
                output.toString());
    }

    @Test
    void console() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        ByteArrayInputStream input = new ByteArrayInputStream(("one two three four five six seven eight nine ten elven!" + ls +
                ls + "hey Hey        hooray! this is a test file..." + ls + "-end").getBytes());
        System.setIn(input);
        Cut.main(new String[]{"-c", "10-20"});
        assertEquals("Enter your text. Print \"-end\" from a new line in the end\r\nhree four f\n\n      hoora",
                output.toString());
    }

}
