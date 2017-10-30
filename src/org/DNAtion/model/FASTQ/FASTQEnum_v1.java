package org.DNAtion.model.FASTQ;

public enum FASTQEnum_v1 {
    MACHINE("Instrument ID"),
    FLOWCELL("Flowcell Lane"),
    TILE_NUMB("Tile Number within the Flowcell"),
    XCOORD("X-coord"),
    YCOORD("Y-coord"),
    MX_NUMB("Multiplex Index Number"),
    MEMB_PAIR("Member of a Pair");

    private String value;

    FASTQEnum_v1(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String getEnumByString(String code) {
        for (FASTQEnum_v1 e : FASTQEnum_v1.values()) {
            if (code.equals(e.value)) return e.name();
        }
        return null;
    }
}