import java.time.LocalTime;

final class Packet {

    final LocalTime localTime;
    final Mac BSSID;
    final Mac DA;

    private Packet(Builder builder) {
        localTime = builder.localTime;
        BSSID     = builder.BSSID;
        DA        = builder.DA;
    }

    static class Builder {
        LocalTime localTime;
        Mac BSSID;
        Mac DA;

        Packet build() {
            return new Packet(this);
        }
    }

    static Builder builder() {
        return new Builder();
    }
}
