final class Mac {

    final String value;

    private Mac(String value) {
        this.value = value;
    }

    static Mac of(String value) {
        return new Mac(value);
    }

    @Override
    public boolean equals(Object o) {
        Mac that = (Mac) o;
        return value.equals(that.value);
    }

    @Override public String toString() { return value;            }
    @Override public int    hashCode() { return value.hashCode(); }
}
