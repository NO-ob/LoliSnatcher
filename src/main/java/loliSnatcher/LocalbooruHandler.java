package loliSnatcher;


import java.sql.*;
import java.util.ArrayList;

public class LocalbooruHandler extends BooruHandler{
    public static Connection dbConn = null;
    public String baseURL;
    /**
     * The LocalbooruHandler will fetch images from the local database file
     */
    public LocalbooruHandler(int limit, String baseURL){
        dbConnect();
        this.limit = limit;
        this.baseURL = baseURL;
    }

    /** The dbConnect function will connect to the sqlite, if it doesn't exist it will be created and then populated with necessary tables
     *
     * @return Connection dbConn
     */
    public static Connection dbConnect(){
        try {
            String dbPath = "jdbc:sqlite:" + System.getProperty("user.home") + "/.loliSnatcher/database.db";
            dbConn = DriverManager.getConnection(dbPath);
            System.out.println("Connected");
            Statement query = dbConn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS BooruItem" +
                    "(BooruItemID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "ImagePath TEXT , " +
                    "SamplePath TEXT , " +
                    "ThumbPath TEXT , " +
                    "SourceURL TEXT ," +
                    "Width INTEGER ," +
                    "Height INTEGER)";
            query.executeUpdate(sql);
            sql = "CREATE TABLE IF NOT EXISTS ItemTag" +
                    "(ItemTagID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "BooruItemID INTEGER NOT NULL, " +
                    "TagID INTEGER NOT NULL)";
            query.executeUpdate(sql);
            sql = "CREATE TABLE IF NOT EXISTS Tag" +
                    "(TagID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "TagString TEXT NOT NULL)";
            query.executeUpdate(sql);
            query.close();
            return dbConn;
        } catch (SQLException e){
            System.out.println("\nLocalbooruHandler::dcConnect::SQLException");
            System.out.println(e.toString());
        }
        return null;
    }

    /**
     * Create an new row in the BooruItem table and return the id for that row
     * @param filePath
     * @return
     */
    public String createBooruItemRow(String filePath){
        try {
            Statement query = dbConn.createStatement();
            String sql = "INSERT INTO BooruItem('ImagePath') VALUES ('"+filePath+"')";
            query.executeUpdate(sql);
            sql = "SELECT BooruItemId FROM BooruItem WHERE ImagePath='" + filePath + "'";
            query.close();
            return query.executeQuery(sql).getString("BooruItemId");

        } catch(SQLException e){
            System.out.println("\nLocalbooruHandler::createBooruItemRow::SQLException");
            System.out.println(e.toString());
        }
        return null;
    }

    /**
     * Populate the database with data from a BooruItem
     * @param item
     */
    public void updateBooruItemRow(BooruItem item){
        try {
            Statement query = dbConn.createStatement();
            String sql = "UPDATE BooruItem SET SamplePath='" + item.getSampleURL() + "'," +
                            "SourceURL='" + item.getPostURL() + "'," +
                            "ThumbPath='" + item.getThumbnailURL() + "'," +
                            "Width=" + item.getWidth() + "," +
                            "Height=" + item.getHeight() +
                            " WHERE BooruItemID=" + item.getId();
            query.executeUpdate(sql);
            for (int i = 0; i < item.getTags().split(" ").length; i++){
                int tagID = -1;
                // Check if tag exists
                sql = "SELECT TagID FROM Tag WHERE TagString='" + item.getTags().split(" ")[i] + "'";
                ResultSet results = query.executeQuery(sql);
                while (results.next()){
                    tagID = results.getInt("TagID");
                }
                // If tag doesn't exist write it to the database
                if (tagID == -1){
                    sql = "INSERT INTO Tag('TagString') VALUES ('" + item.getTags().split(" ")[i] + "')";
                    query.executeUpdate(sql);
                    sql = "SELECT TagID FROM Tag WHERE TagString='" + item.getTags().split(" ")[i] + "'";
                    results = query.executeQuery(sql);
                    while (results.next()){
                        tagID = results.getInt("TagID");
                    }
                }
                // Create row in ItemTag to link tags to the BooruItem
                sql = "INSERT INTO ItemTag('BooruItemID','TagID') VALUES ('" + item.getId() + "','" + tagID + "')";
                query.executeUpdate(sql);
                query.close();
            }
        } catch(SQLException e){
            System.out.println("\nLocalbooruHandler::updateBooruItemRow::SQLException");
            System.out.println(e.toString());
        }
    }

    /**
     * This function will call getItems and also manage the fetched arraylist by clearing it when the searched tags change to make sure that
     * a new search doesn't contain items from a previous search
     * @param tags
     * @return
     */
    @Override
    public ArrayList Search(String tags){

        if(!prevTags.equals(tags)){
            System.out.println("Reset Search!");
            fetched = new ArrayList<BooruItem>();
            pageNum = 0;
        }
        prevTags = tags;
        try {

            return getItems(tags);

        } catch(Exception e){

        }
        return null;

    }
    /** This function queries the database by searching for a tag and returns a list of booruItems created from the results
     *
     * @return
     */
    protected ArrayList getItems(String tags){
        if(dbConn!=null){
            try {
                String sql = "SELECT BooruItemID FROM ItemTag INNER JOIN Tag ON Tag.TagID=ItemTag.TagID WHERE tag.TagString='"+ tags.split(" ")[0] + "'";
                Statement query = dbConn.createStatement();
                System.out.println(sql);
                // Query DB
                ResultSet results = query.executeQuery(sql);
                while (results.next()){
                    String id = results.getString("BooruItemID");
                    fetched.add(new BooruItem(getFileURL(id),getSampleURL(id),getThumbnailURL(id),getTags(id),getPostURL(id),getHeight(id),getWidth(id),Integer.parseInt(id)));
                }
                query.close();
                pageNum ++;
                return fetched;

            } catch (Exception e) {
                System.out.println("\nLocalbooruHandler::GetItems::IOERROR");
                System.out.println(e.toString());
            }

        }
        return null;
    }



    @Override
    protected String getFileURL(String input){
        String sql = "SELECT ImagePath FROM BooruItem WHERE BooruItemID='" + input + "'";
        try{
            Statement query = dbConn.createStatement();
            String result = query.executeQuery(sql).getString("ImagePath");
            query.close();
            return result;
        } catch (SQLException e){
            System.out.println("LocalbooruHandler::getFileURL::SQLException");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected String getSampleURL(String input){
        String sql = "SELECT SamplePath FROM BooruItem WHERE BooruItemID='" + input + "'";
        try{
            Statement query = dbConn.createStatement();
            String result = query.executeQuery(sql).getString("SamplePath");
            query.close();
            return result;
        } catch (SQLException e){
            System.out.println("LocalbooruHandler::getSampleURL::SQLException");
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected String getThumbnailURL(String input){
        String sql = "SELECT ThumbPath FROM BooruItem WHERE BooruItemID='" + input + "'";
        try{
            Statement query = dbConn.createStatement();
            String result = query.executeQuery(sql).getString("ThumbPath");
            query.close();
            return result;
        } catch (SQLException e){
            System.out.println("LocalbooruHandler::getThumbnailURL::SQLException");
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected String getTags(String input){
        String sql = "SELECT TagString FROM ItemTag INNER JOIN Tag ON Tag.TagID=ItemTag.TagID WHERE ItemTag.BooruItemID='" + input + "'";
        try{
            Statement query = dbConn.createStatement();
            String tags = "";
            ResultSet results = query.executeQuery(sql);
            while (results.next()){
                tags += results.getString("TagString") + " ";

            }
            query.close();
            return tags;
        } catch (SQLException e){
            System.out.println("LocalbooruHandler::getThumbnailURL::SQLException");
            e.printStackTrace();
        }
        return "no tags";
    }
    @Override
    protected String getPostURL(String input){
        String sql = "SELECT SourceURL FROM BooruItem WHERE BooruItemID='" + input + "'";
        try{
            Statement query = dbConn.createStatement();
            String result = query.executeQuery(sql).getString("SourceURL");
            query.close();
            return result;
        } catch (SQLException e){
            System.out.println("LocalbooruHandler::getPostURL::SQLException");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected int getHeight(String input){
        String sql = "SELECT Height FROM BooruItem WHERE BooruItemID='" + input + "'";
        try{
            Statement query = dbConn.createStatement();
            int result = query.executeQuery(sql).getInt("Height");
            return result;
        } catch (SQLException e){
            System.out.println("LocalbooruHandler::getHeight::SQLException");
            e.printStackTrace();
        }
        return 0;
    }
    @Override
    protected int getWidth(String input){
        String sql = "SELECT Width FROM BooruItem WHERE BooruItemID='" + input + "'";
        try{
            Statement query = dbConn.createStatement();
            int result = query.executeQuery(sql).getInt("Width");
            query.close();
            return result;
        } catch (SQLException e){
            System.out.println("LocalbooruHandler::getWidth::SQLException");
            e.printStackTrace();
        }
        return 0;
    }

}