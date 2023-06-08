package pl.edu.pw.ee;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class Huffman {

    private String inputFileName;
    private String outputFileName;
    private String decodedFileName;
    private String treeFileName;

    private FileReader inputFileReader;
    private DataOutputStream outputFile;
    private PrintStream decodedFile;
    private PrintStream treeFile;
    private FileReader treeReader;
    private DataInputStream encodedFile;
    private Map<Character, Integer> charFrequencies;
    private Map<Character, String> huffmanCodes;
    private Map<String, Character> huffmanChars;
    private String encodedText;
    private int nChars = 0;
    private int nBits = 0;

    public int huffman(String pathToRootDir, boolean compress) throws IOException {

        if (pathToRootDir == null) {
            throw new IllegalArgumentException("path to the directory cannot be null");
        }

        inputFileName = pathToRootDir + "in.txt";
        outputFileName = pathToRootDir + "out";
        treeFileName = pathToRootDir + "tree";
        decodedFileName = pathToRootDir + "decompressed";


        if (compress) {
            encode();
            return nBits;
        } else {
            decode();
            return nChars;
        }
    }

    private void encode() throws IOException {
        fillCharFrequenciesMap();
        huffmanCodes = new HashMap<>();
        generateHuffmanCodes(buildTree(), "");
        generateEncodedText();
        writeEncodedTextToFile();
        writeTreeToFile();
    }

    private Node buildTree() {
        Queue<Node> queue = new PriorityQueue<>();
        charFrequencies.forEach((character, frequency) -> queue.add(new Leaf(character, frequency)));
        while (queue.size() > 1) {
            queue.add(new Node(queue.poll(), queue.poll()));
        }
        return queue.poll();
    }

    private void writeTreeToFile() throws FileNotFoundException {
        this.treeFile = new PrintStream(new FileOutputStream(treeFileName));
        for (var entry : huffmanCodes.entrySet()) {
            treeFile.print(entry.getKey() + entry.getValue() + "/");
        }
    }

    private void writeEncodedTextToFile() throws IOException {
        this.outputFile = new DataOutputStream(new FileOutputStream(outputFileName));
        int idx = 0;
        int sizeOfBuf = 8;

        String nBitsBinary = String.format("%32s", Integer.toBinaryString(nBits)).replace(' ', '0');

        encodedText = nBitsBinary + encodedText;
        nBits += 32;

        while (idx + sizeOfBuf <= nBits) {
            String buf = encodedText.substring(idx, idx + sizeOfBuf);
            idx += sizeOfBuf;
            int val = Integer.parseInt(buf, 2);
            outputFile.writeByte(val);
        }
        if (idx == nBits) {
            return;
        }
        StringBuilder buf = new StringBuilder(encodedText.substring(idx));
        while (buf.length() < sizeOfBuf) {
            buf.append("0");
            nBits++;
        }
        int val = Integer.parseInt(buf.toString(), 2);
        outputFile.writeByte(val);

    }

    private void generateEncodedText() throws IOException {
        inputFileReader = new FileReader(inputFileName);
        StringBuilder sb = new StringBuilder();
        int currentInt = inputFileReader.read();
        while (currentInt != -1) {
            Character currentCharacter = (char) currentInt;
            sb.append(huffmanCodes.get(currentCharacter));
            nBits += huffmanCodes.get(currentCharacter).length();
            currentInt = inputFileReader.read();
        }
        this.encodedText = sb.toString();
    }

    private void generateHuffmanCodes(Node node, String code) {
        if (node instanceof Leaf) {
			if(code == "") {
				code = "0";
			}
            huffmanCodes.put(((Leaf) node).getCharacter(), code);
            return;
        }
        generateHuffmanCodes(node.getLeftNode(), code.concat("0"));
        generateHuffmanCodes(node.getRightNode(), code.concat("1"));
    }

    private void fillCharFrequenciesMap() throws IOException {
        this.inputFileReader = new FileReader(inputFileName);
        charFrequencies = new HashMap<>();
        int currentInt = inputFileReader.read();
		if (currentInt == -1) {
			throw new IOException("File cannot be empty");
		}
        while (currentInt != -1) {
            Character currentCharacter = (char) currentInt;
            Integer currentFrequency = charFrequencies.get(currentCharacter);
            charFrequencies.put(currentCharacter, currentFrequency != null ? currentFrequency + 1 : 1);

            currentInt = inputFileReader.read();
        }
    }

    private void decode() throws IOException {
        readTreeFromFile();
        readEncodedText();
        setNBits();
        writeDecodedTextToFile();
    }

    private void writeDecodedTextToFile() throws FileNotFoundException {
        
        decodedFile = new PrintStream(decodedFileName);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < nBits; i++) {
            sb.append(encodedText.charAt(i));
            Character val = huffmanChars.get(sb.toString());
            if (val == null) {
                continue;
            }
            decodedFile.print(val);
            nChars++;
            sb.setLength(0);
        }

    }

    private void setNBits() {
        String nBitsBinary = encodedText.substring(0, 32);
        nBits = Integer.parseInt(nBitsBinary, 2);
        encodedText = encodedText.substring(32);
    }

    private void readEncodedText() throws IOException {
        encodedFile = new DataInputStream(new FileInputStream(outputFileName));
        StringBuilder sb = new StringBuilder();

        while (encodedFile.available() > 0) {
			byte currentByte = encodedFile.readByte();
            String currentBinary = String.format("%8s", Integer.toBinaryString(currentByte & 0xFF)).replace(' ', '0');
            sb.append(currentBinary);
        }
        encodedText = sb.toString();

    }

    private void readTreeFromFile() throws IOException {
        this.treeReader = new FileReader(treeFileName);
        this.huffmanChars = new HashMap<>();
        huffmanCodes = new HashMap<>();
        int currentInt = treeReader.read();
        while (currentInt != -1) {
            char currentChar = (char) currentInt;
            currentInt = treeReader.read();
            StringBuilder sb = new StringBuilder();
            while ((char) currentInt != '/') {
                sb.append((char) currentInt);
                currentInt = treeReader.read();
            }
            huffmanChars.put(sb.toString(), currentChar);
            currentInt = treeReader.read();
        }

    }

}
