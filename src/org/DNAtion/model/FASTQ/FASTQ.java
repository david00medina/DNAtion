package org.DNAtion.model.FASTQ;

import org.DNAtion.model.SAM.SAMEnum_RG;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class FASTQ {
    private static final String ILLUMINA = "ILLUMINA";
    private FASTQHeader fastqHeader;
    private File file;
    private Map<String, String> rgFields;
    private String bwaHeader;
    private String bowtieHeader;

    public FASTQ(File file) {
        rgFields = new HashMap<>();
        this.file = file;
        initializeParameters();
    }

    private void initializeParameters() {
        extractHeader();
        rgBuilder();
    }

    private void extractHeader() {
        try {
            if (file.getName().toLowerCase().contains(".fastq.gz")
                    || file.getName().toLowerCase().contains("fq.gz")) {

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                new GZIPInputStream(
                                        new FileInputStream(file))));

                fastqHeader = new FASTQHeader(br.readLine());


            } else {

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(file)));

                fastqHeader = new FASTQHeader(br.readLine());


            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("The FASTQ file crashed attempting to open it");
        }
    }

    private void rgBuilder() {
        switch (fastqHeader.getVersion()) {
            case 1:
                loadRGFields_v1();
                break;
            case 2:
                loadRGFields_v2();
                break;
            default:
                //TODO: Implement this part with Pekin lab's FASTQ Headers
                break;
        }

        bwaHeader = "@RG\\t";
        int i = 1;
        for (String header :
                rgFields.keySet()) {

            bwaHeader = bwaHeader
                    + SAMEnum_RG.getEnumByString(header) + ":"
                    + rgFields.get(header);

            if (i < rgFields.size())
                bwaHeader = bwaHeader + "\\t";

            i++;
        }

        bowtieHeader = "@RG\t";
        i = 1;
        for (String header :
                rgFields.keySet()) {

            bowtieHeader = bowtieHeader
                    + SAMEnum_RG.getEnumByString(header) + ":"
                    + rgFields.get(header);

            if (i < rgFields.size())
                bowtieHeader = bowtieHeader + "\t";

            i++;
        }
    }

    private void loadRGFields_v1() {
        Map<String, String> fastqFields = getFastqHeader().getFields();
        FASTQEnum_v1[] keyFastq = FASTQEnum_v1.values();
        SAMEnum_RG[] keySam = SAMEnum_RG.values();

        rgFields.put(keySam[0].getValue(),
                fastqFields.get(keyFastq[1].getValue()) + "."
                        + fastqFields.get(keyFastq[2].getValue()));

        rgFields.put(keySam[1].getValue(),
                fastqFields.get(keyFastq[1].getValue()) + "."
                        + fastqFields.get(keyFastq[2].getValue()) + "."
                        + fastqFields.get(keyFastq[0].getValue()));

        rgFields.put(keySam[2].getValue(),
                file.getName().substring(0, file.getName().indexOf('_')));

        rgFields.put(keySam[3].getValue(),
                ILLUMINA);

        rgFields.put(keySam[4].getValue(),
                fastqFields.get(keyFastq[5].getValue()));
    }

    private void loadRGFields_v2() {
        Map<String, String> fastqFields = getFastqHeader().getFields();
        FASTQEnum_v2[] keyFastq = FASTQEnum_v2.values();
        SAMEnum_RG[] keySam = SAMEnum_RG.values();

        rgFields.put(keySam[0].getValue(),
                fastqFields.get(keyFastq[2].getValue()) + "."
                        + fastqFields.get(keyFastq[3].getValue()));

        rgFields.put(keySam[1].getValue(),
                fastqFields.get(keyFastq[2].getValue()) + "."
                        + fastqFields.get(keyFastq[3].getValue()) + "."
                        + fastqFields.get(keyFastq[1].getValue()));

        rgFields.put(keySam[2].getValue(),
                file.getName().substring(0, file.getName().indexOf('_')));

        rgFields.put(keySam[3].getValue(),
                ILLUMINA);

        rgFields.put(keySam[4].getValue(),
                fastqFields.get(keyFastq[10].getValue()));
    }

    public FASTQHeader getFastqHeader() {
        return fastqHeader;
    }

    public void setFastqHeader(FASTQHeader fastqHeader) {
        this.fastqHeader = fastqHeader;
    }

    public String getBowtieHeader() {
        return bowtieHeader;
    }

    public void setBowtieHeader(String bowtieHeader) {
        this.bowtieHeader = bowtieHeader;
    }

    public String getBwaHeader() {
        return bwaHeader;
    }

    public void setBwaHeader(String bwaHeader) {
        this.bwaHeader = bwaHeader;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Map<String, String> getRgFields() {
        return rgFields;
    }

    public void setRgFields(Map<String, String> rgFields) {
        this.rgFields = rgFields;
    }
}