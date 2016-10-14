import org.junit.Test;

import static org.junit.Assert.*;

public class HostTest {

    @Test
    public void hosts_compare_like_strings() {
        assertCompare("red","red");
        assertCompare("red","green");
        assertCompare("blue","green");
    }

    void assertCompare(String string1, String string2) {
        Host host1 = Host.of(string1);
        Host host2 = Host.of(string2);
        assertEquals(string1.compareTo(string2),host1.compareTo(host2));
    }

    @Test
    public void equal() {
        assertEqual("localhost");
        assertEqual("amazon.com");
        assertEqual("192.168.0.1");
    }

    @Test
    public void not_equal() {
        assertNotEqual("localhost","amazon.com");
        assertNotEqual("sample.com","192.168.0.1");
    }

    @Test
    public void toString_is_name() {
        assertString("localhost");
        assertString("amazon.com");
        assertString("amazon.com");
    }

    void assertEqual(String name) {
        Host a = host(name);
        Host b = host(name);
        assertEquals(a,a);
        assertEquals(a,b);
        assertEquals(b,a);
        assertEquals(a.hashCode(),b.hashCode());
    }

    void assertNotEqual(String name1, String name2) {
        Host a = host(name1);
        Host b = host(name2);
        assertNotEquals(a,b);
        assertNotEquals(b,a);
        assertNotEquals(a.hashCode(),b.hashCode());
    }

    void assertString(String name) {
        assertSame(name,host(name).toString());
    }

    Host host(String name) {
        return Host.of(name);
    }
}
