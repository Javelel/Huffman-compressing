package pl.edu.pw.ee;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class HuffmanTest {

	@Test
	public void testCompressFile() throws IOException {
		Huffman huf = new Huffman();
		huf.huffman("src//main//resources//", true);
		assert true;
	}

	@Test
	public void testDecompressFile() throws IOException {
		Huffman huf = new Huffman();
		huf.huffman("src//main//resources//", false);
		assert true;
	}

	@Test(expected = FileNotFoundException.class)
	public void nameOfTheFileShould_BeIn() throws IOException {
		Huffman huf = new Huffman();
		huf.huffman("src//main//resources//noInFile//", true);
		assert true;
	}

	@Test(expected = IllegalArgumentException.class)
	public void nameOfThePathToFileShouldNot_BeNull() throws IOException {
		Huffman huf = new Huffman();
		huf.huffman(null, true);
		assert true;
	}

	@Test(expected = IOException.class)
	public void FileShouldNot_BeEmpty() throws IOException {
		Huffman huf = new Huffman();
		huf.huffman("src//main//resources//emptyInFile//", true);
		assert true;
	}

	@Test
	public void testTextShouldNot_ChangeAfterDecompression() throws IOException {
		//given
		Huffman huf = new Huffman();
		PrintStream in = new PrintStream(new FileOutputStream("src//main//resources//testDir//in.txt"));
		String expected = "Mniej mam i mniemam że nie mam ja mienia.\n"
				+ "Mnie nie omamia mania mania mniemania.\n"
				+ "Ja mam imię, a nie mienienie się mianem.\n"
				+ "Ja manie mam na „nie”, a me imię – Niemanie.";
		in.print(expected);
		//when
		huf.huffman("src//main//resources//testDir//", true);
		huf.huffman("src//main//resources//testDir//", false);
		Path path = Path.of("src//main//resources//testDir//decompressed");
		String result = Files.readString(path);
		//then
		Assert.assertEquals(expected, result);
	}

	@Test
	public void testNumberOfCharsShouldNot_ChangeAfterDecompression() throws IOException {
		//given
		Huffman huf = new Huffman();
		PrintStream in = new PrintStream(new FileOutputStream("src//main//resources//testDir//in.txt"));
		String inputString = "Mniej mam i mniemam że nie mam ja mienia.\n"
				+ "Mnie nie omamia mania mania mniemania.\n"
				+ "Ja mam imię, a nie mienienie się mianem.\n"
				+ "Ja manie mam na „nie”, a me imię – Niemanie.";
		in.print(inputString);
		huf.huffman("src//main//resources//testDir//", true);
		//when
		int result = huf.huffman("src//main//resources//testDir//", false);
		int expected = inputString.length();
		//then
		Assert.assertEquals(expected, result);
	}

}
