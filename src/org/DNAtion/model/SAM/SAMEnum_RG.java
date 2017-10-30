package org.DNAtion.model.SAM;

public enum SAMEnum_RG {
    ID("Read Group Identifier"),
    PU("Platform Unit"),
    SM("Sample"),
    PL("Platform Used"),
    LB("Library");

    private String value;

    SAMEnum_RG(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String getEnumByString(String code) {
        for (SAMEnum_RG e : SAMEnum_RG.values()) {
            if (code.equals(e.value)) return e.name();
        }
        return null;
    }
}