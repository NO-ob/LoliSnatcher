package loliSnatcher;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MoebooruHandler extends GelbooruHandler {
    int pageNum = 1;
    /**
     * The GelbooruHandler will fetch images and information about them from boorus running on the danbooru engine
     *
     * @param limit
     * @param baseURL
     */
    public MoebooruHandler(int limit, String baseURL) {
        super(limit, baseURL);
    }

    @Override
    public ArrayList Search(String tags){
        String https_url = baseURL + "/post.xml?tags=" +
                tags.replaceAll(" ","+") + "&limit=" + limit + "&page=" + pageNum;
        URL url;
        if(!prevTags.equals(tags)){
            System.out.println("Reset Search!");
            fetched = new ArrayList<BooruItem>();
            pageNum = 1;
        }
        prevTags = tags;
        try {

            url = new URL(https_url);
            HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
            pageNum ++;
            return getItems(conn);

        } catch (MalformedURLException e) {
            System.out.println("\nMoeboooruHandler::Search::MalformedURL");
            System.out.println("\n"+https_url+"\n");
            System.out.println(e.toString());

        } catch (IOException e) {
            System.out.println("\nMoebooruHandler::Search::IOERROR");
            System.out.println(e.toString());
        }
        return null;

    }
    @Override
    protected String getPostURL(String input){
        Pattern file_url = Pattern.compile(" id=\\\"(.*?)\\\"");
        Matcher matcher = file_url.matcher(input);
        while(matcher.find()) {
            System.out.println("id = "+matcher.group(1));
            return baseURL + "/post/show/" + matcher.group(1);
        }
        return null;
    }
}
