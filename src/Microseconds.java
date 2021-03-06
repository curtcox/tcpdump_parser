final class Microseconds {

    final long value;

    private Microseconds(long value) {
        this.value = value;
    }

    static boolean canParse(String value) {
        return value.endsWith("us") && !value.endsWith("ous") && isValidLong(value);
    }

    private static boolean isValidLong(String value) {
        try {
            Long.parseLong(value.substring(0,value.length() - 2));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    static Microseconds parse(String string) {
        try {
            String[] parts = string.split("u");
            return new Microseconds(Long.parseLong(parts[0]));
        } catch (NumberFormatException e) {
            return null;
        }
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
