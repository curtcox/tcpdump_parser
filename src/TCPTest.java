import org.junit.Test;

import static org.junit.Assert.*;

public class TCPTest {

    String sample1 = "Flags [P.], seq 0:699, ack 1, win 832, options [nop,nop,TS val 828748516 ecr 798386358], length 699";
    String sample2 = "Flags [P.], seq 373279892:373280080, ack 3212565907, win 905, options [nop,nop,TS val 2424555772 ecr 49580065], length 188: HTTP: HTTP/1.0 200 OK";
    String sample3 = "Flags [.], ack 4, win 4191, options [nop,nop,TS val 1924602467 ecr 121440208], length 0";

    @Test
    public void parse_returns_null_when_not_TCP() {
        assertNull(parse(""));
        assertNull(parse("Not TCP"));
    }

    @Test
    public void flags() {
        assertFlags(sample1,"P.");
        assertFlags(sample2,"P.");
        assertFlags(sample3,".");
    }

    @Test
    public void options() {
        assertOptions(sample1,"nop,nop,TS val 828748516 ecr 798386358");
        assertOptions(sample2,"nop,nop,TS val 2424555772 ecr 49580065");
        assertOptions(sample3,"nop,nop,TS val 1924602467 ecr 121440208");
    }

    @Test
    public void seq() {
        assertSeq(sample1,"0:699");
        assertSeq(sample2,"373279892:373280080");
        assertSeq(sample3,null);
    }

    @Test
    public void ack() {
        assertAck(sample1,"1");
        assertAck(sample2,"3212565907");
        assertAck(sample3,"4");
    }

    void assertOptions(String sample, String options) {
        assertEquals(options,parse(sample).options);
    }

    void assertFlags(String sample, String flags) {
        assertEquals(flags,parse(sample).flags);
    }

    void assertAck(String sample, String ack) {
        assertEquals(ack,parse(sample).ack);
    }

    void assertSeq(String sample, String seq) {
        assertEquals(seq,parse(sample).seq);
    }

    TCP parse(String sample) {
        return TCP.parse(sample.split(" "));
    }

    @Test
    public void equal() {
        assertEqual(sample1);
        assertEqual(sample2);
        assertEqual(sample3);
    }

    @Test
    public void not_equal() {
        assertNotEqual(sample1,sample2);
        assertNotEqual(sample2,sample3);
        assertNotEqual(sample3,sample1);
    }

    void assertEqual(String sample) {
        TCP a = parse(sample);
        TCP b = parse(sample);
        assertEquals(a,b);
        assertEquals(b,a);
        assertEquals(a.hashCode(),b.hashCode());
    }

    void assertNotEqual(String sampleA, String sampleB) {
        TCP a = parse(sampleA);
        TCP b = parse(sampleB);
        assertNotEquals(a,b);
        assertNotEquals(b,a);
        assertNotEquals(a.hashCode(),b.hashCode());
    }

    @Test
    public void toString_does_not_show_null_fields() {
        TCP tcp = TCP.builder().build();
        assertEquals("TCP:{}",tcp.toString());
    }

    @Test
    public void toString_shows_all_fields_when_supplied() {
        TCP.Builder builder = TCP.builder();
        builder.ack     = "a1";
        builder.seq     = "s2";
        builder.flags   = "f3";
        builder.options = "o4";
        TCP tcp = builder.build();
        assertEquals("TCP:{ack=a1, flags=f3, options=o4, seq=s2}",tcp.toString());
    }

}
