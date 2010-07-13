package com.semicolonapps.onepassword.dropbox;

import com.dropbox.client.DropboxClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SyncTest {
    private Token consumerToken = new Token("consumer_key", "consumer_secret");
    private Token accessToken = new Token("access_key", "access_secret");
    @Mock DropboxClient dropboxClient;
    @Mock LocalKeychain localKeychain;
    @Mock RemoteKeychain remoteKeychain;
    @Mock SyncProgress progress;
    @InjectMocks @Spy private Sync sync = new Sync(consumerToken, accessToken);

    @Test
    public void testGetDropboxClient() {
        DropboxClient client = sync.getClient(accessToken);
        assertEquals(accessToken.key, client.auth.getTokenKey());
        assertEquals(accessToken.secret, client.auth.getTokenSecret());
    }

    @Test
    public void shouldSyncAllItems() {
        mockDropboxClient();

        List<Item> remoteItems = Arrays.asList(new Item("item1", 100), new Item("item2", 200));
        when(remoteKeychain.getEncryptionKeys()).thenReturn("encryption keys");
        when(remoteKeychain.getItems()).thenReturn(remoteItems);
        when(remoteKeychain.getItem("item1")).thenReturn("item 1 contents");
        when(remoteKeychain.getItem("item2")).thenReturn("item 2 contents");
        when(localKeychain.lastSyncTime()).thenReturn(0l);

        sync.sync("remote location", "local location", progress);

        verify(localKeychain).addEncryptionKeys("encryption keys");
        verify(localKeychain).addItems(remoteItems);
        verify(localKeychain).addItem("item1", "item 1 contents");
        verify(localKeychain).addItem("item2", "item 2 contents");
    }

    @Test
    public void shouldSyncItemsWithMissingContent() {
        mockDropboxClient();

        List<Item> remoteItems = Arrays.asList(new Item("item1", 100), new Item("item2", 200));
        when(remoteKeychain.getEncryptionKeys()).thenReturn("encryption keys");
        when(remoteKeychain.getItems()).thenReturn(remoteItems);
        when(localKeychain.lastSyncTime()).thenReturn(200l);
        when(localKeychain.itemExists("item1")).thenReturn(true);
        when(localKeychain.itemExists("item2")).thenReturn(false);
        when(remoteKeychain.getItem("item2")).thenReturn("item 2 contents");

        sync.sync("remote location", "local location");

        verify(localKeychain).addEncryptionKeys("encryption keys");
        verify(localKeychain).addItems(remoteItems);
        verify(localKeychain, never()).addItem(eq("item1"), anyString());
        verify(localKeychain).addItem("item2", "item 2 contents");
    }

    @Test
    public void shouldNotSyncAnyItems() {
        mockDropboxClient();

        List<Item> remoteItems = Arrays.asList(new Item("item1", 100), new Item("item2", 200));
        when(remoteKeychain.getEncryptionKeys()).thenReturn("encryption keys");
        when(remoteKeychain.getItems()).thenReturn(remoteItems);
        when(localKeychain.lastSyncTime()).thenReturn(200l);
        when(localKeychain.itemExists("item1")).thenReturn(true);
        when(localKeychain.itemExists("item2")).thenReturn(true);

        sync.sync("remote location", "local location");

        verify(localKeychain).addEncryptionKeys("encryption keys");
        verify(localKeychain).addItems(remoteItems);
        verify(localKeychain, never()).addItem(anyString(), anyString());
    }

    @Test
    public void shouldOnlySyncOutOfDateItems() {
        mockDropboxClient();

        List<Item> remoteItems = Arrays.asList(new Item("item1", 100), new Item("item2", 200));
        when(localKeychain.lastSyncTime()).thenReturn(100l);
        when(remoteKeychain.getEncryptionKeys()).thenReturn("encryption keys");
        when(remoteKeychain.getItems()).thenReturn(remoteItems);
        when(remoteKeychain.getItem("item2")).thenReturn("item 2 contents");
        when(localKeychain.itemExists("item1")).thenReturn(true);

        sync.sync("remote location", "local location");

        InOrder inOrder = inOrder(localKeychain);
        inOrder.verify(localKeychain).lastSyncTime();
        inOrder.verify(localKeychain).addItems(remoteItems);

        verify(localKeychain).addEncryptionKeys("encryption keys");
        verify(localKeychain, never()).addItem(eq("item1"), anyString());
        verify(localKeychain).addItem("item2", "item 2 contents");
    }

    @Test
    public void testShouldProvideProgress() {
        SyncProgress progress = mock(SyncProgress.class);
        mockDropboxClient();

        List<Item> remoteItems = Arrays.asList(new Item("item1", 100), new Item("item2", 200));
        when(remoteKeychain.getEncryptionKeys()).thenReturn("encryption keys");
        when(remoteKeychain.getItems()).thenReturn(remoteItems);
        when(remoteKeychain.getItem("item1")).thenReturn("item 1 contents");
        when(remoteKeychain.getItem("item2")).thenReturn("item 2 contents");
        when(localKeychain.lastSyncTime()).thenReturn(0l);

        sync.sync("remote location", "local location", progress);

        InOrder inOrder = inOrder(progress);
        inOrder.verify(progress).started();
        inOrder.verify(progress).retrievingEncryptionKeys();
        inOrder.verify(progress).retrievingItemList();
        inOrder.verify(progress).itemsToSync(2);
        inOrder.verify(progress).retrievingItem("item1");
        inOrder.verify(progress).retrievingItem("item2");
        inOrder.verify(progress).completed();

    }

    private void mockDropboxClient() {
        doReturn(dropboxClient).when(sync).getClient(accessToken);
        doReturn(localKeychain).when(sync).localKeychain("local location");
        doReturn(remoteKeychain).when(sync).remoteKeychain("remote location", dropboxClient);
    }

}
