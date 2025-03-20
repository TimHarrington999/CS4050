package org.example.Trees;
//Author: Jack Villanueva
import javafx.scene.paint.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class AVLTree<T extends Comparable<T>> implements Tree<T>, Serializable {
    Node root;
    int size;

    private class Node implements TreeNode<T> {
        T value;
        Node left;
        Node right;
        int height;

        public Node(T value) {
            this.value = value;
            this.left = null;
            this.right = null;
            this.height = 1;
        }

        @Override
        public T getValue() { return value; }
        @Override
        public TreeNode<T> getLeft() { return left; }
        @Override
        public TreeNode<T> getRight() { return right; }
        @Override
        public String getColor() { return null; }

        public void updateHeight() {
            height = Math.max(
                    left == null ? 0 : left.height,
                    right == null ? 0 : right.height
            ) + 1;
        }
        public int[] checkedHeights() {
            return new int[] {
                    left == null ? 0 : left.height,
                    right == null ? 0 : right.height,
            };
        }
        public int balanceFactor() {
            int[] heights = checkedHeights();
            return heights[0] - heights[1];
        }

        private void postRotationUpdate() {
            if (left != null) { left.updateHeight(); }
            if (right != null) { right.updateHeight(); }
            updateHeight();
        }

        private void rotateLeft() {
            Node temp = new Node(value);
            temp.left = left;
            temp.right = right.left;
            value = right.value;
            right = right.right;
            left = temp;
            postRotationUpdate();
        }
        private void rotateRight() {
            Node temp = new Node(value);
            temp.right = right;
            temp.left = left.right;
            value = left.value;
            left = left.left;
            right = temp;
            postRotationUpdate();
        }

        private int insertionCause(T cause) {
            //ASSERT: cause != value != left.value != right.value
            if (cause.compareTo(value) < 0) {
                //left->left
                if (cause.compareTo(left.value) < 0) { return 0; }
                //left->right
                else { return 1; }
            }
            else {
                //right->left
                if (cause.compareTo(right.value) < 0) { return 2; }
                //right->right
                else { return 3;}
            }
        }
        private int deletionCause() {
            int[] h1 = checkedHeights();
            if (h1[0] > h1[1]) {
                int[] h2 = left.checkedHeights();
                //left->left
                if (h2[0] > h2[1]) { return 0; }
                //left->right
                else { return 1; }
            }
            else {
                int[] h2 = right.checkedHeights();
                //right->left
                if (h2[0] > h2[1]) { return 2; }
                //right->right
                else { return 3;}
            }
        }

        private Optional<T> inorderSuccessor() {
            if (right == null) { return Optional.empty(); }
            Node cursor = right;
            while (cursor.left != null) { cursor = cursor.left; }
            return Optional.of(cursor.value);
        }
    }

    public AVLTree() {
        root = null;
        size = 0;
    }

    @Override
    public void insert(T value) {
        if (!contains(value)) {
            root = insert(root, value);
            size++;
        }
    }
    private Node insert(Node node, T value) {
        if (node == null) { return new Node(value); }
        if (value.compareTo(node.value) < 0) { node.left = insert(node.left, value); }
        else if (value.compareTo(node.value) > 0) { node.right = insert(node.right, value); }
        else if (value.compareTo(node.value) == 0) { return node; }

        node.updateHeight();

        if (Math.abs(node.balanceFactor()) > 1) {
            switch (node.insertionCause(value)) {
                case 0: //left->left
                    node.rotateRight();
                    break;
                case 1: //left->right
                    node.left.rotateLeft();
                    node.rotateRight();
                    break;
                case 2: //right->left
                    node.right.rotateRight();
                    node.rotateLeft();
                    break;
                case 3: //right->right
                    node.rotateLeft();
                    break;
            }
        }
        return node;
    }

    @Override
    public boolean delete(T value) {
        if (contains(value)) {
            root = delete(root, value);
            size--;
        }
        return !contains(value);
    }
    private Node delete(Node node, T value) {
        if (node == null) { return null; }
        if (value.compareTo(node.value) < 0) { node.left = delete(node.left, value); }
        else if (value.compareTo(node.value) > 0) { node.right =  delete(node.right, value); }
        else if (value.compareTo(node.value) == 0) {
            Optional<T> successor = node.inorderSuccessor();
            if (successor.isPresent()) {
                T successorValue = successor.get();
                node.value = successorValue;
                node.right = delete(node.right, successorValue);
            }
            else { return null; }
        }

        node.updateHeight();

        if (Math.abs(node.balanceFactor()) > 1) {
            switch (node.deletionCause()) {
                case 0: //left->left
                    node.rotateRight();
                    break;
                case 1:
                    node.left.rotateLeft();
                    node.rotateRight();
                    break;
                case 2:
                    node.right.rotateLeft();
                    node.rotateLeft();
                    break;
                case 3:
                    node.rotateLeft();
                    break;
            }
        }
        return node;
    }

    @Override
    public boolean contains(T value) {
        return contains(root, value);
    }
    private boolean contains(Node node, T value) {
        if (node == null) { return false; }
        return switch (Math.clamp(value.compareTo(node.value), -1, 1)) {
            case -1 -> contains(node.left, value);
            case 0 -> true;
            case 1 -> contains(node.right, value);
            default -> throw new IllegalStateException();
        };
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public List<T> inorderTraversal() {
        List<T> result = new ArrayList<>();
        inorderTraversal(root, result);
        return result;
    }

    private void inorderTraversal(Node node, List<T> result) {
        if (node != null) {
            inorderTraversal(node.left, result);
            result.add(node.value);
            inorderTraversal(node.right, result);
        }
    }

    @Override
    public String type() {
        return "AVL";
    }

    @Override
    public Color color() {
        return Color.BLACK;
    }

    @Override
    public TreeNode<T> getRoot() {
        return root;
    }
}