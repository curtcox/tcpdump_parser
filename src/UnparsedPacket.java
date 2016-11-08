final class UnparsedPacket {

    final String line;
    final String[] dump;

    private UnparsedPacket(String line, String[] dump) {
        this.line = line;
        this.dump = dump;
    }

    static UnparsedPacket of(String line, String[] dump) {
        return new UnparsedPacket(line,dump);
    }

    static UnparsedPacket of(String line) {
        return new UnparsedPacket(line,new String[0]);
    }

}
