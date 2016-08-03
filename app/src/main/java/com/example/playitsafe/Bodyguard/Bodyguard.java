package com.example.playitsafe.Bodyguard;

/**
 * Created by aquaaquanam on 2/13/2016.
 */
import android.net.Uri;

public class Bodyguard {
    public Bodyguard(int bID, String bName, String bPhone, String bEmail, Uri bPhoto, String addFrom) {
        this.bID = bID;
        this.bName = bName;
        this.bPhone = bPhone;
        this.bEmail = bEmail;
        this.bPhoto = bPhoto;
        this.addFrom = addFrom;
    }

    public String getaddFrom() { return addFrom; }

    public void setaddFrom(String addFrom) { this.addFrom = addFrom; }

    public int getbID() {
        return bID;
    }

    public void setbID(int bID) {
        this.bID = bID;
    }

    public String getbName() {
        return bName;
    }

    public void setbName(String bName) {
        this.bName = bName;
    }

    public String getbPhone() {
        return bPhone;
    }

    public void setbPhone(String bPhone) {
        this.bPhone = bPhone;
    }

    public String getbEmail() {
        return bEmail;
    }

    public void setbEmail(String bEmail) {
        this.bEmail = bEmail;
    }

    public Uri getbPhoto() {
        return bPhoto;
    }

    public void setbPhoto(Uri bPhoto) {
        this.bPhoto = bPhoto;
    }

    private int bID;
    private String bName;
    private String bPhone;
    private String bEmail;
    private Uri bPhoto;
    private String addFrom;


}