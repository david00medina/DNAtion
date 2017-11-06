package org.DNAtion.control.alignment.GATK;

import org.DNAtion.control.alignment.Aligner;
import org.apache.commons.lang.ArrayUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseRecalibration {
    private File genome;
    private File in;
    private File out;
    private File[] references;
    private int genomicInterval;
    private int cores;
    private ProcessBuilder recalCmd;

    public BaseRecalibration(File genome, File in, File out, File... references) {
        this.genome = genome;
        this.in = in;
        this.out = out;
        this.genomicInterval = 20;
        this.references = references;
        cores = Runtime.getRuntime().availableProcessors();
    }

    private void buildCmd() {
        List<String> cmdConstructor = new ArrayList<>();

        String[] cmd = {"java", "-jar", "GenomeAnalysisTK.jar",
                "-T", "BaseRecalibrator",
                "-R", genome.getAbsolutePath(),
                "-nct", String.valueOf(cores),
                "-I", in.getAbsolutePath(),
                "-L", Integer.toString(genomicInterval),
                /*"-knownSites", "/media/uichuimi/DiscoInterno/ResourceBundle/dbSNP",
                "-knownSites", "/media/uichuimi/DiscoInterno/ResourceBundle/Mills_indel/Mills_and_1000G_gold_standard.indels.hg38.vcf",*/
                "-o", out.getAbsolutePath()};

        cmdConstructor = Arrays.asList(cmd);

        for (int i = 0; i < references.length; i++) {
            cmdConstructor.add("-knownSites");
            cmdConstructor.add(references[i].getAbsolutePath());
        }

        recalCmd = new ProcessBuilder(cmdConstructor.toArray(new String[cmdConstructor.size()]));
    }

    public int applyBaseRecalibration() {
        buildCmd();

        if (recalCmd == null) return Aligner.FAILURE;  // TODO: Modify this line

        try {
            Process process = recalCmd.start();
            int exeCode = process.waitFor();
            System.out.println("Program executed with any errors? "
                    + (exeCode == 0 ? "No" : "Yes"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return Aligner.SUCCESS; // TODO: Create an interface to apply exit codes (do same for last TODO)
    }

    public File getGenome() {
        return genome;
    }

    public void setGenome(File genome) {
        this.genome = genome;
    }

    public File getIn() {
        return in;
    }

    public void setIn(File in) {
        this.in = in;
    }

    public File getOut() {
        return out;
    }

    public void setOut(File out) {
        this.out = out;
    }

    public int getGenomicInterval() {
        return genomicInterval;
    }

    public void setGenomicInterval(int genomicInterval) {
        this.genomicInterval = genomicInterval;
    }

    public String getID() {
        return "GATK BaseRecalibrator";
    }
}
