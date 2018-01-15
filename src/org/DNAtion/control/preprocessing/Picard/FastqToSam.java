package org.DNAtion.control.preprocessing.Picard;

import org.DNAtion.model.FASTQ.FASTQ;
import org.DNAtion.model.SAM.SAMEnum_RG;

import java.io.File;
import java.time.format.DateTimeFormatter;

public class FastqToSam {
	private FASTQ sample1;
	private FASTQ sample2;
	private File uBam;

	public FastqToSam(FASTQ sample1, FASTQ sample2, File uBam) {
		this.sample1 = sample1;
		this.sample2 = sample2;
		this.uBam = uBam;
	}

	private void execFasqToSam() {
		picard.sam.FastqToSam fastqToSam = new picard.sam.FastqToSam();
		DateTimeFormatter dtf = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

		fastqToSam.FASTQ = sample1.getFile().getAbsoluteFile();
		fastqToSam.FASTQ2 = sample2.getFile().getAbsoluteFile();
		fastqToSam.OUTPUT = uBam.getAbsoluteFile();
		fastqToSam.READ_GROUP_NAME = sample1.getRgFields().get(SAMEnum_RG.ID.getValue());
		fastqToSam.SAMPLE_NAME = sample1.getRgFields().get(SAMEnum_RG.SM.getValue());
		fastqToSam.LIBRARY_NAME = sample1.getRgFields().get(SAMEnum_RG.LB.getValue());
		fastqToSam.PLATFORM_UNIT = sample1.getRgFields().get(SAMEnum_RG.PU.getValue());
		fastqToSam.PLATFORM = sample1.getRgFields().get(SAMEnum_RG.PL.getValue());

		fastqToSam.instanceMain(new String[]{});
	}
}
