import java.io.IOException;
import java.util.Date;

final class Speaker {

    static void say(String speech) {
        try {
            System.out.println(new Date() + " say " + speech);
            Runtime.getRuntime().exec("say " + speech);
        } catch (IOException e) {
            System.err.println("Speech failed due to exception : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        say("Hello");
    }
}
