final class Type {
    static String parse(Fields fields) {
        for (int i =0; i< fields.length(); i++) {
            String field = fields.at(i);
            if (field.equals("BA") ||
                field.equals("Beacon") ||
                field.equals("Clear-To-Send") ||
                field.equals("Request-To-Send") ||
                field.equals("LLC") ||
                field.equals("Unknown"))
            {
                return field;
            }
            if (field.equals("LLC,")) {
                return "LLC";
            }
            if (field.equals("DeAuthentication:")) {
                return "DeAuthentication";
            }
            if (field.equals("Probe") && fields.at(i + 1).equals("Request")) {
                return "Probe Request";
            }
            if (field.equals("Probe") && fields.at(i + 1).equals("Response")) {
                return "Probe Response";
            }
        }
        return null;
    }
}
