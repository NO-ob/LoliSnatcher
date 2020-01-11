package com.no.loliSnatcher;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class BooruHandler{
private int pageNum = 0;
private int limit = 20;
    public ArrayList Search(String tags){
        String https_url = "https://gelbooru.com/index.php?page=dapi&s=post&q=index&tags=" +
                tags.replaceAll(" ","+") + "&limit=" + limit + "&pid = " + pageNum;
        URL url;
        try {

            url = new URL(https_url);
            HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
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
                ArrayList<BooruItem> fetched = new ArrayList<BooruItem>();

               while ((input = br.readLine()) != null){
                    if (input.contains("<post ")) {
                        fetched.add(new BooruItem(getFileURL(input),getSampleURL(input),getTags(input),getPostID(input)));
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
    private String getTags(String input){
        Pattern file_url = Pattern.compile("tags=\\\"(.*?)\\\"");
        Matcher matcher = file_url.matcher(input);
        while(matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    private String getPostID(String input){
        Pattern file_url = Pattern.compile("id=\\\"(.*?)\\\"");
        Matcher matcher = file_url.matcher(input);
        while(matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

}