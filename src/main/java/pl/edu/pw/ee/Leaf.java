package pl.edu.pw.ee;

public class Leaf extends Node {

	private final char character;

	public Leaf(char character, int frequency) {
		super(frequency);
		this.character = character;
	}

	public char getCharacter() {
		return character;
	}
}
