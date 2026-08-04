package org.example;

import org.example.dto.ComponentViewRecord;

import java.util.LinkedHashMap;
import java.util.Map;

public class RecentViewedList extends LinkedHashMap<String, ComponentViewRecord> {
    private final int capacity;
    private final LinkedHashMap<String, ComponentViewRecord> linkedHashMap;

    public RecentViewedList(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
        this.linkedHashMap = new LinkedHashMap<>(capacity, 0.75f, true){
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, ComponentViewRecord> eldest) {
                return size() > capacity;
            }
        };
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<String, ComponentViewRecord> eldest){
        return this.size()>this.capacity;
    }
}
