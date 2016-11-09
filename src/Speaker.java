import java.io.IOException;

final class Speaker {

    static void say(String speech) {
        try {
            Runtime.getRuntime().exec("say " + speech);
        } catch (IOException e) {
            System.err.println("Speech failed due to exception : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        say("Hello");
    }
}
