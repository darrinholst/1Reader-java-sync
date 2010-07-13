package com.semicolonapps.onepassword.dropbox;

import com.dropbox.client.DropboxClient;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;

import java.util.List;

public class RemoteKeychain extends Keychain {
    private DropboxClient client;
    private String location;

    public RemoteKeychain(DropboxClient client, String location) {
        this.client = client;
        this.location = location;
    }

    public String getEncryptionKeys() {
        return getFile("encryptionKeys.js");
    }

    public List<Item> getItems() {
        String contents = getFile("contents.js");
        return parseItems(contents);
    }

    public String getItem(String id) {
        return getFile(id + ".1password");
    }

    private String getFile(String filename) {
        try {
            HttpResponse response = client.getFile("dropbox", location + "/data/default/" + filename);

            if(response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Unable to get file - " + response.getStatusLine().getStatusCode());
            }

            String contents = IOUtils.toString(response.getEntity().getContent());
            response.getEntity().consumeContent();
            return contents;
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
