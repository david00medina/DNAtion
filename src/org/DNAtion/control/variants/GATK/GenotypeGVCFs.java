package org.DNAtion.control.variants.GATK;

import org.DNAtion.control.preprocessing.Aligner;

import java.io.File;
import java.io.IOException;

public class GenotypeGVCFs {
	private File genome;
	private File gvcf;
	private File vcf;
	private File[] references;
	private int cores;
	private ProcessBuilder jointGVCFsCmd;
	private int call_threshold = 10;

	public GenotypeGVCFs(File genome, File gvcf, File vcf) {
		this.genome = genome;
		this.gvcf = gvcf;
		this.vcf = vcf;
		this.cores = Runtime.getRuntime().availableProcessors();
	}

	public GenotypeGVCFs(File genome, File gvcf, File vcf, File... references) {
		this.genome = genome;
		this.gvcf = gvcf;
		this.vcf = vcf;
		this.references = references;
		this.cores = Runtime.getRuntime().availableProcessors();
	}

	private void buildCmd() {
		String[] cmd = {"java", "-jar", "GenomeAnalysisTK.jar",
				"-T", "GenotypeGVCFs",
				"-nt", String.valueOf(cores),
				"-R", genome.getAbsolutePath(),
				"-V", gvcf.getAbsolutePath(),
				"-o", vcf.getAbsolutePath()};

		jointGVCFsCmd = new ProcessBuilder(cmd);
	}

	private int runProcess() {
		System.out.println("Executing GenotypeGVCFs . . .");
		if (jointGVCFsCmd == null) return Aligner.FAILURE;  // TODO: Modify this line

		try {
			Process process = jointGVCFsCmd.start();
			int exeCode = process.waitFor();
			System.out.println("Program executed with any errors? "
					+ (exeCode == 0 ? "No" : "Yes"));
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return Aligner.SUCCESS; // TODO: Create an interface to apply exit codes (do same for last TODO)
	}

	public void callVariant() {
		buildCmd();

		if(runProcess() == Aligner.FAILURE)
			System.out.println("Cannot run the GenotypeGVCFs process");
	}
}
