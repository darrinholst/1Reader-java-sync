package com.semicolonapps.onepassword.dropbox;

import com.dropbox.client.DropboxClient;
import com.dropbox.client.DropboxException;
import com.dropbox.client.TrustedAuthenticator;
import oauth.signpost.OAuthConsumer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationTest {
    private Token consumerToken = new Token("consumerKey", "consumerSecret");
    @Mock TrustedAuthenticator authenticator;
    @Mock OAuthConsumer consumer;
    @Mock Config config;
    @InjectMocks @Spy private Authentication authentication = new Authentication(consumerToken);

    @Before
    public void setup() {
        authenticator.consumer = consumer;
    }

    @Test
    public void testGetAccessToken() throws Exception {
        doReturn(authenticator).when(authentication).getAuthenticator();
        when(authenticator.retrieveTrustedAccessToken("username", "password")).thenReturn(true);
        when(consumer.getToken()).thenReturn("key");
        when(consumer.getTokenSecret()).thenReturn("secret");

        Token token = authentication.getAccessTokenFor("username", "password");

        assertEquals("key", token.key);
        assertEquals("secret", token.secret);
    }

    @Test(expected = RuntimeException.class)
    public void testGetAccessTokenWhenAuthenticationFails() throws Exception {
        doReturn(authenticator).when(authentication).getAuthenticator();
        when(authenticator.retrieveTrustedAccessToken("username", "password")).thenReturn(false);
        authentication.getAccessTokenFor("username", "password");
    }

    @Test
    public void testGetAuthenticator() throws Exception {
        HashMap map = new HashMap();
        map.put("port", 80l);
        when(config.createFor(consumerToken)).thenReturn(map);
        TrustedAuthenticator trustedAuthenticator = authentication.getAuthenticator();
        assertSame(map, trustedAuthenticator.config);
    }

    @Test
    public void testCheckAccessToken() throws Exception {
        DropboxClient client = mock(DropboxClient.class);
        HashMap accountInfo = new HashMap();
        accountInfo.put("uid", "foo");

        Token accessToken = new Token("key", "secret");
        doReturn(client).when(authentication).getDropboxClient(accessToken);
        when(client.accountInfo(false, null)).thenReturn(accountInfo);

        assertTrue(authentication.check(accessToken));
    }

    @Test
    public void testCheckAccessTokenWhenAccountInfoReturnedDoesNotContainAnUid() throws Exception {
        DropboxClient client = mock(DropboxClient.class);

        Token accessToken = new Token("key", "secret");
        doReturn(client).when(authentication).getDropboxClient(accessToken);
        when(client.accountInfo(false, null)).thenReturn(new HashMap());

        assertFalse(authentication.check(accessToken));
    }

    @Test
    public void testCheckAccessTokenWhenAccountInfoThrows() throws Exception {
        DropboxClient client = mock(DropboxClient.class);

        Token accessToken = new Token("key", "secret");
        doReturn(client).when(authentication).getDropboxClient(accessToken);
        when(client.accountInfo(false, null)).thenThrow(new DropboxException("forced"));

        assertFalse(authentication.check(accessToken));
    }
}
