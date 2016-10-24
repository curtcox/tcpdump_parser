
public class Main {

    public static void main(String[] args) {
        Parser.parse(() -> System.in).forEach(packet -> System.out.println(packet));
    }

}
