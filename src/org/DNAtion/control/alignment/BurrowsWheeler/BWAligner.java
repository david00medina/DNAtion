package org.DNAtion.control.alignment.BurrowsWheeler;

import org.DNAtion.control.alignment.Aligner;
import org.DNAtion.model.FASTQ.FASTQ;

import java.io.*;

public class BWAligner implements Aligner {
    private File genome;
    private FASTQ sampleSq_1;
    private FASTQ sampleSq_2;
    private File stdout;
    private File stderr;
    private ProcessBuilder alignCmd;
    private int cores;

    public BWAligner(File genome, FASTQ sampleSq_1, FASTQ sampleSq_2, File stdout, File stderr) {
        this.genome = genome;
        this.sampleSq_1 = sampleSq_1;
        this.sampleSq_2 = sampleSq_2;
        this.stdout = stdout;
        this.stderr = stderr;
        cores = Runtime.getRuntime().availableProcessors();
    }

    public BWAligner(File genome, FASTQ sampleSq_1, File stdout) {
        this.genome = genome;
        this.sampleSq_1 = sampleSq_1;
        this.stdout = stdout;
        alignSq();
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

    private void buildCmd() {

        ProcessBuilder pb;

        if (sampleSq_2 != null) {
            String[] bwa = {"bwa", "mem", "-M",
                    "-R", sampleSq_1.getRgHeader(),
                    "-t", Integer.toString(cores),
                    genome.getAbsolutePath(),
                    sampleSq_1.getFile().getAbsolutePath()};

            pb = new ProcessBuilder(bwa);
        } else {
            String[] bwa = {"bwa", "mem", "-M",
                    "-R", sampleSq_1.getRgHeader(),
                    "-t", Integer.toString(cores),
                    genome.getAbsolutePath(),
                    sampleSq_1.getFile().getAbsolutePath(),
                    sampleSq_2.getFile().getAbsolutePath()};

            pb = new ProcessBuilder(bwa);
        }
        alignCmd = pb;
    }

    @Override
    public int alignSq() {
        buildCmd();

        if (alignCmd == null) return FAILURE;

        alignCmd.redirectError(ProcessBuilder.Redirect.to(stderr));
        alignCmd.redirectOutput(ProcessBuilder.Redirect.to(stdout));

        try {
            Process process = alignCmd.start();
            int exeCode = process.waitFor();
            System.out.println("Program executed with any errors? "
                    + (exeCode == 0 ? "No" : "Yes"));

        } catch (IOException e) {
            System.out.println("Error loading the Process");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Process interrupted by an external signal");
            e.printStackTrace();
        }

        return SUCCESS;
    }

    @Override
    public String getAlignerID() {
        return "Burrows-Wheeler Aligner";
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
    public int getCores() {
        return cores;
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
    public File getStderr() {
        return stderr;
    }

    @Override
    public void setStderr(File stderr) {
        this.stderr = stderr;
    }

    @Override
    public ProcessBuilder getAlignCmd() {
        return alignCmd;
    }

    @Override
    public void setAlignCmd(ProcessBuilder alignCmd) {
        this.alignCmd = alignCmd;
    }
}