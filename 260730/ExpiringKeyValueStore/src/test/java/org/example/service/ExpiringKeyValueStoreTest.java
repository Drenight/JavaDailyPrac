package org.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExpiringKeyValueStoreTest {

    private ExpiringKeyValueStore expiringKeyValueStore;

    public class MutableClock extends Clock{
        private Instant instant;

        public MutableClock(Instant start){
            instant = start;
        }

        @Override
        public ZoneId getZone(){
            return ZoneId.of("UTC");
        }

        @Override
        public Clock withZone(ZoneId zone) {
            if (!ZoneOffset.UTC.equals(zone)) {
                throw new UnsupportedOperationException("UTC only");
            }
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }

        public void pass(Duration duration){
            instant = instant.plus(duration);
        }
    }

    @BeforeEach
    public void init() {

    }

    @Test
    public void testBasicHappyPaths() {
        MutableClock mutableClock = new MutableClock(Instant.parse("2007-12-03T10:15:30.00Z"));
        expiringKeyValueStore = new ExpiringKeyValueStore(mutableClock);

        assertNull(expiringKeyValueStore.get("1"));

        assertEquals(true, expiringKeyValueStore.put("1", "1"));
        assertEquals("1", expiringKeyValueStore.get("1").value());

        assertEquals(true, expiringKeyValueStore.del("1"));
        assertNull(expiringKeyValueStore.get("1"));

        assertEquals(true, expiringKeyValueStore.put("1", "1", Duration.of(30, ChronoUnit.SECONDS)));
        assertEquals("1", expiringKeyValueStore.get("1").value());
        mutableClock.pass(Duration.of(29, ChronoUnit.SECONDS));
        assertEquals("1", expiringKeyValueStore.get("1").value());
        mutableClock.pass(Duration.of(1, ChronoUnit.SECONDS));
        assertNull(expiringKeyValueStore.get("1"));
    }

    @Test
    public void testWriteCover(){
        MutableClock mutableClock = new MutableClock(Instant.parse("2007-12-03T10:15:30.00Z"));
        expiringKeyValueStore = new ExpiringKeyValueStore(mutableClock);

        assertEquals(true, expiringKeyValueStore.put("1", "1"));
        assertEquals("1", expiringKeyValueStore.get("1").value());

        assertEquals(true, expiringKeyValueStore.put("1", "2", Duration.of(30, ChronoUnit.SECONDS)));
        assertEquals("2", expiringKeyValueStore.get("1").value());
        mutableClock.pass(Duration.of(30, ChronoUnit.SECONDS));
        assertNull(expiringKeyValueStore.get("1"));

        assertEquals(true, expiringKeyValueStore.put("2", "1", Duration.of(30, ChronoUnit.SECONDS)));
        assertEquals("1", expiringKeyValueStore.get("2").value());
        assertEquals(true, expiringKeyValueStore.put("2", "2"));
        assertEquals("2", expiringKeyValueStore.get("2").value());
        mutableClock.pass(Duration.of(30, ChronoUnit.SECONDS));
        assertEquals("2", expiringKeyValueStore.get("2").value());
    }

    @Test
    public void testDel(){
        MutableClock mutableClock = new MutableClock(Instant.parse("2007-12-03T10:15:30.00Z"));
        expiringKeyValueStore = new ExpiringKeyValueStore(mutableClock);

        assertEquals(true, expiringKeyValueStore.put("1", "1"));
        assertEquals("1", expiringKeyValueStore.get("1").value());
        assertTrue(expiringKeyValueStore.del("1"));
        assertFalse(expiringKeyValueStore.del("1"));

        assertEquals(true, expiringKeyValueStore.put("1", "1", Duration.of(30, ChronoUnit.SECONDS)));
        mutableClock.pass(Duration.of(30, ChronoUnit.SECONDS));
        assertFalse(expiringKeyValueStore.del("1"));
    }

    @Test
    public void testInvalidDuration(){
        MutableClock mutableClock = new MutableClock(Instant.parse("2007-12-03T10:15:30.00Z"));
        expiringKeyValueStore = new ExpiringKeyValueStore(mutableClock);
        assertEquals(false, expiringKeyValueStore.put("1", "1", Duration.of(-1, ChronoUnit.SECONDS)));
    }
}