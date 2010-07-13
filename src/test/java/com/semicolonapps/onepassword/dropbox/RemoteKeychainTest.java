package com.semicolonapps.onepassword.dropbox;

import com.dropbox.client.DropboxClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RemoteKeychainTest extends KeychainTestCase {
    private static final String LOCATION = "/location/1Password.agilekeychain";

    @Mock DropboxClient client;
    RemoteKeychain keychain;

    @Before
    public void setup() {
        keychain = new RemoteKeychain(client, LOCATION);
    }

    @Test
    public void shouldGetEncryptionKeys() throws Exception {
        HttpResponse response = expectToGetFile("encryptionKeys.js", "encryption keys");
        assertEquals("encryption keys", keychain.getEncryptionKeys());
        verify(response.getEntity()).consumeContent();
    }

    @Test(expected = RuntimeException.class)
    public void testGetEncryptionKeysWhenNotA200() throws Exception {
        HttpResponse response = expectToGetFile("encryptionKeys.js", 400);
        keychain.getEncryptionKeys();
    }

    @Test
    public void shouldGetItems() throws Exception {
        HttpResponse response = expectToGetFile("contents.js", CONTENTS);
        List<Item> items = keychain.getItems();
        assertEquals(3, items.size());
        assertItem(items, 0, ITEM1);
        assertItem(items, 1, ITEM2);
        assertItem(items, 2, ITEM3);
        verify(response.getEntity()).consumeContent();
    }

    @Test
    public void shouldGetItem() throws Exception {
        HttpResponse response = expectToGetFile("item.1password", "item contents");
        assertEquals("item contents", keychain.getItem("item"));
        verify(response.getEntity()).consumeContent();
    }

    private void assertItem(List<Item> items, int index, String raw) {
        Item item = items.get(index);
        assertEquals("item" + (index + 1), item.getId());
        assertEquals(index + 1, item.getTimestamp());
        assertEquals(raw, item.getRaw());
    }

    private HttpResponse expectToGetFile(String filename, int statusCode) throws Exception {
        return expectToGetFile(filename, "", statusCode);
    }

    private HttpResponse expectToGetFile(String fileName, String expectedContents) throws Exception {
        return expectToGetFile(fileName, expectedContents, 200);
    }

    private HttpResponse expectToGetFile(String fileName, String expectedContents, int statusCode) throws Exception {
        StatusLine statusLine = mock(StatusLine.class);
        HttpResponse response = mock(HttpResponse.class);
        HttpEntity entity = mock(HttpEntity.class);
        when(client.getFile("dropbox", LOCATION + "/data/default/" + fileName)).thenReturn(response);
        when(response.getEntity()).thenReturn(entity);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(statusCode);
        when(entity.getContent()).thenReturn(new ByteArrayInputStream(expectedContents.getBytes()));
        return response;
    }
}
