package com.semicolonapps.onepassword.dropbox;

import com.dropbox.client.DropboxClient;
import com.dropbox.client.TrustedAuthenticator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Sync {
    private Token consumerToken;
    private Token accessToken;
    private Config config = new Config();
    private SyncProgress progress = new NullProgress();

    public Sync(Token consumerToken, Token accessToken) {
        this.consumerToken = consumerToken;
        this.accessToken = accessToken;
    }

    public void sync(String source, String destination) {
        progress.started();

        DropboxClient client = getClient(accessToken);
        RemoteKeychain remote = remoteKeychain(source, client);
        LocalKeychain local = localKeychain(destination);

        progress.retrievingEncryptionKeys();
        local.addEncryptionKeys(remote.getEncryptionKeys());

        progress.retrievingItemList();
        List<Item> remoteItems = remote.getItems();
        long lastSyncTime = local.lastSyncTime();
        local.addItems(remoteItems);

        List<String> idsToSync = new ArrayList<String>();

        for(Item item : remoteItems) {
            if(item.getTimestamp() > lastSyncTime || !local.itemExists(item.getId())) {
                idsToSync.add(item.getId());
            }
        }

        progress.itemsToSync(idsToSync.size());

        for(String id : idsToSync) {
            progress.retrievingItem(id);
            local.addItem(id, remote.getItem(id));
        }

        progress.completed();
    }

    public void sync(String source, String destination, SyncProgress progress) {
        this.progress = progress;
        sync(source, destination);
    }

    protected LocalKeychain localKeychain(String destination) {
        return new LocalKeychain(destination);
    }

    protected RemoteKeychain remoteKeychain(String source, DropboxClient client) {
        return new RemoteKeychain(client, source);
    }

    protected DropboxClient getClient(Token accessToken) {
        try {
            Map config = this.config.createFor(consumerToken, accessToken);
            return new DropboxClient(config, new TrustedAuthenticator(config));
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class NullProgress implements SyncProgress {
        public void started() {
        }

        public void completed() {
        }

        public void retrievingEncryptionKeys() {
        }

        public void retrievingItemList() {
        }

        public void itemsToSync(int count) {
        }

        public void retrievingItem(String id) {
        }
    }
}
