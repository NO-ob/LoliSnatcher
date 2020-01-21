package com.no.loliSnatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;

public class DanbooruHandler extends GelbooruHandler {
    private int pageNum = 0;
    public int limit = 20;
    private String prevTags = "";
    ArrayList<BooruItem> fetched = new ArrayList<BooruItem>();

    public ArrayList Search(String tags){
        String https_url = "https://danbooru.donmai.us/posts.xml?tags=" +
                tags.replaceAll(" ","+") + "&limit=" + limit + "&page=" + pageNum;
        URL url;
        if(!prevTags.equals(tags)){
            System.out.println("Reset Search!");
            fetched = new ArrayList<BooruItem>();
            pageNum = 0;
        }
        prevTags = tags;
        try {

            url = new URL(https_url);
            HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
            pageNum ++;
            return getItems(conn);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
    private ArrayList getItems(HttpsURLConnection conn){
        if(conn!=null){

            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String input;
                String postURL=null;
                String fileURL=null;
                String sampleURL=null;
                String thumbnailURL=null;
                String tagList=null;
                while ((input = br.readLine()) != null){
                    if (input.contains("<id")) {
                        postURL = getPostURL(input);
                    }else if (input.contains("<tag-string>")){
                        tagList = getTags(input);
                    }else if (input.contains("<file-url")){
                        sampleURL = getSampleURL(input);
                    }else if (input.contains("<large-file-url")){
                        fileURL = getFileURL(input);
                    }else if (input.contains("<preview-file-url")){
                        thumbnailURL = getThumbnailURL(input);
                     //null values if at the end of current post since Danbooru doesn't provide urls for all posts
                    }else if (input.contains("</post>")){
                        postURL=null;
                        tagList=null;
                        sampleURL=null;
                        fileURL=null;
                        thumbnailURL=null;
                    }
                    if(!(postURL == null)&&!(fileURL == null)&&!(tagList == null)&&!(thumbnailURL == null)&&!(sampleURL == null)) {
                        fetched.add(new BooruItem(fileURL, sampleURL, thumbnailURL, tagList, postURL));
                        postURL=null;
                        tagList=null;
                        sampleURL=null;
                        fileURL=null;
                        thumbnailURL=null;
                    }
                }
                br.close();

                return fetched;

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }
    private String getFileURL(String input){
        Pattern file_url = Pattern.compile("<large-file-url>(.*?)</large-file-url>");
        Matcher matcher = file_url.matcher(input);
        while(matcher.find()) {
            System.out.println(matcher.group(1));
            return matcher.group(1);
        }
        return null;
    }
    private String getSampleURL(String input){
        Pattern file_url = Pattern.compile("<file-url>(.*?)</file-url>");
        Matcher matcher = file_url.matcher(input);
        while(matcher.find()) {
            System.out.println(matcher.group(1));
            return matcher.group(1);
        }
        return null;
    }
    private String getThumbnailURL(String input){
        Pattern file_url = Pattern.compile("<preview-file-url>(.*?)</preview-file-url>");
        Matcher matcher = file_url.matcher(input);
        while(matcher.find()) {
            System.out.println(matcher.group(1));
            return matcher.group(1);
        }
        return null;
    }
    private String getTags(String input){
        Pattern file_url = Pattern.compile("<tag-string>(.*?)</tag-string>");
        Matcher matcher = file_url.matcher(input);
        while(matcher.find()) {
            System.out.println(matcher.group(1));
            return matcher.group(1);
        }
        return null;
    }
    private String getPostURL(String input){
        Pattern file_url = Pattern.compile("<id type=\"integer\">(.*?)</id>");
        Matcher matcher = file_url.matcher(input);
        while(matcher.find()) {
            return "https://danbooru.donmai.us/posts/" + matcher.group(1);
        }
        return null;
    }

}

