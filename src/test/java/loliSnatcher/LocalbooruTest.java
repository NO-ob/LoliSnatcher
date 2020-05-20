package loliSnatcher;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class LocalbooruTest {
    @Test
    void dbConnTest(){
        LocalbooruHandler localbooru = new LocalbooruHandler(10,"");
        System.out.println("Trying to connect to db");
        assertNotNull(localbooru.dbConn);
        try {
            localbooru.dbConn.close();
        } catch (SQLException e){
            System.out.println("LocalbooruTest::dbConnTest::SQLException");
            e.printStackTrace();
        }
    }
    @Test
    void dbTablesExistTest() throws SQLException {
        LocalbooruHandler localbooru = new LocalbooruHandler(10,"");
        Connection dbConn = localbooru.dbConn;
        Statement query = dbConn.createStatement();
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='BooruItem'";
        System.out.println("Checking BooruItem");
        assertEquals("BooruItem",query.executeQuery(sql).getString("name"));
        sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='ItemTag'";
        System.out.println("Checking ItemTag");
        assertEquals("ItemTag",query.executeQuery(sql).getString("name"));
        sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='Tag'";
        System.out.println("Checking Tag");
        assertEquals("Tag",query.executeQuery(sql).getString("name"));
        query.close();
    }
    @Test
    void testNewRow() throws SQLException{
        LocalbooruHandler localbooru = new LocalbooruHandler(10,"");
        assertNotNull(localbooru.createBooruItemRow("/fake/path/uwu.png"));
    }
    @Test
    void testImagetoDB() {
        ImageWriter writer = new ImageWriter();
        BooruItem item = new BooruItem("https://img2.gelbooru.com/images/c8/77/c877ae202a17b181a8a23e23ad304ec0.jpg","https://img2.gelbooru.com/samples/c8/77/sample_c877ae202a17b181a8a23e23ad304ec0.jpg","https://img1.gelbooru.com/thumbnails/c8/77/thumbnail_c877ae202a17b181a8a23e23ad304ec0.jpg","1girl artist_name bow breasts cleavage closed_mouth","blank",300,300,1234);
        File file = new File("/home/kannalo/Documents/test/");
        if (!file.exists()){
            file.mkdirs();
        }
        file = new File("/home/kannalo/Documents/test/test.jpg");
        writer.item = item;
        writer.writeImage(file,true);
        LocalbooruHandler localbooru = new LocalbooruHandler(0,"");
        String sql = "SELECT BooruItem.BooruItemID, TagString FROM BooruItem INNER JOIN ItemTag ON ItemTag.BooruItemID=BooruItem.BooruItemID INNER JOIN Tag ON Tag.TagID=ItemTag.ItemTagID ";
        try {
            Statement query = localbooru.dbConn.createStatement();
            ResultSet results = query.executeQuery(sql);
            int id = -1;
            String tags = "";
            while (results.next()){
                id = results.getInt("BooruItemID");
                tags += results.getString("TagString") + " ";
            }
            // Check if id in the database is the same one used in ImageWriter.writeImage
            assertEquals(id, Integer.parseInt(writer.localID));
            int tagmatch = 0;
            // Increment tag match if a tag matches the ones in booruItem
            for (int i = 0; i < tags.split(" ").length; i ++) {
                for (int y = 0; y < item.getTags().split(" ").length; y++) {
                    if (tags.split(" ")[i].equals(item.getTags().split(" ")[y])) {
                        tagmatch++;
                    }
                }
            }
            // Check tagMatch is the same size as length of the tag list
            assertEquals(tagmatch, item.getTags().split(" ").length);
        } catch(SQLException e) {
            System.out.println("LocalbooruTest::testImagetoDB::SQLException");
            e.printStackTrace();
        }

    }
    @Test
    void testDBSearch() {
        ImageWriter writer = new ImageWriter();
        BooruHandler booruHandler = new GelbooruHandler(20,"https://gelbooru.com/");
        ArrayList<BooruItem>fetched = booruHandler.Search("rating:safe");
        for (int i = 0; i < fetched.size(); i++){
            writer.writeImage(writer.makeFile("/home/kannalo/Documents/test/","$HASH.$EXT",fetched.get(i),"rating:safe"),true);
        }
        LocalbooruHandler localbooru = new LocalbooruHandler(20,"");
        fetched = localbooru.Search("long_hair");
        for (int i = 0; i < fetched.size(); i++){
            assertTrue(fetched.get(i).getTags().contains("long_hair"));
        }
        try {
            localbooru.dbConn.close();
        } catch (SQLException e){
            System.out.println("LocalbooruTest::testDBSearch::SQLException");
            e.printStackTrace();
        }
    }
}
