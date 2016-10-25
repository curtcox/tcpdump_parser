import java.io.*;
import java.util.*;
import java.util.function.*;

public class Reports {

    final String inputFile = "/tmp/capture.txt";
    final String outputFile = "/tmp/report.txt";
    final String[] specialHosts = new String[] {"wifi"};
    PrintStream out = System.out;

    void writeToFile() {
        try {
            out = new PrintStream(new FileOutputStream(new File(outputFile)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Reports reports = new Reports();
        reports.printTimeline();
    }

    void printTimeline() {
        Timeline timeline = timeline();
        for (Channel channel : timeline.channels) {
            if (serversWeCareAbout(channel.server)) {
                print(channel);
                print(channel.transcript());
            }
        }
    }

    void dumpAllPackets() {
        Parser.parseValid(input())
                .forEach(packet -> print(packet));
    }

    void networkQuality() {
        long all = Parser.parseValid(input()).count();
        long reliable = Parser.parseValid(input()).reliable().count();
        double percent = (reliable  * 100.0 ) / all;
        print(reliable + " / " + all + " or " + percent + "% OK");
    }

    void listAllMacs() throws Exception {
        print(allMacs());
    }

    void listConversations() {
        for (Mac mac : packets().allClients()) {
            packets().contains(mac).forEach(x -> print(x));
        }
    }

    void listIpConversations() {
        for (Mac mac : ipPackets().allClients()) {
            ipPackets().contains(mac).forEach(x -> print(x));
        }
    }

    Timeline timeline() {
        return Timeline.of(ipPackets());
    }

    void summarizeIpConversations() {
        for (Mac mac : summarizeIpPackets().allClients()) {
            summarizeIpPackets().map(packet -> packet.localTime + " " + packet.ip ).forEach(x -> print(x));
        }
    }

    void countAllMacs() {
        print(allMacs().size());
    }

    void topMacsByAppearances(int count) {
        packets().macToCounts().entrySet().stream().limit(count)
                .forEach(x -> print(x));
    }

    void topMacPaths(int count) {
        packets().macPathsToCounts().entrySet().stream().limit(count)
                .forEach(x -> print(x));
    }

    void topIpPaths(int count) {
        ipPackets().ipPathsToCounts().entrySet().stream().limit(count)
                .forEach(x -> print(x));
    }

    void allAccessPoints() {
        packets().allAccessPoints()
                .forEach(bssid -> print(bssid));
    }

    void allAccessPointVendors() {
        packets().allAccessPointVendors()
                .forEach(vendor -> print(vendor));
    }

    Set<Mac> allMacs() {
        return packets().allMacs();
    }

    Packets packets() {
        return Parser.parseValid(input()).reliable();
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

    Packets summarizeIpPackets() {
        return ipPackets().summarize();
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

    void print(Object object) {
        out.println(object);
    }
}
