package com.no.loliSnatcher;

/**
 * A class to store data for a specific booru this is only currently used for the booru selector combo box but will be
 * useful later on for allowing the user to add more boorus
 */
public class Booru {
    private String name;
    private String faviconUrl;
    public Booru(String name, String imageURL){
        this.name = name;
        this.faviconUrl = imageURL;
    }

    public String getFaviconUrl() {
        return faviconUrl;
    }

    public String getName() {
        return name;
    }
}
