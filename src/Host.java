final class Host implements Comparable<Host> {

    final String name;
    final boolean privateIP;

    private Host(String name) {
        this.name = name;
        privateIP = isPrivate(name);
    }

    private static boolean isPrivate(String name) {
        return name.startsWith("10.") || name.startsWith("172.") || name.startsWith("192.168.");
    }

    static Host of(String name) {
        return new Host(name);
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean equals(Object o) {
        Host that = (Host) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public int compareTo(Host that) {
        return name.compareTo(that.name);
    }
}
