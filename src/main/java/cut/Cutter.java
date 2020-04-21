package cut;

import java.util.ArrayList;
import java.util.List;

public class Cutter {
    private int start, end;

    public Cutter(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public ArrayList<String> cutChars(List<String> lines) {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> result = new ArrayList<>();
        for (String line : lines) {
            if (line.length() > start && line.length() >= end)
                sb.append(line, start - 1, end);
            else {
                if (line.length() > start)
                    sb.append(line, start - 1, line.length());
            }
            result.add(sb.toString());
            sb = new StringBuilder();
        }
        return result;
    }

    public ArrayList<String> cutWords(List<String> lines) {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> result = new ArrayList<>();
        for (String line : lines) {
            String[] words = line.split("\\s+");
            if (words.length >= start) {
                for (int i = start - 1; (i < end && i < words.length); i++)
                    sb.append(words[i]).append(" ");
                sb.deleteCharAt(sb.length() - 1);
            }
            result.add(sb.toString());
            sb = new StringBuilder();
        }
        return result;
    }

}