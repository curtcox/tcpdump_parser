final class RadioTap {

    final boolean badFcs;
    final boolean wep;
    final boolean cfp;
    final boolean shortPreamble;
    final boolean fragmented;

    private RadioTap(Builder builder) {
        badFcs = builder.badFcs;
        wep = builder.wep;
        cfp = builder.cfp;
        shortPreamble = builder.shortPreamble;
        fragmented = builder.fragmented;
    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        boolean badFcs;
        boolean wep;
        boolean cfp;
        boolean shortPreamble;
        boolean fragmented;

        RadioTap build() {
            return new RadioTap(this);
        }
    }

    static RadioTap parse(Fields fields) {
        Builder builder = builder();
        for (String field : fields) {
            if (field.equals("bad-fcs")) { builder.badFcs = true;}
            if (field.equals("fragmented")) {builder.fragmented = true;}
            if (field.equals("preamble")) {builder.shortPreamble = true;}
            if (field.equals("wep")) {builder.wep = true;}
            if (field.equals("cfp")) {builder.cfp = true;}
        }
        return builder.build();
    }
}
