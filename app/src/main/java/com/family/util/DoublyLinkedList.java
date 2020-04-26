package com.family.util;

import com.family.familyprotector.Logger;
import com.family.familyprotector.Message;

import java.util.Date;
import java.util.NoSuchElementException;

public class DoublyLinkedList<E> {

    public Node head;
    public Node tail;
    public int size;
    public int MAX_SIZE = 1000;

    public DoublyLinkedList() {
        size = 0;
    }

    /**
     * this class keeps track of each element information
     *
     * @author java2novice
     */
    public class Node {
        public E element;
        public Node next;
        public Node prev;

        public Node(E element, Node next, Node prev) {
            this.element = element;
            this.next = next;
            this.prev = prev;
        }
    }

    /**
     * returns the size of the linked list
     *
     * @return
     */
    public int size() {
        return size;
    }

    /**
     * return whether the list is empty or not
     *
     * @return
     */
    public boolean isEmpty() {
        return size == 0;
    }
    public void clear() {
        size = 0;
        head = null;
        tail = null;
    }

    /**
     * adds element at the starting of the linked list
     *
     * @param element
     */
    public void addFirst(E element) {
        if(size >= MAX_SIZE) {
            return;
        }
        Message m = (Message)element;
        if(m.date != null) {
            long s = m.date.getTime();
            long now = new Date().getTime();
            if(now - s > (1000 * 60 * 60 * 24 * 7)) {
                return;
            }
        }
        Node tmp = new Node(element, head, null);

        if (head != null) {
            head.prev = tmp;
        }
        head = tmp;
        if (tail == null) {
            tail = tmp;
        }
        size++;
        Logger.l("adding first: " + element.toString());
    }

    /**
     * adds element at the end of the linked list
     *
     * @param element
     */
    public void addLast(E element) {

        Node tmp = new Node(element, null, tail);
        //elemesem daha yaxshidi
        Message m = (Message)element;
        if(m.date != null) {
            long s = m.date.getTime();
            long now = new Date().getTime();
            if(now - s > (1000 * 60 * 60 * 24 * 7)) {
                return;
            }
        }
        if (tail != null) {
            tail.next = tmp;
        }
        tail = tmp;
        if (head == null) {
            head = tmp;
        }
        size++;
        if(size > MAX_SIZE) {
            size--;
            head = head.next;
        }
        Logger.l("adding last: " + element.toString());
    }

    /**
     * this method walks forward through the linked list
     */
    public void iterateForward() {

        Logger.l("iterating forward..");
        Node tmp = head;
        while (tmp != null) {
            Logger.l(tmp.element.toString());
            tmp = tmp.next;
        }
    }

    /**
     * this method walks backward through the linked list
     */
    public void iterateBackward() {

        Logger.l("iterating backword..");
        Node tmp = tail;
        while (tmp != null) {
            Logger.l(tmp.element.toString());
            tmp = tmp.prev;
        }
    }

    /**
     * this method removes element from the start of the linked list
     *
     * @return
     */
    public E removeFirst() {
        if (size == 0) throw new NoSuchElementException();
        Node tmp = head;
        head = head.next;
        head.prev = null;
        size--;
        Logger.l("deleted: " + tmp.element);
        return tmp.element;
    }

    /**
     * this method removes element from the end of the linked list
     *
     * @return
     */
    public E removeLast() {
        if (size == 0) throw new NoSuchElementException();
        Node tmp = tail;
        tail = tail.prev;
        tail.next = null;
        size--;
        Logger.l("deleted: " + tmp.element);
        return tmp.element;
    }

}