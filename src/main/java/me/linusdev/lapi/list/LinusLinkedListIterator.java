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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class LinusLinkedListIterator<O> implements Iterator<O> {

    private final @NotNull LinusLinkedList<O> list;

    private @Nullable LinusLinkedListEntry<O> lastEntry;
    private @NotNull LinusLinkedListEntry<O> currentEntry;

    public LinusLinkedListIterator(@NotNull LinusLinkedList<O> list) {
        this.list = list;
        this.currentEntry = list.getHead();
        this.lastEntry = null;
    }

    @Override
    public boolean hasNext() {
        return currentEntry.getNext() != null;
    }

    @Override
    public O next() {
        if(currentEntry.getNext() == null)
            throw new NoSuchElementException();
        lastEntry = currentEntry;
        currentEntry = currentEntry.getNext();
        return currentEntry.getValue();
    }

    @Override
    public void remove() {
        if(lastEntry == null)
            throw new IllegalStateException("element cant be removed. next must be called");

        list.remove(lastEntry, currentEntry);
        lastEntry = null;
    }
}
