package org.DNAtion.gui;

import org.DNAtion.control.alignment.Aligner;
import org.DNAtion.control.alignment.BurrowsWheeler.BWAligner;
import org.DNAtion.control.alignment.GATK.BaseRecalibration;
import org.DNAtion.control.alignment.Picard.Picard;
import org.DNAtion.model.FASTQ.FASTQ;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static final File HOME = new File(System.getProperty("user.home"));
    private static final String GENOME =
            "/media/uichuimi/DiscoInterno/references/GRCh38/GRCh38.fa";

    public static void main(String[] args) throws IOException {

        String in;
        Scanner scanner = new Scanner(System.in);

        System.out.print("Introduzca primera muestra biológica : ");
        in = scanner.nextLine();

        File file = new File(in);
        System.out.println(file.getAbsolutePath());
        FASTQ sample_1 = new FASTQ(file);
        System.out.println(sample_1.getRgHeader());

        System.out.print("Introduzca segunda muestra biológica : ");
        in = scanner.nextLine();

        file = new File(in);

        FASTQ sample_2 = new FASTQ(file);
        System.out.println(sample_2.getRgHeader());

        File genome = new File(GENOME);

        Aligner aligner = new BWAligner(genome, sample_1, sample_2,
                new File("./out.sam"), new File("./error.log"));

        /*if (aligner.alignSq() == BWAligner.FAILURE)
            System.out.println("Error loading the ProcessBuilder");*/

        File sam = aligner.getStdout();
        File bam = new File(sam.getAbsolutePath().
                toLowerCase().substring(0, sam.getAbsolutePath().toLowerCase().indexOf(".sam")) + ".bam");
        /*File sam = new File("./out.sam");
        File bam = new File("./out.bam");*/
        Picard picard = new Picard(sample_1.getFastqHeader().getVersion(), sam, bam, true);
        //picard.execSortAndConvert();
        picard.execMarkDuplicates();
        picard.execBuildBamIndex();

        File dedup = picard.getDedupBam();
        File recal = new File("./recal_data.table");

        File[] references = {
                new File("/media/uichuimi/DiscoInterno/ResourceBundle/Mills_indel/Mills_and_1000G_gold_standard.indels.hg38.vcf.gz"),
                new File("/media/uichuimi/DiscoInterno/ResourceBundle/dbSNP/All_20170710.vcf.gz"),
                new File("/media/uichuimi/DiscoInterno/ResourceBundle/1000G/1000G_omni2.5.hg38.vcf.gz")};

        BaseRecalibration baseRecal = new BaseRecalibration(genome, dedup, recal, references);
        baseRecal.applyBaseRecalibration();
    }

}