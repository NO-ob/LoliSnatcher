package loliSnatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
/**
 * The DanbooruHandler will fetch images and information about them from boorus running on the danbooru engine
 */
public class DanbooruHandler extends BooruHandler {
    public String baseURL;

    public DanbooruHandler(int limit, String baseURL){
        this.limit = limit;
        this.baseURL = baseURL;
    }
    /** This will make a connection to the url
     *
     * @param tags
     * @return
     */
    @Override
    public ArrayList Search(String tags){
        // Create a url using tags and other information
        String https_url =  baseURL + "/posts.xml?tags=" +
                tags.replaceAll(" ","+") + "&limit=" + limit + "&page=" + pageNum;

        // Resets arraylist if new tags are searched
        if(!prevTags.equals(tags)){
            System.out.println("Reset Search!");
            fetched = new ArrayList<BooruItem>();
            pageNum = 0;
        }
        prevTags = tags;
        try {
            URL url = new URL(https_url);
            HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
            pageNum ++;
            return getItems(conn);

        } catch (MalformedURLException e) {
            System.out.println("\nDanbooruHandler::Search::MalformedURL");
            System.out.println("\n"+https_url+"\n");
            System.out.println(e.toString());

        } catch (IOException e) {
            System.out.println("\nDanbooruHandler::Search::IOERROR");
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
                String postURL=null;
                String fileURL=null;
                String sampleURL=null;
                String thumbnailURL=null;
                String tagList=null;
                int id=0;
                int height=0;
                int width=0;
                while ((input = br.readLine()) != null){
                    if (input.contains("<id")) {
                        id = getID(input);
                        postURL = getPostURL(input);

                    }else if (input.contains("<tag-string>")){
                        tagList = getTags(input);
                    }else if (input.contains("<large-file-url")){
                        sampleURL = getSampleURL(input);
                    }else if (input.contains("<file-url")){
                        fileURL = getFileURL(input);
                    }else if (input.contains("<preview-file-url")){
                        thumbnailURL = getThumbnailURL(input);
                    }else if (input.contains("<image-height")) {
                        height = getHeight(input);
                    }else if (input.contains("<image-width")) {
                        width = getWidth(input);

                        //null values if at the end of current post since Danbooru doesn't provide urls for all posts
                    }else if (input.contains("</post>")){
                        postURL=null;
                        tagList=null;
                        sampleURL=null;
                        fileURL=null;
                        thumbnailURL=null;
                        height=0;
                        width=0;
                        id=0;
                    }
                    // Create BooruItem if all fields have been found
                    if(!(postURL == null)&&!(fileURL == null)&&!(tagList == null)&&!(thumbnailURL == null)&&!(sampleURL == null) && !(height == 0) &&
                    !(width == 0)&& !(id == 0)) {
                        fetched.add(new BooruItem(fileURL, sampleURL, thumbnailURL, tagList, postURL, height,width,id));
                        postURL=null;
                        tagList=null;
                        sampleURL=null;
                        fileURL=null;
                        thumbnailURL=null;
                        height=0;
                        width=0;
                    }
                }
                br.close();

                return fetched;

            } catch (IOException e) {
                System.out.println("\nDanbooruHandler::getItems::IOERROR");
                System.out.println(e.toString());
            }

        }
        return null;
    }
    @Override
    protected String getFileURL(String input){
        Pattern file_url = Pattern.compile("<file-url>(.*?)</file-url>");
        Matcher matcher = file_url.matcher(input);
        while(matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    @Override
    protected String getSampleURL(String input){
        Pattern file_url = Pattern.compile("<large-file-url>(.*?)</large-file-url>");
        Matcher matcher = file_url.matcher(input);
        while(matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    @Override
    protected String getThumbnailURL(String input){
        Pattern file_url = Pattern.compile("<preview-file-url>(.*?)</preview-file-url>");
        Matcher matcher = file_url.matcher(input);
        while(matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    @Override
    protected String getTags(String input){
        Pattern file_url = Pattern.compile("<tag-string>(.*?)</tag-string>");
        Matcher matcher = file_url.matcher(input);
        while(matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    @Override
    protected String getPostURL(String input){
        Pattern file_url = Pattern.compile("<id type=\"integer\">(.*?)</id>");
        Matcher matcher = file_url.matcher(input);
        while(matcher.find()) {
            return baseURL + "/posts/" + matcher.group(1);
        }
        return null;
    }
    //@Override
    protected int getID(String input){
        Pattern file_url = Pattern.compile("<id type=\"integer\">(.*?)</id>");
        Matcher matcher = file_url.matcher(input);
        while(matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }
    @Override
    protected int getHeight(String input){
        Pattern file_url = Pattern.compile("<image-height type=\"integer\">(.*?)</image-height>");
        Matcher matcher = file_url.matcher(input);
        while(matcher.find()) {
            System.out.println("Height = "+matcher.group(1));
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }
    @Override
    protected int getWidth(String input){
        Pattern file_url = Pattern.compile("<image-width type=\"integer\">(.*?)</image-width>");
        Matcher matcher = file_url.matcher(input);
        while(matcher.find()) {
            System.out.println("width = "+matcher.group(1));
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }

}

