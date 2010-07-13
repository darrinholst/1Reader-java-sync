package com.semicolonapps.onepassword.dropbox;

public class Item {
    private String id;
    private long timestamp;
    private String raw;

    public Item(String id, long timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }

    public Item(String id, long timestamp, String raw) {
        this(id, timestamp);
        this.raw = raw;
    }

    public String getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getRaw() {
        return raw;
    }
}
