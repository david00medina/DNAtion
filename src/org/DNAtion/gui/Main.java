package org.DNAtion.gui;

import org.DNAtion.control.preprocessing.Bowtie2.Bowtie2;
import org.DNAtion.control.variants.GATK.*;
import org.DNAtion.control.preprocessing.Picard.Picard;
import org.DNAtion.model.FASTQ.FASTQ;
import org.DNAtion.model.SAM.SAMEnum_RG;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static final File HOME = new File(System.getProperty("user.home"));
    private static final String GENOME =
            "/media/uichuimi/DiscoInterno/references/GRCh38/GRCh38.fa";

    public static void main(String[] args) throws IOException {
        // Get input files (samples)
        String in;
        Scanner scanner = new Scanner(System.in);

        System.out.print("Introduzca primera muestra biológica : ");
        in = scanner.nextLine();

        File file = new File(in);
        System.out.println(file.getAbsolutePath());
        FASTQ sample_1 = new FASTQ(file);
        System.out.println(sample_1.getBwaHeader());

        System.out.print("Introduzca segunda muestra biológica : ");
        in = scanner.nextLine();

        file = new File(in);

        FASTQ sample_2 = new FASTQ(file);
        System.out.println(sample_2.getBwaHeader());
        System.out.println(sample_2.getRgFields().get(SAMEnum_RG.ID.getValue()));

        File genome = new File(GENOME);


        // Align DNA sequences on the reference genome
        Bowtie2 aligner = new Bowtie2(genome,
                sample_1,
                sample_2,
                new File("out.sam"),
                new File("error.log"));
        //aligner.buildGenome();
        /*if(aligner.alignSq() == Bowtie2.FAILURE)
            System.out.println("Error loading the ProcessBuilder");*/

        /*Aligner aligner = new BWAligner(genome, sample_1, sample_2,
                new File("./out.sam"), new File("./error.log"));*/

        /*if (aligner.alignSq() == BWAligner.FAILURE)
            System.out.println("Error loading the ProcessBuilder");*/


        // Sort and mark duplicates on DNA sequences
        File sam = aligner.getStdout();
        File bam = new File(sam.getAbsolutePath().
                substring(0, sam.getAbsolutePath().toLowerCase().indexOf(".sam")) + ".bam");

        Picard picard = new Picard(sample_1.getFastqHeader().getVersion(), sam, bam, true);
        /*picard.execSortAndConvert();
        picard.execMarkDuplicates();
        picard.execBuildBamIndex();*/


        // Base Recalibration
        File dedup = picard.getDedupBam();
        File recal = new File("recal_reads.bam");
        File[] references = {
                new File("/media/uichuimi/DiscoInterno/ResourceBundle/Mills_indel/GATK-resourcebundle/Mills_and_1000G_gold_standard.indels.hg38.vcf.gz"),
                new File("/media/uichuimi/DiscoInterno/ResourceBundle/dbSNP/TODO/All_20170710.vcf.gz")};
        System.out.println(dedup);
        BaseRecalibrator baseRecal = new BaseRecalibrator(genome, dedup, recal, true, true, references);
        //baseRecal.execBaseRecalibration();



        // Filter Variants databases
        File[] hapmap = {new File("/media/uichuimi/DiscoInterno/ResourceBundle/Hapmap/GATK-resourcebundle/hapmap_3.3.hg38.vcf.gz")};
        File[] omni = {new File("/media/uichuimi/DiscoInterno/ResourceBundle/Omni/GATK-resourcebundle/1000G_omni2.5.hg38.vcf.gz")};
        File[] thousandG = {new File("/media/uichuimi/DiscoInterno/ResourceBundle/1000G/GATK-resourcebundle/1000G_phase1.snps.high_confidence.hg38.vcf.gz")};
        File[] dbSNP = {new File("/media/uichuimi/DiscoInterno/ResourceBundle/dbSNP/TODO/All_20170710.vcf.gz")};
        File[] mills_indel = {new File("/media/uichuimi/DiscoInterno/ResourceBundle/Mills_indel/GATK-resourcebundle/Mills_and_1000G_gold_standard.indels.hg38.vcf.gz")};


        // Call variants/haplotypes
        File haploGVCF = new File("raw_variants_haplotype.g.vcf");
        HaplotypeCaller haplotypeCaller = new HaplotypeCaller(genome, recal, haploGVCF);
        //haplotypeCaller.execCallVariant();


        // Call joint genotyping
        File genotypeGVCF = new File("raw_variants.vcf");
        GenotypeGVCFs genotypeGVCFs = new GenotypeGVCFs(genome, haploGVCF, genotypeGVCF);
        genotypeGVCFs.callVariant();

        // Call VariantAnnotator
        File variantAnnotated = new File("raw_variants_annotated.vcf");
        VariantAnnotator variantAnnotator = new VariantAnnotator(genome, genotypeGVCF, variantAnnotated, hapmap, omni, thousandG, dbSNP, mills_indel);
        //variantAnnotator.execAnnotateVariant();


        // Call VariantRecalibrator
        File analysisFile = new File("recalibrated_variants.vcf");
        VariantRecalibrator variantRecalibrator = new VariantRecalibrator(genome, /*variantAnnotated*/genotypeGVCF,
                analysisFile, hapmap, omni, thousandG, dbSNP, mills_indel);
        variantRecalibrator.execRecalibration_SNP();
        //variantRecalibrator.execApplyRecalibration_SNP();
        //variantRecalibrator.execRecalibration_INDEL();
        //variantRecalibrator.execApplyRecalibration_INDEL();
    }

}