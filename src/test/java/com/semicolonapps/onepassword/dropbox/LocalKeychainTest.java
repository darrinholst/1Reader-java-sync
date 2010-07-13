package com.semicolonapps.onepassword.dropbox;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Arrays;

import static junit.framework.Assert.*;

public class LocalKeychainTest extends KeychainTestCase {
    @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();
    File location;
    LocalKeychain keychain;

    @Before
    public void setup() {
        location = temporaryFolder.newFolder("1Password.agilekeychain");
        keychain = new LocalKeychain(location.getAbsolutePath());
    }

    @Test
    public void shouldCreateAllDirectoriesOnConstruction() {
        assertEquals(true, new File(location, "data/default").exists());
    }

    @Test
    public void shouldCreateEncryptionKeys() throws Exception {
        keychain.addEncryptionKeys("encryption keys");
        assertEquals("encryption keys", getContentsOf("encryptionKeys.js"));
    }

    @Test
    public void shouldCreateContents() throws Exception {
        Item item1 = new Item("1", 100, "[item1]");
        Item item2 = new Item("2", 200, "[item2]");
        Item item3 = new Item("3", 300, "[item3]");
        keychain.addItems(Arrays.asList(item1, item2, item3));
        assertEquals("[[item1],\n[item2],\n[item3]]", getContentsOf("contents.js"));
    }

    @Test
    public void shouldCreateItem() throws Exception {
        keychain.addItem("id", "item");
        assertEquals("item", getContentsOf("id.1password"));
    }

    @Test
    public void shouldGetLastSyncTime() throws Exception {
        writeFile("contents.js", CONTENTS);
        assertEquals(3, keychain.lastSyncTime());
    }

    @Test
    public void shouldGetLastSyncTimeWhenNoItems() throws Exception {
        writeFile("contents.js", "[]");
        assertEquals(0, keychain.lastSyncTime());
    }

    @Test
    public void shouldGetLastSyncTimeWhenFileNotThere() throws Exception {
        assertEquals(0, keychain.lastSyncTime());
    }

    @Test
    public void shouldExist() throws Exception {
        keychain.addItem("id", "contents");
        assertEquals(true, keychain.itemExists("id"));
    }

    @Test
    public void shouldNotExist() throws Exception {
        assertEquals(false, keychain.itemExists("doesn't exist"));
    }

    private void writeFile(String fileName, String contents) throws Exception {
        FileUtils.writeStringToFile(new File(location, "data/default/" + fileName), contents);
    }

    private String getContentsOf(String fileName) throws Exception {
        return FileUtils.readFileToString(new File(location, "data/default/" + fileName));
    }
}
