package compression;

public enum RuleProbType {

    STATIC("static"), ADAPTIVE("adaptive"), SEMI_ADAPTIVE("semi-adaptive"),
    STATIC_FROM_FILE("static-from-file");

    private String name;

    RuleProbType(final String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return name;
    }

    public static RuleProbType fromString(String text) {
        switch(text) {
            case "static":
            case "STATIC":
                return STATIC;
            case "static-file":
            case "static-from-file":
            case "STATIC_FILE":
            case "STATIC_FROM_FILE":
                return STATIC_FROM_FILE;
            case "adaptive":
            case "ADAPTIVE":
                return ADAPTIVE;
            case "semi-adaptive":
            case "SEMI_ADAPTIVE":
            case "SEMI-ADAPTIVE":
            case "semiadaptive":
            case "semi_adaptive":
                return SEMI_ADAPTIVE;
            default: throw new IllegalArgumentException("Unknown rule probability type: " + text);
        }
    }

}

