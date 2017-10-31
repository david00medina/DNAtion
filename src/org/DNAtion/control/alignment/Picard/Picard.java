package org.DNAtion.control.alignment.Picard;

import htsjdk.samtools.SAMFileHeader;
import picard.sam.SortSam;

import java.io.File;
import java.io.IOException;

public class Picard {
    private File sam;
    private File bam;
    private File sortedBam;

    public Picard(File sam, File bam) {
        this.sam = sam;
        this.bam = bam;
        this.sortedBam = new File(sam.getParent() + "/out_sorted.bam");
    }

    public void sortAndConvert() {
        SortSam sortSam = new SortSam();

        sortSam.INPUT = sam;
        sortSam.OUTPUT = sortedBam;
        sortSam.SORT_ORDER = SAMFileHeader.SortOrder.coordinate;

        sortSam.instanceMainWithExit(new String[]{});
    }

    public File getSam() {
        return sam;
    }

    public void setSam(File sam) {
        this.sam = sam;
    }

    public File getBam() {
        return bam;
    }

    public void setBam(File bam) {
        this.bam = bam;
    }
}
