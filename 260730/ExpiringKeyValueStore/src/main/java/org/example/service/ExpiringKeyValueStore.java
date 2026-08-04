package org.example.service;

import org.example.dto.Entry;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class ExpiringKeyValueStore {
    private final Clock clock;
    private final HashMap<String, Entry> mp; //source of truth
    private final PriorityQueue<Entry> pq; //derived ds

    public ExpiringKeyValueStore(Clock clock) {
        this.clock = clock;
        this.mp = new HashMap<>();
        this.pq = new PriorityQueue<>(Comparator.comparing(Entry::exprTime));
    }

    //1. W forever data; store.put("language", "java"); second write with cover
    public Boolean put(String key, String value) {
        Entry newEntry = new Entry(key, value, null, true);
        Entry oldEntry = mp.get(key);
        mp.put(key, newEntry);
        return true;
    }

    //2. W data w/ ttl; store.put("verification-code", "123456", Duration.ofSeconds(30));
    public Boolean put(String key, String value, Duration duration) {
        if (duration.isNegative() || duration.isZero()) {
            return false;
        }
        Instant now = Instant.now(clock);
        cleanInvalidTillTopValid(now);
        Instant exprTime = now.plus(duration);
        Entry newEntry = new Entry(key, value, exprTime, false);
        pq.add(newEntry);
        mp.put(key, newEntry);
        return true;
    }

    //3. R data w/ key; store.get("language") -> java;
    public Entry get(String key) {
        if (!mp.containsKey(key)) {
            return null;
        }
        Instant now = Instant.now(clock);
        Entry entry = mp.get(key);
        if (!entry.isForever() && (entry.exprTime().isBefore(now) || entry.exprTime().equals(now))) {
            //already expired
            mp.remove(key);
            return null;
        } else {
            //still in time
            return entry;
        }
    }

    //4. delete data
    public Boolean del(String key) {
        cleanInvalidTillTopValid(Instant.now(clock));
        if (!mp.containsKey(key)) {
            return false;
        } else {
            mp.remove(key);
            return true;
        }
    }

    //5. R amount of valid data
    public Integer getAmount() {
        cleanInvalidTillTopValid(Instant.now(clock));
        return mp.size();
    }

    private void cleanInvalidTillTopValid(Instant now) {
        while (pq.size() != 0) {
            Entry top = pq.poll();
            if (!mp.containsKey(top.key()) || top != mp.get(top.key())) {
                //out sync scenario, remove derive
                continue;
            } else if (now.isAfter(top.exprTime()) || now.equals(top.exprTime())) {
                //confirmed sync with source, so also update the source
                mp.remove(top.key());
                continue;
            } else {
                //the top one is still valid and sync
                pq.add(top);
                break;
            }
        }
    }
}
