package com.semicolonapps.onepassword.dropbox;

public interface SyncProgress {
    void started();

    void completed();

    void retrievingEncryptionKeys();

    void retrievingItemList();

    void itemsToSync(int count);

    void retrievingItem(String id);
}
