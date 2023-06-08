package pl.edu.pw.ee;

public class Node implements Comparable<Node> {

	private int frequency;
	private Node leftNode;
	private Node rightNode;

	public Node(Node leftNode, Node rightNode) {
		this.frequency = leftNode.getFrequency() + rightNode.getFrequency();
		this.leftNode = leftNode;
		this.rightNode = rightNode;
	}

	public Node(int frequency) {
		this.frequency = frequency;
	}

	@Override
	public int compareTo(Node node) {
		return Integer.compare(frequency, node.getFrequency());
	}

	public int getFrequency() {
		return frequency;
	}

	public Node getLeftNode() {
		return leftNode;
	}

	public Node getRightNode() {
		return rightNode;
	}
}
