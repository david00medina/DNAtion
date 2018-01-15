package org.DNAtion.model.FASTQ;

public enum FASTQEnum_v2 {
    MACHINE("Instrument ID"),
    RUNID("Run ID"),
    FLOWCELLID("Flowcell ID"),
    FLOWCELL("Flowcell Lane"),
    TILE_NUMB("Tile Number within the Flowcell"),
    XCORD("X-coord"),
    YCOORD("Y-coord"),
    MEMB_PAIR("Member of a Pair"),
    FILTER("Filter"),
    CTRL_BITS("Control Bits"),
    INDEX_SEQ("Index Sequence");

    private String value;

    FASTQEnum_v2(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String getEnumByString(String code) {
        for (FASTQEnum_v2 e : FASTQEnum_v2.values()) {
            if (code.equals(e.value)) return e.name();
        }
        return null;
    }
}