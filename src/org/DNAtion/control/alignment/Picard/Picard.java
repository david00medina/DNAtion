package org.DNAtion.control.alignment.Picard;

import htsjdk.samtools.SAMFileHeader;
import org.DNAtion.model.FASTQ.FASTQ;
import picard.sam.SortSam;
import picard.sam.markduplicates.MarkDuplicates;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Picard {
    private int version;
    private File sam;
    private File bam;
    private File sortedBam;
    private File dedupBam;

    public Picard(int version, File sam, File bam) {
        this.version = version;

        this.sam = sam;
        this.bam = bam;
        this.sortedBam = new File(sam.getAbsolutePath().
                substring(0, bam.getAbsolutePath().indexOf(".bam")) + "_sorted.bam");
        this.dedupBam = new File(sortedBam.getAbsolutePath().
                substring(0, sortedBam.getAbsolutePath().indexOf(".bam")) + "_dedup.bam");
    }

    public void execSortAndConvert() {
        SortSam sortSam = new SortSam();

        sortSam.INPUT = sam;
        sortSam.OUTPUT = sortedBam;
        sortSam.SORT_ORDER = SAMFileHeader.SortOrder.coordinate;

        sortSam.instanceMainWithExit(new String[]{});
    }

    public void execMarkDuplicates() {
        MarkDuplicates markDuplicates = new MarkDuplicates();

        List<String> inputList = new ArrayList<>();
        inputList.add(sortedBam.getAbsolutePath());
        markDuplicates.INPUT = inputList;
        markDuplicates.OUTPUT = dedupBam;
        markDuplicates.METRICS_FILE = new File(dedupBam.getAbsolutePath().
                substring(0, sortedBam.getAbsolutePath().indexOf(".bam")) + "_metrics.bam");
        markDuplicates.TAG_DUPLICATE_SET_MEMBERS = true;
        markDuplicates.TAGGING_POLICY = MarkDuplicates.DuplicateTaggingPolicy.All;
        markDuplicates.ASSUME_SORT_ORDER = SAMFileHeader.SortOrder.coordinate;
        if (version == 2) {
            markDuplicates.READ_NAME_REGEX =
                    "/(?=^(?:[^:\\s]+:){4}([^:\\s]+))"
                            + "(?=^(?:[^:\\s]+:){5}([^:\\s]+))"
                            + "(?=^(?:[^:\\s]+:){6}([^:\\s]+))/y";
        }

        markDuplicates.instanceMain(new String[]{});
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
