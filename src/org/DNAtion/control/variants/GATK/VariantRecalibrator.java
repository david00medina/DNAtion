package org.DNAtion.control.variants.GATK;

import org.DNAtion.control.preprocessing.Aligner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VariantRecalibrator {
	private File genome;
	private File vcf_in;
	private File vcfSNP;
	private File vcf_out;
	private File[] hapmap;
	private File[] omni;
	private File[] thousandG;
	private File[] dbSNP;
	private File[] mills_indel;
	private File recalSNP;
	private File tranchesSNP;
	private File rSNP;
	private File recalIndel;
	private File tranchesIndel;
	private File rIndel;
	private int cores;
	private ProcessBuilder variantRecalCmd;

	public VariantRecalibrator(File genome, File vcf_in, File vcf_out,
	                           File[] hapmap, File[] omni, File[] thousandG,
	                           File[] dbSNP, File[] mills_indel) {
		this.genome = genome;
		this.vcf_in = vcf_in;
		this.vcfSNP = new File("recalibrated_snps_raw_indels.vcf");
		this.vcf_out = vcf_out;
		this.hapmap = hapmap;
		this.omni = omni;
		this.thousandG = thousandG;
		this.dbSNP = dbSNP;
		this.mills_indel = mills_indel;
		recalSNP = new File("recalibrate_SNP.recal");
		tranchesSNP = new File("recalibrate_SNP.tranches");
		rSNP = new File("recalibrate_SNP_plots.R");
		recalIndel = new File("recalibrate_INDEL.recal");
		tranchesIndel = new File("recalibrate_INDEL.tranches");
		rIndel = new File("recalibrate_INDEL_plots.R");
		cores = Runtime.getRuntime().availableProcessors();
	}

	private void buildSNPCmd() {

		String[] cmd = {"java", "-jar", "-Xmx4g", "GenomeAnalysisTK.jar",
				"-T", "VariantRecalibrator",
				"-R", genome.getAbsolutePath(),
				"--input", vcf_in.getAbsolutePath(),
				"-nt", String.valueOf(cores),
				"-an", "DP",
				"-an", "QD",
				"-an", "FS",
				"-an", "SOR",
				"-an", "MQ",
				"-an", "MQRankSum",
				"-an", "ReadPosRankSum",
				"-an", "InbreedingCoeff",
				"-mode", "SNP",
				"-tranche", "100.0",
				"-tranche", "99.9",
				"-tranche", "99.0",
				"-tranche", "90.0",
				"-recalFile", recalSNP.getAbsolutePath(),
				"-tranchesFile", tranchesSNP.getAbsolutePath(),
				"-rscriptFile", rSNP.getAbsolutePath()};


		List<String> cmdList = new ArrayList<String>(Arrays.asList(cmd));
		cmdList.addAll(loadSNP());
		variantRecalCmd = new ProcessBuilder(cmdList.toArray(new String[cmdList.size()]));
	}

	private void buildIndelCmd() {

		String[] cmd = {"java", "-jar", "-Xmx4g", "GenomeAnalysisTK.jar",
				"-T", "VariantRecalibrator",
				"-R", genome.getAbsolutePath(),
				"--input", vcfSNP.getAbsolutePath(),
				"-nt", String.valueOf(cores),
				"-an", "DP",
				"-an", "QD",
				"-an", "FS",
				"-an", "SOR",
				"-an", "MQRankSum",
				"-an", "ReadPosRankSum",
				"-an", "InbreedingCoeff",
				"-mode", "INDEL",
				"-tranche", "100.0",
				"-tranche", "99.9",
				"-tranche", "99.0",
				"-tranche", "90.0",
				"--maxGaussians", "4",
				"-recalFile", recalIndel.getAbsolutePath(),
				"-tranchesFile", tranchesIndel.getAbsolutePath(),
				"-rscriptFile", rIndel.getAbsolutePath()};

		List<String> cmdList = new ArrayList<String>(Arrays.asList(cmd));
		cmdList.addAll(loadIndel());
		variantRecalCmd = new ProcessBuilder(cmdList.toArray(new String[cmdList.size()]));
	}

	private void buildApplySNPCmd() {
		String[] cmd = {"java", "-jar", "-Xmx4g", "GenomeAnalysisTK.jar",
				"-T", "ApplyRecalibration",
				"-R", genome.getAbsolutePath(),
				"--input", vcf_in.getAbsolutePath(),
				"-nt", String.valueOf(cores),
				"-mode", "SNP",
				"--ts_filter_level", "99.0",
				"-recalFile", recalSNP.getAbsolutePath(),
				"-tranchesFile", tranchesSNP.getAbsolutePath(),
				"-o", vcfSNP.getAbsolutePath()};

		variantRecalCmd = new ProcessBuilder(cmd);
	}

	private void buildApplyIndelCmd() {
		String[] cmd = {"java", "-jar", "-Xmx4g", "GenomeAnalysisTK.jar",
				"-T", "ApplyRecalibration",
				"-R", genome.getAbsolutePath(),
				"--input", vcfSNP.getAbsolutePath(),
				"-nt", String.valueOf(cores),
				"-mode", "INDEL",
				"--ts_filter_level", "99.0",
				"-recalFile", recalIndel.getAbsolutePath(),
				"-tranchesFile", tranchesIndel.getAbsolutePath(),
				"-o", vcf_out.getAbsolutePath()};

		variantRecalCmd = new ProcessBuilder(cmd);
	}

	private List<String> loadIndel() {
		List<String> cmdList = new ArrayList<>();

		for (int i = 0; i < mills_indel.length; i++) {
			cmdList.add("-resource:mills,known=false,training=true,truth=true,prior=12.0");
			cmdList.add(mills_indel[i].getAbsolutePath());
		}

		/*for (int i = 0; i < dbSNP.length; i++) {
			cmdList.add("-resource:dbsnp,known=true,training=false,truth=false,prior=2.0");
			cmdList.add(dbSNP[i].getAbsolutePath());
		}*/

		return cmdList;
	}

	private List<String> loadSNP() {
		List<String> cmdList = new ArrayList<>();


		for (int i = 0; i < hapmap.length; i++) {
			cmdList.add("-resource:hapmap,known=false,training=true,truth=true,prior=15.0");
			cmdList.add(hapmap[i].getAbsolutePath());
		}


		for (int i = 0; i < omni.length; i++) {
			cmdList.add("-resource:omni,known=false,training=true,truth=true,prior=12.0");
			cmdList.add(omni[i].getAbsolutePath());
		}

		for (int i = 0; i < thousandG.length; i++) {
			cmdList.add("-resource:1000G,known=false,training=true,truth=false,prior=10.0");
			cmdList.add(thousandG[i].getAbsolutePath());
		}

		for (int i = 0; i < dbSNP.length; i++) {
			cmdList.add("-resource:dbsnp,known=true,training=false,truth=false,prior=2.0");
			cmdList.add(dbSNP[i].getAbsolutePath());
		}

		return cmdList;
	}

	public void execRecalibration_SNP() {
		buildSNPCmd();
		runProcess();
	}

	public void execApplyRecalibration_SNP() {
		buildApplySNPCmd();
		runProcess();
	}

	public void execRecalibration_INDEL() {
		buildIndelCmd();
		runProcess();
	}

	public void execApplyRecalibration_INDEL() {
		buildApplyIndelCmd();
		runProcess();
	}

	private int runProcess() {
		System.out.println("Executing VariantRecalibrator . . .");
		if (variantRecalCmd == null)
			return Aligner.FAILURE;  // TODO: Modify this line
		variantRecalCmd.redirectError(ProcessBuilder.Redirect.to(new File("variantrecalibrator.log")));
		try {
			Process process = variantRecalCmd.start();
			int exeCode = process.waitFor();
			System.out.println("Program executed with any errors? "
					+ (exeCode == 0 ? "No" : "Yes"));
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return Aligner.SUCCESS; // TODO: Create an interface to apply exit codes (do same for last TODO)
	}
}
