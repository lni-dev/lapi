/*
 * Copyright (c) 2022 Linus Andera
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

package me.linusdev.lapi.list;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LinusLinkedList<O> implements List<O> {

    final @NotNull Object lock = new Object();

    volatile int size;
    private final @NotNull LinusLinkedListEntry<O> head = new LinusLinkedListEntry<>(null);
    private @NotNull LinusLinkedListEntry<O> tail = head;

    public LinusLinkedList(){

    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return head.getNext() == null;
    }

    @Override
    public boolean contains(Object o) {
        for(O value : this) {
            if(Objects.equals(value, o)) return true;
        }
        return false;
    }

    @NotNull
    @Override
    public Iterator<O> iterator() {
        return new LinusLinkedListIterator<>(this);
    }

    @NotNull
    @Override
    public Object[] toArray() {
        Object[] array = new Object[size];
        int i = 0;
        for(O value : this) {
            array[i++] = value;
        }
        return array;
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {
        return (T[]) Arrays.copyOf(toArray(), size, a.getClass());
    }

    @Override
    public boolean add(O o) {
        synchronized (lock) {
            LinusLinkedListEntry<O> entry = new LinusLinkedListEntry<>(o);

            tail.setNext(entry);
            tail = entry;

            size++;
            return true;
        }
    }

    @Override
    public boolean remove(Object o) {
        synchronized (lock) {
            Iterator<O> iterator = iterator();

            while (iterator().hasNext()){
                if(Objects.equals(iterator.next(), o)){
                    iterator.remove();
                    return true;
                }
            }

            return false;
        }

    }

    /**
     * removes removeEntry from this lit
     * @param beforeEntry the entry before the entry to remove
     * @param removeEntry entry to remove
     */
    void remove(@NotNull LinusLinkedListEntry<O> beforeEntry, @NotNull LinusLinkedListEntry<O> removeEntry) {
        synchronized (lock) {
            if(removeEntry == tail) {
                //we are removing the last entry, so we need to change the tail
                tail = beforeEntry;
                beforeEntry.setNext(null);

            } else {
                beforeEntry.setNext(removeEntry.getNext());

            }
            size--;
        }
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends O> c) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends O> c) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void clear() {
        head.setNext(null);
    }

    @Override
    public O get(int index) {
        int i = 0;
        for(O value : this){
            if(i++ == index) return value;
        }
        throw new IndexOutOfBoundsException(index);
    }

    @Override
    public O set(int index, O element) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void add(int index, O element) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public O remove(int index) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @NotNull
    @Override
    public ListIterator<O> listIterator() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @NotNull
    @Override
    public ListIterator<O> listIterator(int index) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @NotNull
    @Override
    public List<O> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @NotNull LinusLinkedListEntry<O> getHead() {
        return head;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");

        boolean first = true;
        for(O value : this) {
            if(!first){
                sb.append(", ");
            } else first = false;
            sb.append(value);
        }

        sb.append("]");

        return sb.toString();
    }
}
