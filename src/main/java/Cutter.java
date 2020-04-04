public class Cutter {
    private int start, end;

    public Cutter(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public String cutChars(String text) {
        String[] lines = text.split("\\n");
        StringBuilder result = new StringBuilder();
        for (String line : lines) {
            if (line.length() > start && line.length() >= end)
                result.append(line, start - 1, end);
            else {
                if (line.length() > start)
                    result.append(line, start - 1, line.length());
            }
            result.append("\n");
        }
        return result.deleteCharAt(result.length() - 1).toString();
    }

    public String cutWords(String text) {
        String[] lines = text.split("\\n");
        StringBuilder result = new StringBuilder();
        for (String line : lines) {
            String[] words = line.split("\\s+");

            if (words.length >= start) {
                for (int i = start - 1; (i < end && i < words.length); i++)
                    result.append(words[i]).append(" ");
                result.deleteCharAt(result.length() - 1);
            }

            result.append("\n");
        }
        return result.deleteCharAt(result.length() - 1).toString();
    }

}