import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class TimelineTest {

    @Test
    public void timeline_of_no_packets() {
        Timeline timeline = Timeline.of(Packets.of(() -> Collections.EMPTY_LIST.stream()));
        assertEquals(timeline.channels().size(),0);
    }

}
