import java.io.*;
import java.net.*;

final class UDPMulticaster {

    final DatagramSocket socket;
    final InetAddress    group;
    // between 224.0.0.1 and 239.255.255.254
    final String multicastAddress = "224.225.226.227";
    static UDPMulticaster broadcaster = new UDPMulticaster();

    private UDPMulticaster() {
        try {
            socket = new DatagramSocket(4445);
            group  = InetAddress.getByName(multicastAddress);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void broadcast(String message) {
        try {
            socket.send(packet(message));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    DatagramPacket packet(String message) {
        byte[] buffer = message.getBytes();
        return new DatagramPacket(buffer, buffer.length, group, 4446);
    }

    public static void main(String[] args) {
        broadcaster.broadcast("Hello");
    }
}
