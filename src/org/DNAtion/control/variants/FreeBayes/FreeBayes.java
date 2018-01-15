package org.DNAtion.control.variants.FreeBayes;

import org.DNAtion.control.preprocessing.Aligner;

import java.io.File;
import java.io.IOException;

public class FreeBayes {
	private File genome;
	private File bam;
	private File gvcf;
	private ProcessBuilder callVariantCmd;

	public FreeBayes(File genome, File bam, File gvcf) {
		this.genome = genome;
		this.bam = bam;
		this.gvcf = gvcf;
	}

	private void buildCmd() {
		String cmd[] = {"freebayes",
				"-f", genome.getAbsolutePath(),
				"--gvcf",
				bam.getAbsolutePath()};

		callVariantCmd = new ProcessBuilder(cmd);
	}

	public void execVariantCall() {
		buildCmd();

		if(runProcess() == Aligner.FAILURE)
			System.out.println("Cannot run the HaplotypeCaller process");
	}

	private int runProcess() {
		if (callVariantCmd == null) return Aligner.FAILURE;  // TODO: Modify this line

		callVariantCmd.redirectOutput(ProcessBuilder.Redirect.to(gvcf));

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
}
