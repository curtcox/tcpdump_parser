import java.io.IOException;
import java.util.Date;

final class Speaker {

    static void say(String speech) {
        Exec.command("say " + speech);
    }

    public static void main(String[] args) {
        say("Hello");
    }
}
