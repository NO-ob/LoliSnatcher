package com.no.loliSnatcher;

import javafx.scene.image.Image;

/**
 * A class to store data for a specific booru this is only currently used for the booru selector combo box but will be
 * useful later on for allowing the user to add more boorus
 */
public class Booru {
    private String name;
    private Image favicon;
    public Booru(String name, String faviconURL){
        this.name = name;
        this.favicon = new Image(faviconURL);
    }

    public Image getFavicon() {
        return favicon;
    }

    public String getName() {
        return name;
    }
}
