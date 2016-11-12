import java.util.*;

final class Type {

    final String value;
    final int hash;
    private static final Map<String,Type> types = new HashMap<>();
    static final Type LLC = Type.of("LLC");
    static final Type DeAuthentication = Type.of("DeAuthentication");
    static final Type Probe_Request = Type.of("Probe Request");
    static final Type Probe_Response = Type.of("Probe Response");

    private Type(String value, int hash) {
        this.value = value;
        this.hash = hash;
    }

    static Type of(String value) {
        if (value==null) {
            return null;
        }
        if (types.containsKey(value)) {
            return types.get(value);
        }
        Type type = new Type(value,types.size());
        types.put(value,type);
        return type;
    }

    static Type parse(Fields fields) {
        for (int i =0; i< fields.length(); i++) {
            String field = fields.at(i);
            if (field.equals("BA") ||
                field.equals("Beacon") ||
                field.equals("Clear-To-Send") ||
                field.equals("Request-To-Send") ||
                field.equals("LLC") ||
                field.equals("Unknown"))
            {
                return Type.of(field);
            }
            if (field.equals("LLC,"))              { return LLC; }
            if (field.equals("DeAuthentication:")) { return DeAuthentication; }
            if (field.equals("Probe") && fields.at(i + 1).equals("Request")) {
                return Probe_Request;
            }
            if (field.equals("Probe") && fields.at(i + 1).equals("Response")) {
                return Probe_Response;
            }
        }
        return null;
    }

    public int hashCode() {
        return hash;
    }

    public boolean equals(Object o) {
        return o==this;
    }
}
