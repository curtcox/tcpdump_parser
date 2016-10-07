final class IP {

    static IP parse(String[] parts) {
        if (!isValid(parts)) {
            return null;
        }
        return new IP();
    }

    static boolean isValid(String[] parts) {
        boolean ipFound = false;
        for (String part : parts) {
            if (ipFound && part.equals(">")) {
                return true;
            }
            if (part.startsWith("IP")) {
                ipFound = true;
            }
        }
        return false;
    }
}
