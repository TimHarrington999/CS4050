package org.example.Trees;

// Author: Timothy Harrington

import javafx.scene.paint.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MaxHeap<T extends Comparable<T>> extends Heap<T> implements Serializable {

    @Override
    protected void heapifyUp(int index) {
        // first we need the parent value
        int parentIndex = getParentIndex(index);
        T parentValue = heap.get(getParentIndex(index));
        T currentValue = heap.get(index);

        // then compare the two values
        if(parentValue != currentValue) {

            // if the parent is less than the new node
            if(parentValue.compareTo(currentValue) < 0) {

                // swap the two values
                swap(index, parentIndex);

                // then upheap
                heapifyUp(parentIndex);
            }
        }
    }

    @Override
    protected void heapifyDown(int index) {
        // get the values of the left and right children
        int leftChildIndex = getLeftChildIndex(index);
        int rightChildIndex = getRightChildIndex(index);
        T leftValue = null;
        T rightValue = null;

        // check to see if there is a left child
        if (leftChildIndex <= heap.size() - 1) {
            leftValue = heap.get(leftChildIndex);

            // now check if there is a right child
            if (rightChildIndex <= heap.size() - 1) {
                rightValue = heap.get(rightChildIndex);

                // since we have both children, swap with the child that is greater
                if (leftValue.compareTo(rightValue) > 0) {
                    swap(index, leftChildIndex);
                    heapifyDown(leftChildIndex);
                } else {
                    swap(index, rightChildIndex);
                    heapifyDown(rightChildIndex);
                }
            }

            // if we have left child but no right child, swap with left
            else {
                swap(index, leftChildIndex);
                heapifyDown(leftChildIndex);
            }
        }

        // if there is no left child, no need to check if there is a right since that cannot happen

    }

    @Override
    public String type() {
        return "MaxHeap";
    }

    @Override
    public Color color() {
        return Color.BLACK;
    }

}
