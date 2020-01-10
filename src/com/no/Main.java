package com.no;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {

        BooruHandler Gelbooru = new BooruHandler();
        Scanner input = new Scanner(System.in);
        System.out.println("Enter Tags");
        String tags = input.nextLine();
        ArrayList<BooruItem> fetched = Gelbooru.Search(tags);
        for (int i = 0; i < fetched.size(); i++) {
            Runtime.getRuntime().exec("feh " + fetched.get(i).fileURL);
            if (input.nextLine().equals("End")){
                break;
            }
        }
    }
}
