package com.no.loliSnatcher;

public class Booru {
    private String name;
    private String imageUrl;
    public Booru(String name, String imageURL){
        this.name = name;
        this.imageUrl = imageURL;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }
}
