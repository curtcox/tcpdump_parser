final class Net {

    static String ping(Host host) {
        return Exec.command("ping " + host);
    }

    public static void main(String[] args) {
        System.out.println(ping(Host.of("localhost")));
    }
}
