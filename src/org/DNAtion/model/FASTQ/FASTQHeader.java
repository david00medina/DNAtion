package org.DNAtion.model.FASTQ;

import java.util.HashMap;
import java.util.Map;

class FASTQHeader {
    private String header;
    private Map<String, String> fields;
    private int version;

    FASTQHeader(String income) {
        fields = new HashMap<>();
        header = income;
        analizeHeader(income);
    }

    public FASTQHeader(Map<String, String> fields) {
        this.fields = fields;
    }

    private void analizeHeader(String income) {
        if (income.split(" ").length == 1) {
            getFields_v1(income);

        } else if (income.split(" ").length == 2) {
            getFields_v2(income);
        }
    }

    private void getFields_v1(String income) {
        version = 1;
        FASTQEnum_v1[] fieldNames = FASTQEnum_v1.values();
        int i = 0;
        for (String field :
                income.substring(1).split(":")) {

            if (i == 4) {
                fields.put(fieldNames[i].getValue(), field.split("#")[0]);
                fields.put(
                        fieldNames[i + 1].getValue(),
                        field.split("#")[1].split("/")[0]);
                fields.put(
                        fieldNames[i + 2].getValue(),
                        field.split("#")[1].split("/")[1]);
            } else {
                fields.put(fieldNames[i].getValue(), field);
            }

            i++;
        }
    }

    private void getFields_v2(String income) {
        version = 2;
        FASTQEnum_v2[] fieldNames = FASTQEnum_v2.values();
        int i = 0;
        for (String field :
                income.split(" ")[0].substring(1).split(":")) {

            fields.put(fieldNames[i].getValue(), field);
            i++;
        }

        for (String field :
                income.split(" ")[1].split(":")) {

            fields.put(fieldNames[i].getValue(), field);
            i++;
        }
    }

    public String getHeader() {
        return header;
    }

    Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public Map<String, String> clearFields() {
        return fields = new HashMap<>();
    }

    int getVersion() {
        return version;
    }
}