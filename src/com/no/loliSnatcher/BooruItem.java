package com.no.loliSnatcher;

public class BooruItem {
    String fileURL;
    String sampleURL;
    String thumbnailURL;
    String tags;
    String postID;
    public BooruItem(String fileurl, String sampleurl,String thumburl, String tag,String ID){
        fileURL = fileurl;
        sampleURL = sampleurl;
        thumbnailURL = thumburl;
        tags = tag;
        postID = ID;
    }
}
