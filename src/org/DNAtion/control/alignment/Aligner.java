package org.DNAtion.control.alignment;

import org.DNAtion.model.FASTQ.FASTQ;

import java.io.File;

public interface Aligner {
    static final int SUCCESS = 0;
    static final int FAILURE = -1;

    int alignSq();

    String getAlignerID();

    File getGenome();

    FASTQ getSampleSq_1();

    FASTQ getSampleSq_2();

    File getStdout();

    File getStderr();

    int getCores();

    ProcessBuilder getAlignCmd();

    void setCores(int cores);

    void setStdout(File stdout);

    void setStderr(File stderr);

    void setAlignCmd(ProcessBuilder alignCmd);

    int setSampleSq_1(FASTQ sample_1);

    int setSampleSq_2(FASTQ sample_2);

    int setSampleSq(FASTQ sample_1, FASTQ sample_2);

    int setReferenceGenome(File genome);
}