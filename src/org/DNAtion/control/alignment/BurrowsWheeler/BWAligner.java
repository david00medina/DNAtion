package org.DNAtion.control.alignment.BurrowsWheeler;

import org.DNAtion.control.alignment.Aligner;
import org.DNAtion.model.FASTQ.FASTQ;

import java.io.*;

public class BWAligner implements Aligner {
    private File genome;
    private FASTQ sampleSq_1;
    private FASTQ sampleSq_2;
    private String stdout;
    private String stderr;
    private ProcessBuilder alignCmd;
    private int cores;

    private static final int SUCCESS = 0;
    private static final int FAILURE = -1;

    public BWAligner(File genome, FASTQ sampleSq_1, FASTQ sampleSq_2, String stdout, String stderr) {
        this.genome = genome;
        this.sampleSq_1 = sampleSq_1;
        this.sampleSq_2 = sampleSq_2;
        this.stdout = stdout;
        this.stderr = stderr;
        cores = Runtime.getRuntime().availableProcessors();
        if (alignSq() == FAILURE)
            System.out.println("Error loading the ProcessBuilder");
    }

    public BWAligner(File genome, FASTQ sampleSq_1, String stdout) {
        this.genome = genome;
        this.sampleSq_1 = sampleSq_1;
        this.stdout = stdout;
        alignSq();
    }

    public int setSampleSq_1(FASTQ sample_1) {
        if (sample_1 == null)
            return FAILURE;

        this.sampleSq_1 = sample_1;
        return SUCCESS;
    }

    public int setSampleSq_2(FASTQ sample_2) {
        if (sample_2 == null)
            return FAILURE;

        this.sampleSq_2 = sample_2;
        return SUCCESS;
    }

    public int setSampleSq(FASTQ sample_1, FASTQ sample_2) {
        if (sample_1 == null || sample_2 == null)
            return FAILURE;

        this.sampleSq_1 = sample_1;
        this.sampleSq_2 = sample_2;
        return SUCCESS;
    }

    public int setReferenceGenome(File genome) {
        if (genome == null)
            return FAILURE;

        this.genome = genome;
        return SUCCESS;
    }

    private void buildCmd() {
        /*if(sampleSq_1 == null)
            return null;*/

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

    private int alignSq() {
        buildCmd();

        if (alignCmd == null) return FAILURE;

        File err = new File(stderr);
        File outFile = new File(stdout);
        alignCmd.redirectError(ProcessBuilder.Redirect.to(err));
        alignCmd.redirectOutput(ProcessBuilder.Redirect.to(outFile));

        try {
            Process process = alignCmd.start();
            int exeCode = process.waitFor();
            System.out.println("Program executed with any errors?"
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

    public File getGenome() {
        return genome;
    }

    public void setGenome(File genome) {
        this.genome = genome;
    }

    public FASTQ getSampleSq_1() {
        return sampleSq_1;
    }

    public FASTQ getSampleSq_2() {
        return sampleSq_2;
    }

    public String getStdout() {
        return stdout;
    }

    public int getCores() {
        return cores;
    }

    public void setCores(int cores) {
        this.cores = cores;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    public ProcessBuilder getAlignCmd() {
        return alignCmd;
    }

    public void setAlignCmd(ProcessBuilder alignCmd) {
        this.alignCmd = alignCmd;
    }
}