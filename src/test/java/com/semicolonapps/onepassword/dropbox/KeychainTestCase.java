package com.semicolonapps.onepassword.dropbox;

public abstract class KeychainTestCase {
    protected static final String ITEM1 = "[\"item1\",\"type\",\"name1\",\"domain\",1,\"\",0,\"N\"]";
    protected static final String ITEM2 = "[\"item2\",\"type\",\"name2\",\"domain\",2,\"\",0,\"N\"]";
    protected static final String ITEM3 = "[\"item3\",\"type\",\"name3\",\"domain\",3,\"\",0,\"N\"]";
    protected static final String CONTENTS = "[" + ITEM1 + ",\n" + ITEM2 + ",\n" + ITEM3 + "]";
}
