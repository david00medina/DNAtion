package org.DNAtion.model.FASTQ;

public enum FASTQEnum_v3 {
    MACHINE("Machine ID"),
    FLOWCELL("Flowcell Lane"),
    TILE_NUMB("Tile Number within the Flowcell"),
    XCORRD("X-coord"),
    YCOORD("Y-coord"),
    MX_NUMB("Multiplex Index Number"),
    MEMB_PAIR("Member of a Pair");

    private String value;

    FASTQEnum_v3(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String getEnumByString(String code) {
        for (FASTQEnum_v3 e : FASTQEnum_v3.values()) {
            if (code.equals(e.value)) return e.name();
        }
        return null;
    }
}