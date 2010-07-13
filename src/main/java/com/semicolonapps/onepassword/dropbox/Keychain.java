package com.semicolonapps.onepassword.dropbox;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public abstract class Keychain {
    protected List<Item> parseItems(String s) {
        try {
            List<Item> items = new ArrayList<Item>();
            JSONArray jsonArray = new JSONArray(s);

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONArray jsonItem = (JSONArray) jsonArray.get(i);
                items.add(new Item(jsonItem.getString(0), jsonItem.getLong(4), jsonItem.toString()));
            }

            return items;
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
