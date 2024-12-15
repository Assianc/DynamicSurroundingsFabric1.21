package org.orecruncher.dsurround.lib.events;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Event<T> {
    private final List<Consumer<T>> handlers = new ArrayList<>();

    public void register(Consumer<T> handler) {
        this.handlers.add(handler);
    }

    public void unregister(Consumer<T> handler) {
        this.handlers.remove(handler);
    }

    public void raise(T event) {
        for (var handler : this.handlers) {
            handler.accept(event);
        }
    }
} 