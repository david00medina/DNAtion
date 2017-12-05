package org.DNAtion.control.alignment.Picard;

import htsjdk.samtools.SAMFileHeader;
import picard.sam.BuildBamIndex;
import picard.sam.SortSam;
import picard.sam.markduplicates.MarkDuplicates;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Picard {
    private int version;
    private boolean buildIndex;
    private File sam;
    private File bam;
    private File sortedBam;
    private File dedupBam;
    private File indexTarget;

    public Picard(int version, File sam, File bam, boolean buildIndex) {
        this.version = version;
        this.buildIndex = buildIndex;

        this.sam = sam;
        this.bam = bam;
        this.sortedBam = new File(sam.getAbsolutePath().
                substring(0, bam.getAbsolutePath().toLowerCase().indexOf(".bam")) + "_sorted.bam");
        this.dedupBam = new File(sortedBam.getAbsolutePath().
                substring(0, sortedBam.getAbsolutePath().toLowerCase().indexOf(".bam")) + "_dedup.bam");
    }

    public void execSortAndConvert() {
        SortSam sortSam = new SortSam();

        sortSam.INPUT = sam;
        sortSam.OUTPUT = sortedBam;
        sortSam.SORT_ORDER = SAMFileHeader.SortOrder.coordinate;

        sortSam.instanceMainWithExit(new String[]{});

        if (buildIndex) indexTarget = sortedBam;
    }

    public void execMarkDuplicates() {
        MarkDuplicates markDuplicates = new MarkDuplicates();

        List<String> inputList = new ArrayList<>();
        inputList.add(sortedBam.getAbsolutePath());
        System.out.println(sortedBam.getAbsolutePath());
        markDuplicates.INPUT = inputList;
        markDuplicates.OUTPUT = dedupBam;
        markDuplicates.METRICS_FILE = new File(dedupBam.getAbsolutePath().
                substring(0, sortedBam.getAbsolutePath().indexOf(".bam")) + "_metrics.bam");
        markDuplicates.TAG_DUPLICATE_SET_MEMBERS = true;
        markDuplicates.TAGGING_POLICY = MarkDuplicates.DuplicateTaggingPolicy.All;
        markDuplicates.ASSUME_SORT_ORDER = SAMFileHeader.SortOrder.coordinate;
        if (version == 2) {
            markDuplicates.READ_NAME_REGEX = "[a-zA-Z0-9]+:[0-9]:([0-9]+):([0-9]+):([0-9]+).*";
                    /*"/(?=^(?:[^:\\s]+:){4}([^:\\s]+))"
                            + "(?=^(?:[^:\\s]+:){5}([^:\\s]+))"
                            + "(?=^(?:[^:\\s]+:){6}([^:\\s]+))/g";*/
        }

        markDuplicates.instanceMain(new String[]{});

        if (buildIndex) indexTarget = dedupBam;
    }

    public void execBuildBamIndex() {
        BuildBamIndex buildBamIndex = new BuildBamIndex();
        buildBamIndex.INPUT = indexTarget.getAbsolutePath();
        buildBamIndex.CREATE_INDEX = true;

        buildBamIndex.instanceMain(new String[]{});
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public File getSortedBam() {
        return sortedBam;
    }

    public void setSortedBam(File sortedBam) {
        this.sortedBam = sortedBam;
    }

    public File getDedupBam() {
        return dedupBam;
    }

    public void setDedupBam(File dedupBam) {
        this.dedupBam = dedupBam;
    }

    public File getIndexTarget() {
        return indexTarget;
    }

    public void setIndexTarget(File indexTarget) {
        this.indexTarget = indexTarget;
    }
}
