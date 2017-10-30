package org.DNAtion.gui;

import org.DNAtion.control.alignment.Aligner;
import org.DNAtion.control.alignment.BurrowsWheeler.BWAligner;
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
        in = scanner.next();

        File file = new File(in);

        FASTQ sample_1 = new FASTQ(file);
        System.out.println(sample_1.getRgHeader());

        System.out.print("Introduzca segunda muestra biológica : ");
        in = scanner.next();

        file = new File(in);

        FASTQ sample_2 = new FASTQ(file);
        System.out.println(sample_2.getRgHeader());

        file = new File(GENOME);

        Aligner aligner = new BWAligner(file, sample_1, sample_2, "out.sam", "error.log");
    }

}