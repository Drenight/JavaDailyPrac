package org.example;

import org.example.dto.ConfigVersion;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class VersionedConfigStore {

    HashMap<String, HashMap<Long, ConfigVersion>> mp;

    public VersionedConfigStore() {
        mp = new HashMap<>();
    }

    void basicParameterCheck(String key, String value) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Invalid key is passed");
        }
        if (value == null) {
            throw new IllegalArgumentException("Invalid value is passed");
        }
    }

    ConfigVersion create(String key, String value) {
        basicParameterCheck(key, value);

        ConfigVersion newConfigVersion = null;
        if (!mp.containsKey(key)) {
            HashMap<Long, ConfigVersion> newSubMap = new LinkedHashMap<>();
            newConfigVersion = new ConfigVersion(
                    key,
                    value,
                    1
            );
            newSubMap.put(1l, newConfigVersion);
            mp.put(key, newSubMap);
        } else {
            throw new IllegalStateException("Same key is already set, use update instead");
        }

        return newConfigVersion;
    }

    ConfigVersion update(String key, String value, long expectedVersion) {
        basicParameterCheck(key, value);
        if (expectedVersion <= 0) {
            throw new IllegalArgumentException("ExpectedVersion is not allowed to be <=0");
        }
        if (!mp.containsKey(key)) {
            throw new IllegalArgumentException("Not existed key is passed");
        }

        HashMap<Long, ConfigVersion> nowSubMap = mp.get(key);
        if (expectedVersion != nowSubMap.size()) {
            throw new IllegalStateException("version number is not expected");
        }
        ConfigVersion newConfigVersion = new ConfigVersion(key, value, expectedVersion + 1);
        nowSubMap.put(expectedVersion + 1, newConfigVersion);
        mp.put(key, nowSubMap);
        return newConfigVersion;
    }

    ConfigVersion getLatest(String key) {
        basicParameterCheck(key, "1");
        if (!mp.containsKey(key)) {
            throw new IllegalArgumentException("Not existed key is passed");
        }
        HashMap<Long, ConfigVersion> nowSubMap = mp.get(key);
        ConfigVersion nowConfigVersion = nowSubMap.get(nowSubMap.size());
        return nowConfigVersion;
    }

    ConfigVersion getVersion(String key, long version) {
        basicParameterCheck(key, "1");
        if (!mp.containsKey(key)) {
            throw new IllegalArgumentException("Not existed key is passed");
        }
        if (version <= 0) {
            throw new IllegalArgumentException("version is not allowed to be <=0");
        }
        HashMap<Long, ConfigVersion> nowSubMap = mp.get(key);
        if (!nowSubMap.containsKey(version)) {
            throw new IllegalStateException("Not existed version queried");
        }
        ConfigVersion nowConfigVersion = nowSubMap.get(version);
        return nowConfigVersion;
    }

    ConfigVersion rollback(String key, long targetVersion, long expectedVersion) {
        basicParameterCheck(key, "1");
        if (!mp.containsKey(key)) {
            throw new IllegalArgumentException("Not existed key is passed");
        }
        if (targetVersion > expectedVersion) {
            throw new IllegalArgumentException("targetVersion is not allowed to be > expectedVersion");
        }
        if (targetVersion <= 0) {
            throw new IllegalArgumentException("targetVersion is not allowed to be <=0");
        }
        if (expectedVersion <= 0) {
            throw new IllegalArgumentException("expectedVersion is not allowed to be <=0");
        }
        HashMap<Long, ConfigVersion> nowSubMap = mp.get(key);
        if (expectedVersion != nowSubMap.size()) {
            throw new IllegalStateException("version number is not expected");
        }
        long nxtVersion = nowSubMap.size() + 1;
        ConfigVersion oldConfigVersion = nowSubMap.get(targetVersion);
        ConfigVersion newConfigVersion = new ConfigVersion(
                key,
                oldConfigVersion.value(),
                nxtVersion
        );
        nowSubMap.put(nxtVersion, newConfigVersion);
        mp.put(key, nowSubMap);
        return oldConfigVersion;
    }
}
