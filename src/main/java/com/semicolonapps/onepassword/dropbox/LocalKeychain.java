package com.semicolonapps.onepassword.dropbox;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;

public class LocalKeychain extends Keychain {
    private static final String PROFILE_LOCATION = "data/default";
    private static final String ENCRYPTION_KEYS_FILENAME = "encryptionKeys.js";
    private static final String ITEM_EXTENSION = ".1password";
    private static final String CONTENTS_FILENAME = "contents.js";

    private File location;

    public LocalKeychain(String location) {
        this.location = new File(location, PROFILE_LOCATION);
        this.location.mkdirs();
    }

    public void addEncryptionKeys(String contents) {
        writeFile(ENCRYPTION_KEYS_FILENAME, contents);
    }

    public void addItems(List<Item> items) {
        StringBuffer contents = new StringBuffer("[");
        String separator = "";

        for(Item item : items) {
            contents.append(separator).append(item.getRaw());
            separator = ",\n";
        }

        contents.append("]");

        writeFile(CONTENTS_FILENAME, contents.toString());
    }

    public void addItem(String id, String contents) {
        writeFile(id + ITEM_EXTENSION, contents);
    }

    public long lastSyncTime() {
        long lastSyncTime = 0;

        try {
            List<Item> items = parseItems(readFile(CONTENTS_FILENAME));

            for(Item item : items) {
                if(item.getTimestamp() > lastSyncTime) {
                    lastSyncTime = item.getTimestamp();
                }
            }

            return lastSyncTime;
        }
        catch(Exception e) {
            return 0;
        }
    }

    public boolean itemExists(String id) {
        return new File(location, id + ITEM_EXTENSION).exists();
    }

    private String readFile(String fileName) {
        try {
            return FileUtils.readFileToString(new File(location, fileName));
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeFile(String fileName, String contents) {
        try {
            FileUtils.writeStringToFile(new File(location, fileName), contents);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
