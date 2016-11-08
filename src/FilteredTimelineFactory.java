import java.io.*;
import java.util.*;
import java.util.function.*;

final class FilteredTimelineFactory {

    final String inputFile = "/tmp/capture.txt";
    final String[] specialHosts = new String[] {"wifi"};

    Timeline timeline() {
        return Timeline.of(ipPackets());
    }

    Packets packets() {
        return Parser.parse(input()).reliable();
    }

    Packets ipPackets() {
        Set<Host> hosts = hostsWeCareAbout();
        return packets().IP().filter(p -> p.containsAny(hosts));
    }

    Set<Host> hostsWeCareAbout() {
        return packets().IP().filter(p -> serversWeCareAbout(p)).allHosts();
    }

    boolean serversWeCareAbout(Packet packet) {
        IP ip = packet.ip;
        return serversWeCareAbout(ip.source) || serversWeCareAbout(ip.destination);
    }

    boolean serversWeCareAbout(Socket socket) {
        for (String match : specialHosts) {
            if (socket.host.name.contains(match)) {
                return true;
            }
        }
        return false;
    }

    Supplier<InputStream> input() {
        return () -> {
            try {
                return new FileInputStream(new File(inputFile));
            } catch (FileNotFoundException e){
                throw new RuntimeException(e);
            }
        };
    }

}
