package org.DNAtion.control.variants.GATK;

import org.DNAtion.control.preprocessing.Aligner;

import java.io.File;
import java.io.IOException;

public class VariantAnnotator {
    private File genome;
    private File vcf_in;
    private File vcf_out;
    private int cores;
    private File[] hapmap;
    private File[] omni;
    private File[] thousandG;
    private File[] dbSNP;
    private File[] mills_indel;
    private ProcessBuilder variantAnnotatorCmd;

    public VariantAnnotator(File genome, File vcf_in, File vcf_out,
                            File[] hapmap, File[] omni, File[] thousandG,
                            File[] dbSNP, File[] mills_indel) {
        this.genome = genome;
        this.vcf_in = vcf_in;
        this.vcf_out = vcf_out;
        this.hapmap = hapmap;
        this.omni = omni;
        this.thousandG = thousandG;
        this.dbSNP = dbSNP;
        this.mills_indel = mills_indel;
        cores = Runtime.getRuntime().availableProcessors();
    }

    private void buildCmd() {
        String[] cmd = {"java", "-jar", "-Xmx4g", "/media/uichuimi/DiscoInterno/David/DNAtion/GenomeAnalysisTK.jar",
                "-T", "VariantAnnotator",
                "-R", genome.getAbsolutePath(),
                "-A Coverage",
                "-A QualByDepth",
                "-A FisherStrand",
                "-A StrandOddsRatio",
                "-A MappingQualityRankSumTest",
                "-A ReadPosRankSumTest",
                "-A InbreedingCoeff",
                "-A RMSMappingQuality",
                "--input", vcf_in.getAbsolutePath(),
                "-o", vcf_out.getAbsolutePath()};
        variantAnnotatorCmd = new ProcessBuilder(cmd);
    }

    public void execAnnotateVariant() {
        System.out.println("Executing VariantAnnotator . . .");
        buildCmd();
        runProcess();
    }

    private int runProcess() {
        if (variantAnnotatorCmd == null)
            return Aligner.FAILURE;  // TODO: Modify this line
        variantAnnotatorCmd.redirectError(ProcessBuilder.Redirect.to(new File("VariantAnnotator.log")));
        try {
            Process process = variantAnnotatorCmd.start();
            int exeCode = process.waitFor();
            System.out.println("Program executed with any errors? "
                    + (exeCode == 0 ? "No" : "Yes"));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return Aligner.SUCCESS; // TODO: Create an interface to apply exit codes (do same for last TODO)
    }
}
