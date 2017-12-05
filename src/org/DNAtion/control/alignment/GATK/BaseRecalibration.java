package org.DNAtion.control.alignment.GATK;

import org.DNAtion.control.alignment.Aligner;

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
    private File recal_table;
    private File post_recal_table;
    private int genomicInterval;
    private int cores;
    private boolean doAgain;
    private boolean plot;
    private ProcessBuilder recalCmd;



    public BaseRecalibration(File genome, File in, File out, boolean doAgain, boolean plot, File... references) {
        this.genome = genome;
        this.in = in;
        this.out = out;
        this.genomicInterval = 20;
        this.references = references;
        this.doAgain = doAgain;
        this.plot = plot;
        cores = Runtime.getRuntime().availableProcessors();
    }

    private void buildCmdBaseRecal() {
        List<String> cmdConstructor = new ArrayList<>();

        recal_table = new File("./recal_data.table");

        String[] cmd = {"java", "-jar", "GenomeAnalysisTK.jar",
                "-T", "BaseRecalibrator",
                "-R", genome.getAbsolutePath(),
                "-nct", String.valueOf(cores),
                "-I", in.getAbsolutePath(),
                "-L", Integer.toString(genomicInterval),
                "-o", recal_table.getAbsolutePath()};

        cmdConstructor = Arrays.asList(cmd);

        for (int i = 0; i < references.length; i++) {
            cmdConstructor.add("-knownSites");
            cmdConstructor.add(references[i].getAbsolutePath());
        }

        recalCmd = new ProcessBuilder(cmdConstructor.toArray(new String[cmdConstructor.size()]));
    }

    private void buildCmdBaseRecalAgain() {
	    List<String> cmdConstructor = new ArrayList<>();

		post_recal_table = new File("./post_recal_data.table");

        String[] cmd = {"java", "-jar", "GenomeAnalysisTK.jar",
                "-T", "BaseRecalibrator",
                "-R", genome.getAbsolutePath(),
                "-nct", String.valueOf(cores),
                "-I", in.getAbsolutePath(),
                "-L", Integer.toString(genomicInterval),
		        "-BQSR", recal_table.getAbsolutePath(),
                "-o", post_recal_table.getAbsolutePath()};
        recalCmd = new ProcessBuilder(cmd);

	    cmdConstructor = Arrays.asList(cmd);

	    for (int i = 0; i < references.length; i++) {
		    cmdConstructor.add("-knownSites");
		    cmdConstructor.add(references[i].getAbsolutePath());
	    }

	    recalCmd = new ProcessBuilder(cmdConstructor.toArray(new String[cmdConstructor.size()]));
    }

    private void buildCmdGeneratePlots() {
    	File pdf = new File("./recalibration_plots.pdf\n");

	    String[] cmd = {"java", "-jar", "GenomeAnalysisTK.jar",
			    "-T", "AnalyzeCovariates",
			    "-R", genome.getAbsolutePath(),
			    "-nct", String.valueOf(cores),
			    "-L", Integer.toString(genomicInterval),
	            "-before", recal_table.getAbsolutePath(),
	            "-after", post_recal_table.getAbsolutePath(),
	            "-plots", pdf.getAbsolutePath()};
	    recalCmd = new ProcessBuilder(cmd);
    }

    private void buildCmdApplyChanges() {
	    String[] cmd = {"java", "-jar", "GenomeAnalysisTK.jar",
			    "-T", "PrintReads",
			    "-R", genome.getAbsolutePath(),
			    "-nct", String.valueOf(cores),
			    "-I", in.getAbsolutePath(),
			    "-L", Integer.toString(genomicInterval),
			    "-BQSR", recal_table.getAbsolutePath(),
			    "-o", out.getAbsolutePath()};
	    recalCmd = new ProcessBuilder(cmd);
    }

    public void applyBaseRecalibration() {
        buildCmdBaseRecal();

        if(runProcess() == Aligner.FAILURE)
        	System.out.println("Cannot run the recalibration process");

        if(doAgain) {
        	buildCmdBaseRecalAgain();
	        if(runProcess() == Aligner.FAILURE)
	        	System.out.println("Cannot run the recalibration process");
        }
        if(plot) {
        	buildCmdGeneratePlots();
	        if(runProcess() == Aligner.FAILURE)
	        	System.out.println("Cannot run the recalibration process");
        }

        buildCmdApplyChanges();
        runProcess();
    }

    private int runProcess() {
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

	public File getRecal_table() {
		return recal_table;
	}

	public void setRecal_table(File recal_table) {
		this.recal_table = recal_table;
	}

	public File getPost_recal_table() {
		return post_recal_table;
	}

	public void setPost_recal_table(File post_recal_table) {
		this.post_recal_table = post_recal_table;
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

	public int getCores() {
		return cores;
	}

	public void setCores(int cores) {
		this.cores = cores;
	}

	public void setDoAgain(boolean doAgain) {
		this.doAgain = doAgain;
	}

	public void setPlot(boolean plot) {
		this.plot = plot;
	}

	public File[] getReferences() {
		return references;
	}

	public void setReferences(File[] references) {
		this.references = references;
	}
}
