final class Microseconds {

    final long value;

    private Microseconds(long value) {
        this.value = value;
    }

    static boolean canParse(String value) {
        return value.endsWith("us") && !value.endsWith("ous");
    }

    static Microseconds parse(String string) {
        String[] parts = string.split("u");
        return new Microseconds(Long.parseLong(parts[0]));
    }

    static Microseconds of(long value) {
        return new Microseconds(value);
    }

    public String toString() {
        return Long.toString(value);
    }

    public boolean equals(Object o) {
        Microseconds that = (Microseconds) o;
        return value == that.value;
    }

    public int hashCode() {
        return Long.hashCode(value);
    }

}
