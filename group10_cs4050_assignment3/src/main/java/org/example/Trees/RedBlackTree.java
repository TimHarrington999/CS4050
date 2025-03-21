package org.example.Trees;

import javafx.scene.paint.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RedBlackTree<T extends Comparable<T>> implements Tree<T>, Serializable {
    private Node root;
    private int size;
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private class Node implements TreeNode<T>, Serializable {
        T value;
        Node left, right;
        boolean color;

        Node(T value) {
            this.value = value;
            this.color = RED;
        }

        @Override
        public T getValue() { return value; }
        @Override
        public TreeNode<T> getLeft() { return left; }
        @Override
        public TreeNode<T> getRight() { return right; }
        @Override
        public String getColor() { return color ? "RED" : "BLACK"; }
    }

    @Override
    public Color color() { return Color.RED; }

    @Override
    public String type() { return "RBT"; }

    @Override
    public void insert(T value) {
        root = insert(root, value);
        root.color = BLACK;
        size++;
    }

    private Node insert(Node node, T value) {
        if (node == null) return new Node(value);
        
        int cmp = value.compareTo(node.value);
        if (cmp < 0) node.left = insert(node.left, value);
        else if (cmp > 0) node.right = insert(node.right, value);
        
        // Fix RBT properties
        if (isRed(node.right) && !isRed(node.left)) node = rotateLeft(node);
        if (isRed(node.left) && isRed(node.left.left)) node = rotateRight(node);
        if (isRed(node.left) && isRed(node.right)) flipColors(node);
        
        return node;
    }

    @Override
    public boolean delete(T value) {
        if (!contains(value)) return false;
        if (!isRed(root.left) && !isRed(root.right)) root.color = RED;
        root = delete(root, value);
        if (root != null) root.color = BLACK;
        size--;
        return true;
    }

    private Node delete(Node node, T value) {
        if (value.compareTo(node.value) < 0) {
            if (!isRed(node.left) && !isRed(node.left.left))
                node = moveRedLeft(node);
            node.left = delete(node.left, value);
        } else {
            if (isRed(node.left)) node = rotateRight(node);
            if (value.compareTo(node.value) == 0 && (node.right == null))
                return null;
            if (!isRed(node.right) && !isRed(node.right.left))
                node = moveRedRight(node);
            if (value.compareTo(node.value) == 0) {
                Node min = findMin(node.right);
                node.value = min.value;
                node.right = deleteMin(node.right);
            } else {
                node.right = delete(node.right, value);
            }
        }
        return balance(node);
    }

    private Node deleteMin(Node node) {
        if (node.left == null) return null;
        if (!isRed(node.left) && !isRed(node.left.left))
            node = moveRedLeft(node);
        node.left = deleteMin(node.left);
        return balance(node);
    }

    @Override
    public boolean contains(T value) {
        return contains(root, value);
    }

    private boolean contains(Node node, T value) {
        while (node != null) {
            int cmp = value.compareTo(node.value);
            if (cmp < 0) node = node.left;
            else if (cmp > 0) node = node.right;
            else return true;
        }
        return false;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public int size() { return size; }

    @Override
    public List<T> inorderTraversal() {
        List<T> result = new ArrayList<>();
        inorderTraversal(root, result);
        return result;
    }

    private void inorderTraversal(Node node, List<T> result) {
        if (node == null) return;
        inorderTraversal(node.left, result);
        result.add(node.value);
        inorderTraversal(node.right, result);
    }

    @Override
    public TreeNode<T> getRoot() { return root; }

    // Helper methods
    private boolean isRed(Node node) {
        return node != null && node.color == RED;
    }

    private Node rotateLeft(Node h) {
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        x.color = h.color;
        h.color = RED;
        return x;
    }

    private Node rotateRight(Node h) {
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        x.color = h.color;
        h.color = RED;
        return x;
    }

    private void flipColors(Node h) {
        h.color = !h.color;
        h.left.color = !h.left.color;
        h.right.color = !h.right.color;
    }

    private Node moveRedLeft(Node h) {
        flipColors(h);
        if (isRed(h.right.left)) {
            h.right = rotateRight(h.right);
            h = rotateLeft(h);
            flipColors(h);
        }
        return h;
    }

    private Node moveRedRight(Node h) {
        flipColors(h);
        if (isRed(h.left.left)) {
            h = rotateRight(h);
            flipColors(h);
        }
        return h;
    }

    private Node balance(Node h) {
        if (isRed(h.right)) h = rotateLeft(h);
        if (isRed(h.left) && isRed(h.left.left)) h = rotateRight(h);
        if (isRed(h.left) && isRed(h.right)) flipColors(h);
        return h;
    }

    private Node findMin(Node node) {
        while (node.left != null) node = node.left;
        return node;
    }
}
