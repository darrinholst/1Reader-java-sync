package com.semicolonapps.onepassword.dropbox;

import java.util.HashMap;
import java.util.Map;

public class Config {
    public Map createFor(Token consumerToken) {
        HashMap config = new HashMap();
        config.put("server", "api.getdropbox.com");
        config.put("content_server", "api-content.getdropbox.com");
        config.put("port", 80l);
        config.put("consumer_key", consumerToken.key);
        config.put("consumer_secret", consumerToken.secret);
        return config;
    }

    public Map createFor(Token consumerToken, Token accessToken) {
        Map config = createFor(consumerToken);
        config.put("access_token_key", accessToken.key);
        config.put("access_token_secret", accessToken.secret);
        return config;
    }
}
