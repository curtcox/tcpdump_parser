import java.time.LocalTime;

final class Packet {

    final LocalTime localTime;
    final Mac BSSID;

    Packet(LocalTime localTime, Mac BSSID) {
        this.localTime = localTime;
        this.BSSID     = BSSID;
    }
}
