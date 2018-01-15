package org.DNAtion.parsers;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

class BamParserTest {

	@Test
	public void test() {
		final File file = new File("/media/uichuimi/DiscoInterno/GENOME_DATA/CONTROLS/DAM/C7BDUACXX_8_3ss.bam");
		final Stream<Alignment> stream;
		try {
			stream = new BamParser().parse(file);
			stream.parallel().forEach(alignment -> {

			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}