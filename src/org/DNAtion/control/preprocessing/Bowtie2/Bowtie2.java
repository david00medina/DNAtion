package org.DNAtion.control.preprocessing.Bowtie2;

import org.DNAtion.control.preprocessing.Aligner;
import org.DNAtion.model.FASTQ.FASTQ;
import org.DNAtion.model.SAM.SAMEnum_RG;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

public class Bowtie2 implements Aligner {
	private File genome;
	private FASTQ sampleSq_1;
	private FASTQ sampleSq_2;
	private File stdout;
	private File stderr;
	private ProcessBuilder alignCmd;
	private int cores;

	private final static String BOWTIE2 =
			"/home/uichuimi/Programas/bowtie2-2.3.3.1/bowtie2";
	private final static String BUILDER =
			"/home/uichuimi/Programas/bowtie2-2.3.3.1/bowtie2-build";

	public Bowtie2(File genome, FASTQ sampleSq_1, FASTQ sampleSq_2, File stdout, File stderr) {
		this.genome = genome;
		this.sampleSq_1 = sampleSq_1;
		this.sampleSq_2 = sampleSq_2;
		this.stdout = stdout;
		this.stderr = stderr;
		cores = Runtime.getRuntime().availableProcessors();
	}

	public Bowtie2(File genome, FASTQ sampleSq_1, File stdout, File stderr) {
		this.genome = genome;
		this.sampleSq_1 = sampleSq_1;
		this.stdout = stdout;
		this.stderr = stderr;
		cores = Runtime.getRuntime().availableProcessors();
		alignSq();
	}


	@Override
	public int alignSq() {
		buildCmd();

		if (alignCmd == null) return FAILURE;

		alignCmd.redirectError(ProcessBuilder.Redirect.to(stderr));

		runProcess();

		return SUCCESS;
	}

	@Override
	public String getAlignerID() {
		return "Bowtie2 Aligner";
	}

	@Override
	public File getGenome() {
		return genome;
	}

	@Override
	public FASTQ getSampleSq_1() {
		return sampleSq_1;
	}

	@Override
	public FASTQ getSampleSq_2() {
		return sampleSq_2;
	}

	@Override
	public File getStdout() {
		return stdout;
	}

	@Override
	public File getStderr() {
		return stderr;
	}

	@Override
	public int getCores() {
		return cores;
	}

	@Override
	public ProcessBuilder getAlignCmd() {
		return alignCmd;
	}

	@Override
	public void setCores(int cores) {
		this.cores = cores;
	}

	@Override
	public void setStdout(File stdout) {
		this.stdout = stdout;
	}

	@Override
	public void setStderr(File stderr) {
		this.stderr = stderr;
	}

	@Override
	public void setAlignCmd(ProcessBuilder alignCmd) {
		this.alignCmd = alignCmd;
	}

	@Override
	public int setSampleSq_1(FASTQ sample_1) {
		if (sample_1 == null)
			return FAILURE;

		this.sampleSq_1 = sample_1;
		return SUCCESS;
	}

	@Override
	public int setSampleSq_2(FASTQ sample_2) {
		if (sample_2 == null)
			return FAILURE;

		this.sampleSq_2 = sample_2;
		return SUCCESS;
	}

	@Override
	public int setSampleSq(FASTQ sample_1, FASTQ sample_2) {
		if (sample_1 == null || sample_2 == null)
			return FAILURE;

		this.sampleSq_1 = sample_1;
		this.sampleSq_2 = sample_2;
		return SUCCESS;
	}

	@Override
	public int setReferenceGenome(File genome) {
		if (genome == null)
			return FAILURE;

		this.genome = genome;
		return SUCCESS;
	}

	private void buildGenomeCmd() {

		String[] builder = {BUILDER,
				"-f",
				"--threads", Integer.toString(cores),
				genome.getAbsolutePath(),
				"BOWTIE2"};

		alignCmd = new ProcessBuilder(builder);
	}

	private void buildCmd() {

		String[] builder = {BOWTIE2,
				"--threads", Integer.toString(cores),
				"--very-sensitive",
				"--rg-id", sampleSq_1.getRgFields().
					get(SAMEnum_RG.ID.getValue()),
				"--rg", SAMEnum_RG.PU + ":"
						+ sampleSq_1.getRgFields().get(SAMEnum_RG.PU.getValue()),
				"--rg", SAMEnum_RG.PL + ":"
						+ sampleSq_1.getRgFields().get(SAMEnum_RG.PL.getValue()),
				"--rg", SAMEnum_RG.SM + ":"
						+ sampleSq_1.getRgFields().get(SAMEnum_RG.SM.getValue()),
				"--rg", SAMEnum_RG.LB + ":"
						+ sampleSq_1.getRgFields().get(SAMEnum_RG.LB.getValue()),
				"--reorder",
				"-x", Paths.get(genome.getAbsolutePath()).getParent().toString() + "/BOWTIE2",
				"-1", sampleSq_1.getFile().getAbsolutePath(),
				"-2", sampleSq_2.getFile().getAbsolutePath(),
				"-S", stdout.getAbsolutePath()};

		alignCmd = new ProcessBuilder(builder);
	}


	public int buildGenome() {
		buildGenomeCmd();

		if (alignCmd == null) return FAILURE;
		alignCmd.redirectError(ProcessBuilder.Redirect.to(new File("./build-genome.log")));
		runProcess();
		return SUCCESS;
	}

	private void runProcess() {

		try {
			Process process = alignCmd.start();

			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = br.readLine()) != null)
				System.out.print(line);

			int exeCode = process.waitFor();
			System.out.println("Program executed with any errors? "
					+ (exeCode == 0 ? "No" : "Yes"));
		} catch (InterruptedException e) {
			System.out.println("Process interrupted by an external signal");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error loading the process");
			e.printStackTrace();
		}
	}

}
