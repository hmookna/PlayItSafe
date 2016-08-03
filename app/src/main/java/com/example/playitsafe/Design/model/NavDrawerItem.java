package com.example.playitsafe.Design.model;

import android.widget.ImageView;

/**
 * Created by Mook on 12/03/2016.
 * This model class is POJO class that defines each row in navigation drawer menu.
 */
public class NavDrawerItem {
    private boolean showNotify;
    private String title;
    private String icon;


    public NavDrawerItem() {

    }

    public NavDrawerItem(boolean showNotify, String title,String icon) {
        this.showNotify = showNotify;
        this.title = title;
        this.icon = icon;
    }

    public boolean isShowNotify() {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    public String getTitle() {
        return title;
    }

    public String getIcon() { return icon; }

    public void setIcon(String icon) { this.icon = icon; }

    public void setTitle(String title) {
        this.title = title;
    }

}
