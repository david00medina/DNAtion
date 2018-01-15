package org.DNAtion.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Stream;

public class BamParser {

	public Stream<Alignment> parse(File file) throws IOException {
		final ProcessBuilder pb = new ProcessBuilder("samtools", "view", file.getAbsolutePath());
		final Process process = pb.start();
		final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		return reader.lines().map(AligmentFactory::createAligment);
	}
}
