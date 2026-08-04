package org.example;

import org.example.dto.ComponentViewRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.util.Collections.reverse;

public class RecentViewedListSolver {
    private final int maxListingsPerUser;
    private final HashMap<String, RecentViewedList> map;

    public RecentViewedListSolver(int maxListingsPerUser) {
        this.maxListingsPerUser = maxListingsPerUser;
        map = new HashMap<>();
    }

    public void insertRVL(String userId, String listingId/*, Instant ts*/){
        RecentViewedList singleUserLinkedHashMap = map.get(userId);
        if (singleUserLinkedHashMap == null) {
            singleUserLinkedHashMap = new RecentViewedList(this.maxListingsPerUser);
            map.put(userId, singleUserLinkedHashMap);
        }

        if(singleUserLinkedHashMap.containsKey(listingId)){
            singleUserLinkedHashMap.get(listingId);
            return;
        }

        ComponentViewRecord newComponentViewRecord = new ComponentViewRecord(listingId);
        singleUserLinkedHashMap.put(listingId, newComponentViewRecord);
    }

    public List<ComponentViewRecord> getRVL(String userId, int limit){
        if(map == null || map.isEmpty() || map.get(userId) == null){
            return List.of();
        }
        List<ComponentViewRecord> rvl = new ArrayList<>();
        map.get(userId).forEach((k,v)->{
            rvl.add(v);
        });
        reverse(rvl);
        return rvl.subList(0, limit);
    }
}
