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

public class GelbooruHandler {
private int pageNum = 0;
public int limit = 20;
private String prevTags = "";
public String apiKey = "";
public String userID = "";
ArrayList<BooruItem> fetched = new ArrayList<BooruItem>();

    public ArrayList Search(String tags){
        String https_url = "https://gelbooru.com/index.php?page=dapi&s=post&q=index&tags=" +
                tags.replaceAll(" ","+") + "&limit=" + limit + "&pid=" + pageNum
                +"&api_key=" + apiKey + "&user_id=" + userID;
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
               while ((input = br.readLine()) != null){
                    if (input.contains("<post ")) {
                        fetched.add(new BooruItem(getFileURL(input),getSampleURL(input),getThumbnailURL(input),getTags(input),getPostURL(input)));
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
        Pattern file_url = Pattern.compile("file_url=\\\"(.*?)\\\"");
        Matcher matcher = file_url.matcher(input);
        while(matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    private String getSampleURL(String input){
        Pattern file_url = Pattern.compile("sample_url=\\\"(.*?)\\\"");
        Matcher matcher = file_url.matcher(input);
        while(matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    private String getThumbnailURL(String input){
        Pattern file_url = Pattern.compile("preview_url=\\\"(.*?)\\\"");
        Matcher matcher = file_url.matcher(input);
        while(matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    private String getTags(String input){
        Pattern file_url = Pattern.compile("tags=\\\"(.*?)\\\"");
        Matcher matcher = file_url.matcher(input);
        while(matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    private String getPostURL(String input){
        Pattern file_url = Pattern.compile(" id=\\\"(.*?)\\\"");
        Matcher matcher = file_url.matcher(input);
        while(matcher.find()) {
            System.out.println("id = "+matcher.group(1));
            return "https://gelbooru.com/index.php?page=post&s=view&id=" + matcher.group(1);
        }
        return null;
    }

}