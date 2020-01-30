package com.no.loliSnatcher;

import javafx.scene.image.Image;

import java.io.*;

/**
 * A class to store data for a specific booru this is only currently used for the booru selector combo box but will be
 * useful later on for allowing the user to add more boorus
 */
public class Booru {
    private String name;
    private Image favicon;
    private String type;
    private String baseURL;
    public Booru(String name, String faviconURL, String type,String baseURL){
        this.name = name;
        this.favicon = new Image(faviconURL);
        this.type = type;
        this.baseURL = baseURL;
    }
    public Booru(File booru){
        String input;
        try {
            BufferedReader br = new BufferedReader(new FileReader(booru));
            try {
                while ((input = br.readLine()) != null){
                    //Splits line and then switches on the option name
                    switch(input.split(" = ")[0]){
                        case("Booru Name"):
                            this.name = input.split(" = ")[1];
                            break;
                        case("Favicon URL"):
                            this.favicon = new Image(input.split(" = ")[1]);
                            System.out.println(input);
                            break;
                        case("Booru Type"):
                            this.type = input.split(" = ")[1];
                            break;
                        case("Base URL"):
                            this.baseURL = input.split(" = ")[1];
                            break;
                    }
                }
            } catch (IOException e) {
                System.out.println("SearchController::loadSettings \n Error reading settings \n");
                System.out.println(e.toString());
            }
        } catch (FileNotFoundException e){
            System.out.println("SearchController::loadSettings \n settings.conf not found \n");
            System.out.println(e.toString());
        }
    }

    public Image getFavicon() {
        return favicon;
    }

    public String getName() {
        return name;
    }
    public String getType(){return type;}
    public String getBaseURL() { return baseURL;}
}
