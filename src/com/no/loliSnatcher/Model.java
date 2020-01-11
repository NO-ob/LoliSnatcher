package com.no.loliSnatcher;

import java.util.ArrayList;

public class Model {
    public ArrayList<BooruItem> search(String tags){
        BooruHandler gelbooru = new BooruHandler();
        return gelbooru.Search(tags);
    }

}
