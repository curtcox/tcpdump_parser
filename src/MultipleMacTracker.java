import java.util.function.*;

final class MultipleMacTracker implements Consumer<Packet> {

    static MultipleMacTracker of(MultipleMacTrackerTest.Listener listener) {
        return new MultipleMacTracker();
    }

    @Override
    public void accept(Packet packet) {

    }
}
