package com.cokepluscarbon.geoip.binary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Test;

public class BinaryTest {
	@Test
	public void t1() throws IOException {
		File file = new File("c://binary.dat");
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();

		FileOutputStream out = new FileOutputStream(file);

		for (int i = Integer.MIN_VALUE; i < Integer.MAX_VALUE; i++) {
			out.write(new Integer(i).byteValue());
		}

		out.flush();
		out.close();
	}
}
