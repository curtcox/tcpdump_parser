interface MacTracker {
    interface Listener {
        void onNewMacAbsence(MacDetectedEvent event);
        void onNewMacPresence(MacDetectedEvent event);
        void onMacDetected(MacDetectedEvent event);
    }
}
