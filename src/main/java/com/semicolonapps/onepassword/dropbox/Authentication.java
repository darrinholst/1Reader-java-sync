package com.semicolonapps.onepassword.dropbox;

import com.dropbox.client.DropboxClient;
import com.dropbox.client.DropboxException;
import com.dropbox.client.TrustedAuthenticator;

import java.util.Map;

public class Authentication {
    private Token consumerToken;
    Config config = new Config();

    public Authentication(Token consumerToken) {
        this.consumerToken = consumerToken;
    }

    public Token getAccessTokenFor(String username, String password) {
        try {
            TrustedAuthenticator authenticator = getAuthenticator();
            boolean success = authenticator.retrieveTrustedAccessToken(username, password);

            if(success) {
                return new Token(authenticator.consumer.getToken(), authenticator.consumer.getTokenSecret());
            }
            else {
                throw new RuntimeException("Not Authenticated");
            }
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean check(Token accessToken) {
        DropboxClient client = getDropboxClient(accessToken);

        if(client == null) return false;

        try {
            Map accountInfo = client.accountInfo(false, null);
            return accountInfo.get("uid") != null;
        }
        catch(Exception e) {
            return false;
        }
    }

    protected DropboxClient getDropboxClient(Token accessToken) {
        try {
            Map config = this.config.createFor(consumerToken, accessToken);
            return new DropboxClient(config, new TrustedAuthenticator(config));
        }
        catch(Exception e) {
            return null;
        }
    }

    protected TrustedAuthenticator getAuthenticator() {
        try {
            return new TrustedAuthenticator(config.createFor(consumerToken));
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
