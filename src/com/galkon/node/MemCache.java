package com.galkon.node;

public final class MemCache {

	public MemCache(int i) {
		emptyNodeSub = new NodeSub();
		queue = new Queue();
		initialCount = i;
		spaceLeft = i;
		hashTable = new HashTable();
	}

	public NodeSub get(long id) {
		NodeSub nodeSub = (NodeSub) hashTable.getNodeForId(id);
		if (nodeSub != null) {
			queue.insertHead(nodeSub);
		}
		return nodeSub;
	}

	public void put(NodeSub nodeSub, long l) {
		try {
			if (spaceLeft == 0) {
				NodeSub nodeSub_1 = queue.popTail();
				nodeSub_1.remove();
				nodeSub_1.clear();
				if (nodeSub_1 == emptyNodeSub) {
					NodeSub nodeSub_2 = queue.popTail();
					nodeSub_2.remove();
					nodeSub_2.clear();
				}
			} else {
				spaceLeft--;
			}
			hashTable.remove(nodeSub, l);
			queue.insertHead(nodeSub);
			return;
		} catch (RuntimeException e) {
			System.out.println("47547, " + nodeSub + ", " + l + ", " + (byte) 2 + ", " + e.toString());
		}
		throw new RuntimeException();
	}

	public void clear() {
		do {
			NodeSub nodeSub = queue.popTail();
			if (nodeSub != null) {
				nodeSub.remove();
				nodeSub.clear();
			} else {
				spaceLeft = initialCount;
				return;
			}
		} while (true);
	}

	private final NodeSub emptyNodeSub;
	private final int initialCount;
	private int spaceLeft;
	private final HashTable hashTable;
	private final Queue queue;
}
