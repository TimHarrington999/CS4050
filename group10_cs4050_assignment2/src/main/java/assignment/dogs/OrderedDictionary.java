package assignment.dogs;

public class OrderedDictionary implements OrderedDictionaryADT {

    Node root;

    OrderedDictionary() {
        root = new Node();
    }

    /**
     * Returns the Record object with key k, or it returns null if such a record
     * is not in the dictionary.
     *
     * @param k
     * @return
     * @throws assignment/dogs/DictionaryException.java
     */
    @Override
    public DogRecord find(DataKey k) throws DictionaryException {
        Node current = root;
        int comparison;
        if (root.isEmpty()) {
            throw new DictionaryException("There is no record matches the given key");
        }

        while (true) {
            comparison = current.getData().getDataKey().compareTo(k);
            if (comparison == 0) { // key found
                return current.getData();
            }
            if (comparison == 1) {
                if (current.getLeftChild() == null) {
                    // Key not found
                    throw new DictionaryException("There is no record matches the given key");
                }
                current = current.getLeftChild();
            } else if (comparison == -1) {
                if (current.getRightChild() == null) {
                    // Key not found
                    throw new DictionaryException("There is no record matches the given key");
                }
                current = current.getRightChild();
            }
        }

    }

    /**
     * Inserts r into the ordered dictionary. It throws a DictionaryException if
     * a record with the same key as r is already in the dictionary.
     *
     * @param r
     * @throws assignment.dogs.DictionaryException
     */
    @Override
    public void insert(DogRecord r) throws assignment.dogs.DictionaryException {
        System.out.println("Inserting a new record");
        //check if the root node exists
        if (root==null) {
            root = new Node();
        }

        // check if root node is empty
        if (root.isEmpty()) {
            // populate the root node and return
            root.setData(r);
            return;
        }

        // create a new node if the root is populated, and insert it into the tree
        Node newNode = new Node(r);
        Node curNode = root;
        Node parent = null;

        // loop through the tree until we find where the new node belongs
        while (curNode != null) {
            parent = curNode;
            if (newNode.getData().getDataKey().compareTo(curNode.getData().getDataKey()) < 0) {
                curNode = curNode.getLeftChild();
            } else {
                curNode = curNode.getRightChild();
            }
        }

        // then insert the new node where it belongs
        newNode.setParent(parent);
        if (newNode.getData().getDataKey().compareTo(parent.getData().getDataKey()) < 0) {
            parent.setLeftChild(newNode);
        } else {
            parent.setRightChild(newNode);
        }
    }

    /**
     * Removes the record with Key k from the dictionary. It throws a
     * DictionaryException if the record is not in the dictionary.
     *
     * @param k
     * @throws assignment.dogs.DictionaryException
     */
    @Override
    public void remove(DataKey k) throws assignment.dogs.DictionaryException {
        Node nodeToRemove = findNode(k);
        if (nodeToRemove == null || nodeToRemove.isEmpty()) {
            throw new DictionaryException("No such record key exists");
        }

        // if node has no children, we reached leaf
        if (nodeToRemove.isLeaf()) {
            replaceNodeInParent(nodeToRemove, null);
        }
        // if node has one child
        else if (!nodeToRemove.hasLeftChild()) {
            replaceNodeInParent(nodeToRemove, nodeToRemove.getRightChild());
        } else if (!nodeToRemove.hasRightChild()) {
            replaceNodeInParent(nodeToRemove, nodeToRemove.getLeftChild());
        }
        // if node has two children
        else {
            Node successor = findMinNode(nodeToRemove.getRightChild());
            nodeToRemove.setData(successor.getData());
            // Remove the successor, the condition is node has at most one right child
            if (successor.getRightChild() != null) {
                replaceNodeInParent(successor, successor.getRightChild());
            } else {
                replaceNodeInParent(successor, null);
            }
        }
    }

    // Helper function to find the node with key k
    private Node findNode(DataKey k) {
        Node current = root;
        while (current != null && !current.isEmpty()) {
            int cmp = current.getData().getDataKey().compareTo(k);
            if (cmp == 0) {
                return current;
            } else if (cmp == 1) {
                current = current.getLeftChild();
            } else {
                current = current.getRightChild();
            }
        }
        return null;
    }

    // Helper function to replace a node with its parent
    private void replaceNodeInParent(Node node, Node replacement) {
        Node parent = node.getParent();
        if (parent == null) {
            root = replacement; // Update root
        } else {
            if (parent.getLeftChild() == node) {
                parent.setLeftChild(replacement);
            } else {
                parent.setRightChild(replacement);
            }
        }
        if (replacement != null) {
            replacement.setParent(parent);
        }
    }

