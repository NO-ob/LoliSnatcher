package com.no.loliSnatcher;

import javax.net.ssl.HttpsURLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BooruHandler {
    protected int pageNum = 0;
    //Max images to be fetched per page
    protected int limit = 20;
    // Last searched tags
    protected String prevTags = "";
    ArrayList<BooruItem> fetched = new ArrayList<BooruItem>();
    BooruHandler(){}

    abstract public ArrayList Search(String tags);
    abstract protected ArrayList getItems(HttpsURLConnection conn);
    abstract protected String getFileURL(String input);
    abstract protected String getSampleURL(String input);
    abstract protected String getThumbnailURL(String input);
    abstract protected String getTags(String input);
    abstract protected String getPostURL(String input);

}
