package com.no.loliSnatcher;

/**
 * A BooruItem stores information about an image from the booru site the
 */
public class BooruItem {
    // Full Res
    String fileURL;
    // Low Res
    String sampleURL;
    // Very low Res
    String thumbnailURL;
    String tags;
    String postURL;
    public BooruItem(String fileURL, String sampleURL,String thumbnailURL, String tags,String postURL){
        this.fileURL = fileURL;
        this.sampleURL = sampleURL;
        this.thumbnailURL = thumbnailURL;
        this.tags = tags;
        this.postURL = postURL;
    }
}
