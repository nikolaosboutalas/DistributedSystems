package common.util;

import java.util.LinkedList;

public class RoundRobinList<T> {
    private LinkedList<T> items;
    private int currentIndex;

    public RoundRobinList() {
        items = new LinkedList<T>();
        currentIndex = 0;
    }

    public synchronized void add(T item) {
        items.add(item);
    }

    public synchronized T getNext() {
        if (items.isEmpty()) {
            return null;
        }

        T nextItem = items.get(currentIndex);
        currentIndex = (currentIndex + 1) % items.size();

        return nextItem;
    }
}
