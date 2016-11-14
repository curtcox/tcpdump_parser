import java.io.*;
import java.util.*;

final class Exec {

    static String command(String command) {
        List<String> result = command(command,1);
        return result.size() == 0 ? "" : result.get(0);
    }

    static List<String> command(String command, int maxlines) {
        try {
            System.out.println(new Date() + " " + command);
            return output(Runtime.getRuntime().exec(command),maxlines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> output(Process process, int maxLines) throws IOException {
        List<String> lines = new ArrayList();
        InputStream in = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line=reader.readLine()) != null && lines.size() < maxLines) {
            lines.add(line);
        }

        return lines;
    }

}
