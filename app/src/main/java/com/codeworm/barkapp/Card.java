package com.codeworm.barkapp;

/**
 * Created by Mariah on 29/03/2018.
 */

public class Card {
    private String imgURL;
    private String title;
    private String label;

    public Card(String imgURL, String title, String label) {
        this.imgURL = imgURL;
        this.title = title;
        this.label = label;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
