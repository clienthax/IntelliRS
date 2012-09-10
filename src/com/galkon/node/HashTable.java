package com.galkon.node;

final class HashTable {

	public HashTable() {
		int i = 1024;// was parameter
		size = i;
		cache = new Node[i];
		for (int k = 0; k < i; k++) {
			Node node = cache[k] = new Node();
			node.prev = node;
			node.next = node;
		}

	}

	public Node getNodeForId(long l) {
		Node node = cache[(int) (l & (long) (size - 1))];
		for (Node node_1 = node.prev; node_1 != node; node_1 = node_1.prev) {
			if (node_1.id == l) {
				return node_1;
			}
		}
		return null;
	}

	public void remove(Node node, long id) {
		try {
			if (node.next != null)
				node.remove();
			Node node_1 = cache[(int) (id & (long) (size - 1))];
			node.next = node_1.next;
			node.prev = node_1;
			node.next.prev = node;
			node.prev.next = node;
			node.id = id;
			return;
		} catch (RuntimeException e) {
			System.out.println("91499, " + node + ", " + id + ", " + (byte) 7 + ", " + e.toString());
		}
		throw new RuntimeException();
	}

	private final int size;
	private final Node[] cache;
}
