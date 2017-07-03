package eu.verdelhan.ta4j.util;

import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ExtendableIterator<T> implements Iterator<T> {

    public Deque<Iterator<T>> its = new ConcurrentLinkedDeque<Iterator<T>>();

    public ExtendableIterator() {

    }

    public ExtendableIterator(Iterator<T> it) {
        this();
        this.extend(it);
    }

    @Override
    public boolean hasNext() {
        // this is true since we never hold empty iterators
        return !its.isEmpty() && its.peekLast().hasNext();
    }

    @Override
    public T next() {
        T next = its.peekFirst().next();
        if (!its.peekFirst().hasNext()) {
            its.removeFirst();
        }
        return next;
    }

    public void extend(Iterator<T> it) {
        if (it.hasNext()) {
            its.addLast(it);
        }
    }
}