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

public class GelbooruHandler extends BooruHandler{
/** apiKey and userID can be found here https://gelbooru.com/index.php?page=account&s=options
 * they may be required when using the api but aren't always
 * */
public String apiKey = "";
public String userID = "";
public String baseURL;
    /**
     * The GelbooruHandler will fetch images and information about them from boorus running on the danbooru engine
     */
    public GelbooruHandler(int limit, String baseURL){
        this.limit = limit;
        this.baseURL = baseURL;
    }
    @Override
    public ArrayList Search(String tags){
        String https_url = baseURL + "/index.php?page=dapi&s=post&q=index&tags=" +
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
            System.out.println("\nGelbooruHandler::Search::MalformedURL");
            System.out.println("\n"+https_url+"\n");
            System.out.println(e.toString());

        } catch (IOException e) {
            System.out.println("\nGelbooruHandler::Search::IOERROR");
            System.out.println(e.toString());
        }
        return null;

    }
    /** Reads the fetched page (XML) line by line and then gets the required data from it
     * it then uses that data to create an ArrayList of BooruItems
     *
     * @param conn
     * @return
     */
    @Override
    protected ArrayList getItems(HttpsURLConnection conn){
        if(conn!=null){

            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String input;
               while ((input = br.readLine()) != null){
                   // Creates a new booruItem if the current line is a post
                    if (input.contains("<post ")) {
                        fetched.add(new BooruItem(getFileURL(input),getSampleURL(input),getThumbnailURL(input),getTags(input),getPostURL(input),getHeight(input),getWidth(input),getID(input)));
                    }
                }
                br.close();
                return fetched;

            } catch (IOException e) {
                System.out.println("\nGelbooruHandler::GetItems::IOERROR");
                System.out.println(e.toString());
            }

        }
        return null;
    }
    @Override
    protected String getFileURL(String input){
        Pattern file_url = Pattern.compile("file_url=\\\"(.*?)\\\"");
        Matcher matcher = file_url.matcher(input);
        while(matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    @Override
    protected String getSampleURL(String input){
        Pattern file_url = Pattern.compile("sample_url=\\\"(.*?)\\\"");
        Matcher matcher = file_url.matcher(input);
        while(matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    @Override
    protected String getThumbnailURL(String input){
        Pattern file_url = Pattern.compile("preview_url=\\\"(.*?)\\\"");
        Matcher matcher = file_url.matcher(input);
        while(matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    @Override
    protected String getTags(String input){
        Pattern file_url = Pattern.compile("tags=\\\"(.*?)\\\"");
        Matcher matcher = file_url.matcher(input);
        while(matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    @Override
    protected String getPostURL(String input){
        Pattern file_url = Pattern.compile(" id=\\\"(.*?)\\\"");
        Matcher matcher = file_url.matcher(input);
        while(matcher.find()) {
            return baseURL + "/index.php?page=post&s=view&id=" + matcher.group(1);
        }
        return null;
    }
    //@Override
    protected int getID(String input){
        Pattern file_url = Pattern.compile(" id=\\\"(.*?)\\\"");
        Matcher matcher = file_url.matcher(input);
        while(matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }
    @Override
    protected int getHeight(String input){
        Pattern file_url = Pattern.compile(" height=\\\"(.*?)\\\"");
        Matcher matcher = file_url.matcher(input);
        while(matcher.find()) {
            System.out.println("Height = "+matcher.group(1));
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }
    @Override
    protected int getWidth(String input){
        Pattern file_url = Pattern.compile(" width=\\\"(.*?)\\\"");
        Matcher matcher = file_url.matcher(input);
        while(matcher.find()) {
            System.out.println("width = "+matcher.group(1));
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }

}