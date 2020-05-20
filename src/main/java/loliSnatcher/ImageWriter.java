package loliSnatcher;




import java.io.IOException;
import java.io.FileOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.SQLException;


public class ImageWriter {
    public BooruItem item;
    public String localID;
    /**
     * Writes and image to a file
     * @param imageFile
     */
    public void writeImage(File imageFile,Boolean DBEnabled) {
            write(item.getFileURL(),imageFile);
            if (DBEnabled){
                LocalbooruHandler localHandler = new LocalbooruHandler(0,"");
                // Create row in database and get the id of that row
                localID = localHandler.createBooruItemRow(imageFile.getAbsolutePath());
                //Create a sample file and write the sample to it
                String samplePath =  System.getProperty("user.home") + "/.loliSnatcher/media/" + localID + "/" + "sample" + item.getSampleURL().substring(item.getSampleURL().lastIndexOf("."));
                String thumbPath =  System.getProperty("user.home") + "/.loliSnatcher/media/" + localID + "/" + "thumbnail" + item.getThumbnailURL().substring(item.getThumbnailURL().lastIndexOf("."));
                File file = new File(System.getProperty("user.home") + "/.loliSnatcher/media/" + localID + "/");
                if (!file.exists()){
                    file.mkdirs();
                }
                file = new File(samplePath);
                write(item.getFileURL(),file);
                //Create a thumbnail file and write the thumbnail to it
                file = new File(thumbPath);

                write(item.getFileURL(),file);
                BooruItem newItem = new BooruItem("file:///" + imageFile.getAbsolutePath(),"file:///" + samplePath,"file:///" + thumbPath,item.getTags(),item.getPostURL(),item.getHeight(),item.getWidth(),Integer.parseInt(localID));
                System.out.println(newItem.toString());
                localHandler.updateBooruItemRow(newItem);
                try {
                    localHandler.dbConn.close();
                } catch (SQLException e){
                    System.out.println("ImageWriter::writeImage::SQLException");
                    e.printStackTrace();
                }
            }
    }

    public void write(String URL, File imageFile){
        try {
            InputStream initialStream = new URL(URL).openStream();
            OutputStream outStream = new FileOutputStream(imageFile);
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = initialStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
            initialStream.close();
            outStream.flush();
            outStream.close();
        } catch (IOException e){
            e.printStackTrace();
        }

    }
    /** Converts the filename string into a proper file Name then creates an empty file ready to be written to
     *
     * @param savePath
     * @param fileName
     * @param item
     * @param searchTags
     * @return
     */
    public File makeFile(String savePath, String fileName, BooruItem item,String searchTags){
        this.item = item;
        String tagString = "";
        File file;
        // Replace the $Search[n] with n tags from the search tags
        if (fileName.contains("$SEARCH[")){
            int n = Integer.parseInt(fileName.substring(fileName.indexOf("$SEARCH[") +8,fileName.indexOf("]",fileName.indexOf("$SEARCH[") +8)));
                for (int i = 0; i < searchTags.split(" ").length && i < n; i++) {
                    if (i+1 == n || i+1 == searchTags.split(" ").length){
                        tagString += searchTags.split(" ")[i];
                    } else {
                        tagString += searchTags.split(" ")[i] +" ";
                    }
                }
            fileName = fileName.split("\\$SEARCH\\[(\\d+)\\]",2)[0] + tagString + fileName.split("\\$SEARCH\\[(\\d+)\\]",2)[1];
            tagString = "";

        }
        // Replace the $TAGS[n] with n tags from the BooruItem
        if (fileName.contains("$TAGS[")) {
            int n = Integer.parseInt(fileName.substring(fileName.indexOf("$TAGS[") + 6, fileName.indexOf("]",fileName.indexOf("$TAGS[") + 6)));
                for (int i = 0; i < item.getTags().split(" ").length && i < n; i++) {
                    if (i + 1 == n || i + 1 == item.getTags().split(" ").length) {
                        tagString += item.getTags().split(" ")[i];
                    } else {
                        tagString += item.getTags().split(" ")[i] + " ";
                    }
                }
                fileName = fileName.split("\\$TAGS\\[(\\d+)\\]",2)[0]  + tagString + fileName.split("\\$TAGS\\[(\\d+)\\]",2)[1];
                tagString = "";

        }

        // Replace $ID with id of the BooruItem
        if (fileName.contains("$ID")){
            fileName = fileName.replace("$ID", Integer.toString(item.getId()));
        // Replace $Hash with the hash from the file_url
        }
        if (fileName.contains("$HASH")){
            fileName = fileName.replace("$HASH",item.getFileURL().substring(item.getFileURL().lastIndexOf("/")+1, item.getFileURL().lastIndexOf(".")));

        }
        // Replace $EXT with the extension from the file_url
        if (fileName.contains("$EXT")) {
            fileName = fileName.replace("$EXT", item.getFileURL().substring(item.getFileURL().lastIndexOf(".") + 1));
        }
        // Recurse if filename still contains variables
        if (fileName.contains("$SEARCH[") ||fileName.contains("$TAGS[")|| fileName.contains("$ID")||fileName.contains("$HASH")||fileName.contains("$EXT")){
            System.out.println(fileName);
            return makeFile(savePath,fileName,item,searchTags);
        } else {
            //Make directories if the string contains /
            if (fileName.contains("/")){
                file = new File(savePath + fileName.substring(0, fileName.lastIndexOf("/")));
            }else{
                file = new File(savePath);
            }
            if (!file.exists()){
                file.mkdirs();
            }
            System.out.println(fileName);
            return new File(savePath+fileName);
        }
    }
}
