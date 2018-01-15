package org.DNAtion.control.preprocessing.Picard;

import htsjdk.samtools.SAMFileHeader;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class RevertSam {
	private File bam;
	private File uBam;
	private double max_discard_fraction;
	private boolean sanitize;
	private boolean restore_qualities;
	private boolean remove_duplicate_info;
	private boolean remove_alignment_info;
	private String[] attribute_to_clear;

	public RevertSam(File bam, File uBam, double max_discard_fraction,
	                 boolean sanitize, boolean restore_qualities,
	                 boolean remove_duplicate_info, boolean remove_alignment_info,
	                 String... attribute_to_clear) {
		this.bam = bam;
		this.uBam = uBam;
		this.max_discard_fraction = max_discard_fraction;
		this.sanitize = sanitize;
		this.restore_qualities = restore_qualities;
		this.remove_duplicate_info = remove_duplicate_info;
		this.remove_alignment_info = remove_alignment_info;
		this.attribute_to_clear = attribute_to_clear;
	}

	private void execFasqToSam() {
		picard.sam.RevertSam revertSam = new picard.sam.RevertSam();
		DateTimeFormatter dtf = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

		revertSam.INPUT = bam.getAbsoluteFile();
		revertSam.OUTPUT = uBam.getAbsoluteFile();
		revertSam.SANITIZE = sanitize;
		revertSam.MAX_DISCARD_FRACTION = max_discard_fraction;
		revertSam.ATTRIBUTE_TO_CLEAR = Arrays.asList(attribute_to_clear);
		revertSam.RESTORE_ORIGINAL_QUALITIES = restore_qualities;
		revertSam.REMOVE_DUPLICATE_INFORMATION = remove_duplicate_info;
		revertSam.REMOVE_ALIGNMENT_INFORMATION = remove_alignment_info;
		revertSam.SORT_ORDER = SAMFileHeader.SortOrder.queryname;
		revertSam.instanceMain(new String[]{});
	}
}