    // Helper function to find the minimum node in a subtree
    private Node findMinNode(Node node) {
        while (node.hasLeftChild()) {
            node = node.getLeftChild();
        }
        return node;
    }

    /**
     * Returns the successor of k (the record from the ordered dictionary with
     * smallest key larger than k); it returns null if the given key has no
     * successor. The given key DOES NOT need to be in the dictionary.
     *
     * @param k
     * @return
     * @throws assignment.dogs.DictionaryException
     */
    @Override
    public DogRecord successor(DataKey k) throws assignment.dogs.DictionaryException {
        Node current = root;
        DogRecord successorRecord = null;

        while (current != null && !current.isEmpty()) {
            int cmp = current.getData().getDataKey().compareTo(k);
            if (cmp == 1) { // current > k
                successorRecord = current.getData(); // Potential successor
                current = current.getLeftChild();
            } else { // current <= k
                current = current.getRightChild();
            }
        }

        // If key exists, check right subtree
        try {
            DogRecord foundRecord = find(k);
            if (foundRecord != null) {
                Node foundNode = findNode(foundRecord.getDataKey());
                if (foundNode.hasRightChild()) {
                    return findMin(foundNode.getRightChild());
                }
            }
        } catch (DictionaryException ignored) {
            // key not found -> proceed with general case
        }

        if (successorRecord == null) {
            throw new DictionaryException("There is no successor for the given record key");
        }
        return successorRecord;
    }

    // Helper function to find the minimum record
    private DogRecord findMin(Node node) {
        while (node.hasLeftChild()) {
            node = node.getLeftChild();
        }
        return node.getData();
    }


    /**
     * Returns the predecessor of k (the record from the ordered dictionary with
     * largest key smaller than k; it returns null if the given key has no
     * predecessor. The given key DOES NOT need to be in the dictionary.
     *
     * @param k
     * @return
     * @throws assignment.dogs.DictionaryException
     */
    @Override
    public DogRecord predecessor(DataKey k) throws assignment.dogs.DictionaryException {
        if (isEmpty()) {
            throw new DictionaryException("Dictionary is empty");
        }

        Node current = root;
        DogRecord predecessorRecord = null;

        while (current != null && !current.isEmpty()) {
            int cmp = current.getData().getDataKey().compareTo(k);
            if (cmp == -1) { // current < k
                predecessorRecord = current.getData();
                current = current.getRightChild();
            } else { // current >= k, then -> left
                current = current.getLeftChild();
            }
        }

        // If key exists, check left
        try {
            DogRecord foundRecord = find(k);
            if (foundRecord != null) {
                Node foundNode = findNode(foundRecord.getDataKey());
                if (foundNode.hasLeftChild()) {
                    return findMax(foundNode.getLeftChild());
                } else {
                    // move up to find ancestor
                    Node parent = foundNode.getParent();
                    while (parent != null && foundNode == parent.getLeftChild()) {
                        foundNode = parent;
                        parent = parent.getParent();
                    }
                    if (parent != null) {
                        return parent.getData();
                    }
                }
            }
        } catch (DictionaryException ignored) {
            // key not found -> proceed with general case
        }

        if (predecessorRecord == null) {
            throw new DictionaryException("There is no predecessor for the given record key");
        }
        return predecessorRecord;
    }

    // Helper method to find the maximum node in a subtree
    private DogRecord findMax(Node node) {
        while (node.hasRightChild()) {
            node = node.getRightChild();
        }
        return node.getData();
    }

    /**
     * Returns the record with smallest key in the ordered dictionary. Returns
     * null if the dictionary is empty.
     *
     * @return
     */
    @Override
    public DogRecord smallest() throws DictionaryException {

        Node current = root;

        while (current.hasLeftChild() && !current.getLeftChild().isEmpty()) {
            current = current.getLeftChild();
        }

        return current.getData();
    }

    /**
     * Returns the record with largest key in the ordered dictionary. Returns
     * null if the dictionary is empty.
     */
    @Override
    public DogRecord largest() throws DictionaryException {

        Node current = root;

        while (current.hasRightChild() && !current.getRightChild().isEmpty()) {
            current = current.getRightChild();
        }

        return current.getData();
    }

    /* Returns true if the dictionary is empty, and true otherwise. */
    @Override
    public boolean isEmpty() {
        return (root == null || root.isEmpty());
    }
}
