import org.junit.Test;

import static org.junit.Assert.*;

public class HTTPTest {

    String sample1 = "There will length 194: HTTP";
    String sample2 = "be a *RANDOM* length 1460: HTTP";
    String sample3 = "number of tokens before length 237: HTTP: HTTP/1.1 200 OK";
    String sample4 = "the one that we length 229: HTTP: GET /generate_204/?unused=1475603635753 HTTP/1.1";
    String sample5 = "use to find the length 386: HTTP: POST /devhand/devicehandler.ashx HTTP/1.1";
    String sample6 = "length 105: HTTP: HTTP/1.1 204 No Content";
    String sample7 = "length 188: HTTP: HTTP/1.0 200 OK";

    @Test
    public void parse_returns_HTTP_when_sample_is_valid() {
        assertNotNull(parse(sample1));
        assertNotNull(parse(sample2));
        assertNotNull(parse(sample3));
        assertNotNull(parse(sample4));
        assertNotNull(parse(sample5));
        assertNotNull(parse(sample6));
    }

    @Test
    public void parse_returns_null_when_sample_is_NOT_valid() {
        assertNull(parse(""));
        assertNull(parse("this is"));
        assertNull(parse("not the key we are looking for"));
        assertNull(parse("Flags [Command], length 1524"));
    }

    @Test
    public void status() {
        assertStatus(sample1,null);
        assertStatus(sample2,null);
        assertStatus(sample3,200);
        assertStatus(sample4,null);
        assertStatus(sample5,null);
        assertStatus(sample6,204);
        assertStatus(sample7,200);
    }

    @Test
    public void url() {
        assertUrl(sample1,null);
        assertUrl(sample2,null);
        assertUrl(sample3,null);
        assertUrl(sample4,"/generate_204/?unused=1475603635753");
        assertUrl(sample5,"/devhand/devicehandler.ashx");
        assertUrl(sample6,null);
        assertUrl(sample7,null);
    }

    @Test
    public void length() {
        assertLength(sample1,194);
        assertLength(sample2,1460);
        assertLength(sample3,237);
        assertLength(sample4,229);
        assertLength(sample5,386);
        assertLength(sample6,105);
        assertLength(sample7,188);
    }

    @Test
    public void verb() {
        assertVerb(sample1,null);
        assertVerb(sample2,null);
        assertVerb(sample3,null);
        assertVerb(sample4,"GET");
        assertVerb(sample5,"POST");
        assertVerb(sample6,null);
        assertVerb(sample7,null);
    }

    void assertLength(String sample, int length) {
        assertEquals(length, parse(sample).length);
    }

    void assertStatus(String sample, Integer status) {
        assertEquals(status, parse(sample).status);
    }

    void assertVerb(String sample, String verb) {
        assertEquals(verb, parse(sample).verb);
    }

    void assertUrl(String sample, String url) {
        assertEquals(url, parse(sample).url);
    }

    HTTP parse(String sample) {
        return HTTP.parse(sample.split(" "));
    }
}
