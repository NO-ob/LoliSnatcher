package com.no.loliSnatcher;

public class BooruItem {
    String fileURL;
    String sampleURL;
    String thumbnailURL;
    String tags;
    String postURL;
    public BooruItem(String fileurl, String sampleurl,String thumburl, String tag,String posturl){
        fileURL = fileurl;
        sampleURL = sampleurl;
        thumbnailURL = thumburl;
        tags = tag;
        postURL = posturl;
    }
}
