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
        builder.badFcs        = fields.contains("bad-fcs");
        builder.fragmented    = fields.contains("fragmented");
        builder.shortPreamble = fields.contains("preamble");
        builder.wep           = fields.contains("wep");
        builder.cfp           = fields.contains("cfp");
        return builder.build();
    }
}
