package com.no.loliSnatcher;

/**
 * A BooruItem stores information about an image from the booru site the
 */
public class BooruItem {
    // Full Res
    private String fileURL;
    // Low Res
    private String sampleURL;
    // Very low Res
    private String thumbnailURL;
    private String tags;
    private String postURL;
    private int width;
    private int height;
    public BooruItem(String fileURL, String sampleURL,String thumbnailURL, String tags,String postURL, int height, int width){
        this.fileURL = fileURL;
        this.sampleURL = sampleURL;
        this.thumbnailURL = thumbnailURL;
        this.tags = tags;
        this.postURL = postURL;
        this.height = height;
        this.width = width;
    }

    public String getFileURL() {
        return fileURL;
    }

    public String getPostURL() {
        return postURL;
    }

    public String getSampleURL() {
        return sampleURL;
    }

    public String getTags() {
        return tags;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
