/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.touringmusician;


import android.graphics.Point;
import android.util.Log;

import java.util.Iterator;

public class CircularLinkedList implements Iterable<Point> {

    private class Node {
        Point point;
        Node prev, next;

        public Node(Point p) {
            point = p;
        }

    }

    Node head;

    public void insertBeginning(Point p) {
        Node node = new Node(p);
        if (head != null) {
            node.next = head;
            node.prev = head.prev;
            node.prev.next = node;
            head.prev = node;
            head = node;
        } else {
            head = node;
            head.next = head.prev = head;
        }
    }

    private float distanceBetween(Point from, Point to) {
        return (float) Math.sqrt(Math.pow(from.y-to.y, 2) + Math.pow(from.x-to.x, 2));
    }

    public float totalDistance() {
        float total = 0;
        Node current = head;
        while (current.next != head) {
            total += distanceBetween(current.point,current.next.point);
            current = current.next;
        }
        total += distanceBetween(current.point,current.next.point);
        return total;
    }
    // Look into kd tree log time
    public void insertNearest(Point p) {
        float dist = 0;
        float curDist;
        Node node = new Node(p);
        if (head == null) {
            head = node;
            head.next = head.prev = head;
        } else {
            Node current = head;
            Node select = current;
            while (current.next != head) {
                curDist = distanceBetween(current.point,p);
                if (dist < curDist) {
                    dist = curDist;
                    select = current;
                }
                current = current.next;
            }
            node.next = select.next;
            node.prev = select;
            select.next.prev = node;
            select.next = node;
        }
    }

    public void insertSmallest(Point p) {
        Node node = new Node(p);
        float diff = 10000000;
        float oldDist,newDist;
        float curDiff;
        if (head == null) {
            head = node;
            head.next = head.prev = head;
        } else {
            Node current = head;
            Node select = current;
            while (current.next != head) {
                oldDist = distanceBetween(current.point,current.next.point);
                newDist = distanceBetween(current.point,p) + distanceBetween(current.next.point,p);
                curDiff = newDist - oldDist;
                if (diff > curDiff ){
                    diff = curDiff;
                    select = current;
                }
                current = current.next;
            }
            oldDist = distanceBetween(current.point,current.next.point);
            newDist = distanceBetween(current.point,p) + distanceBetween(current.next.point,p);
            curDiff = newDist - oldDist;
            if (diff > curDiff ){
                select = current;
            }
            node.next = select.next;
            node.prev = select;
            select.next.prev = node;
            select.next = node;
        }
    }

    public void reset() {
        head = null;
    }

    private class CircularLinkedListIterator implements Iterator<Point> {

        Node current;

        public CircularLinkedListIterator() {
            current = head;
        }

        @Override
        public boolean hasNext() {
            return (current != null);
        }

        @Override
        public Point next() {
            Point toReturn = current.point;
            current = current.next;
            if (current == head) {
                current = null;
            }
            return toReturn;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Iterator<Point> iterator() {
        return new CircularLinkedListIterator();
    }


}
