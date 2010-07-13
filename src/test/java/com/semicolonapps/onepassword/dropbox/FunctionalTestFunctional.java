package com.semicolonapps.onepassword.dropbox;

public class FunctionalTestFunctional {
    private static final String CONSUMER_KEY = "2w0iy8eh6qhiz2f";
    private static final String CONSUMER_SECRET = "7zz1ma1mzrgtgyr";
    private static final Token CONSUMER_TOKEN = new Token(CONSUMER_KEY, CONSUMER_SECRET);
    private static final String ACCESS_KEY = "c9rh0zc3r6r7akr";
    private static final String ACCESS_SECRET = "gxepa2e4gvk5voz";
    private static final Token ACCESS_TOKEN = new Token(ACCESS_KEY, ACCESS_SECRET);
    private static final String USERNAME = "darrinholst@gmail.com";
    private static final String PASSWORD = "Tyh81YqRkFvyFVZnSpQXRzXX";

    public static void main(String[] args) {
//        Token accessToken = new Authentication(new Token(CONSUMER_KEY, CONSUMER_SECRET)).getAccessTokenFor(USERNAME, PASSWORD);
//        System.out.println("accessToken.key = " + accessToken.key);
//        System.out.println("accessToken.secret = " + accessToken.secret);

        Sync sync = new Sync(CONSUMER_TOKEN, ACCESS_TOKEN);
        sync.sync("/1Password/1Password.agilekeychain", "/Users/dholst/Desktop/keychain");
    }
}
