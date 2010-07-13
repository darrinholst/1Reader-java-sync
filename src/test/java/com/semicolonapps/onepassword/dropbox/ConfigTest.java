package com.semicolonapps.onepassword.dropbox;

import org.junit.Test;

import java.util.Map;

import static junit.framework.Assert.*;

public class ConfigTest {
    private Token consumerToken = new Token("consumer_key", "consumer_secret");
    private Token accessToken = new Token("access_key", "access_secret");

    @Test
    public void testCreateForJustConsumerToken() {
        Map config = new Config().createFor(consumerToken);
        verifyCommonAttributes(config);
    }

    @Test
    public void testCreateForConsumerTokenAndAccessToken() {
        Map config = new Config().createFor(consumerToken, accessToken);
        verifyCommonAttributes(config);
        assertEquals("access_key", config.get("access_token_key"));
        assertEquals("access_secret", config.get("access_token_secret"));
    }

    private void verifyCommonAttributes(Map config) {
        assertEquals("api.getdropbox.com", config.get("server"));
        assertEquals("api-content.getdropbox.com", config.get("content_server"));
        assertEquals(80l, config.get("port"));
        assertEquals("consumer_key", config.get("consumer_key"));
        assertEquals("consumer_secret", config.get("consumer_secret"));
    }
}
