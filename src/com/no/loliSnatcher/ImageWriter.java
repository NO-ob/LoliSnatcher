package com.no.loliSnatcher;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageWriter {
    private String savePath;
    private String fileName;
    private BooruItem item;
    private Image fullImage;

    /**
     * Writes and image to a file
     * @param imageFile
     * @param fullImage
     */
    public void writeImage(File imageFile, Image fullImage) {
        this.fullImage = fullImage;
        try {
            BufferedImage image = ImageIO.read(new URL(item.getFileURL()));
            ImageIO.write(image, item.getFileURL().substring(item.getFileURL().lastIndexOf(".")+1),imageFile);

        } catch (IOException e){
            System.out.println("ImageWindowController::saveImage");
            System.out.println("\n Failed to Write File \n" + item.getFileURL().substring(item.getFileURL().lastIndexOf("/")+1) + "\n");
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
        // $TAGS[n] $ID $HASH $EXT $SEARCH[n]
        this.item = item;
        String tagString = "";
        File file;
        // Replace the $Search[n] with n tags from the search tags
        if (fileName.contains("$SEARCH[")){
            int n = Integer.parseInt(fileName.substring(fileName.indexOf("$SEARCH[") +8,fileName.indexOf("]")));
            if(n < searchTags.split(" ").length) {
                for (int i = 0; i < n; i++) {
                    if (n == 1){
                        tagString += searchTags.split(" ")[i];
                    } else{
                        tagString += searchTags.split(" ")[i]+" ";
                    }
                }
            } else{
                for (int i = 0; i < searchTags.split(" ").length; i++) {
                    tagString += searchTags.split(" ")[i] +" ";
                }
            }
            fileName = fileName.split(".SEARCH(.*?)]")[0] + tagString + fileName.split(".SEARCH(.*?)]")[1];
            tagString = "";
        }
        // Replace the $TAGS[n] with n tags from the BooruItem
        if (fileName.contains("$TAGS[")){
            System.out.println(item.getTags());
            int n = Integer.parseInt(fileName.substring(fileName.indexOf("$TAGS[") +6,fileName.indexOf("]")));
            if(n <= item.getTags().split(" ").length) {
                for (int i = 0; i < n; i++) {
                    if (n == 1){
                        tagString += item.getTags().split(" ")[i];
                    } else{
                        tagString += item.getTags().split(" ")[i]+" ";
                    }

                }
            } else{
                for (int i = 0; i < item.getTags().split(" ").length; i++) {
                    tagString += item.getTags().split(" ")[i] +" ";
                }
            }
            System.out.println(tagString);
            fileName = fileName.split(".TAGS(.*?)]")[0] + tagString + fileName.split(".TAGS(.*?)]")[1];
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
