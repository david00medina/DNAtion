package org.DNAtion.control.variants.GATK;

import org.DNAtion.control.preprocessing.Aligner;

import java.io.File;
import java.io.IOException;

public class HaplotypeCaller {
	private File genome;
	private File bam;
	private File gvcf;
	private ProcessBuilder callVariantCmd;
	private int call_threshold = 10;

	public HaplotypeCaller(File genome, File bam, File gvcf) {
		this.genome = genome;
		this.bam = bam;
		this.gvcf = gvcf;
	}

	private void buildCmd() {
		String[] cmd = {"java", "-jar", "GenomeAnalysisTK.jar",
				"-T", "HaplotypeCaller",
				"-R", genome.getAbsolutePath(),
				"-I", bam.getAbsolutePath(),
				"--genotyping_mode", "DISCOVERY",
				"-stand_call_conf", Integer.toString(call_threshold),
				"--emitRefConfidence", "GVCF",
				"-o", gvcf.getAbsolutePath(),
				"-ARO", "active_region.igv",
				"-APO", "raw_activity.igv"};

		callVariantCmd = new ProcessBuilder(cmd);
	}

	private int runProcess() {
		System.out.println("Executing HaplotypeCaler . . .");
		if (callVariantCmd == null) return Aligner.FAILURE;  // TODO: Modify this line

		try {
			Process process = callVariantCmd.start();
			int exeCode = process.waitFor();
			System.out.println("Program executed with any errors? "
					+ (exeCode == 0 ? "No" : "Yes"));
			if(exeCode != 0)
				return Aligner.FAILURE;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return Aligner.SUCCESS; // TODO: Create an interface to apply exit codes (do same for last TODO)
	}

	public void execCallVariant() {
		buildCmd();

		if(runProcess() == Aligner.FAILURE)
			System.out.println("Cannot run the HaplotypeCaller process");
	}
}
