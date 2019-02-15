package com.hw4.juri.hw4;

import java.io.Serializable;

public class Official implements Serializable{
    private String office;
    private String name;
    private String party;
    private String photo;
    private String address;
    private String phone;
    private String email;
    private String website;
    private String gpID;
    private String fbID;
    private String twID;
    private String ytbID;

    public Official(String office, String name, String party, String photo, String address,
                    String phone, String email, String website,
                    String gpID, String fbID, String twID, String ytbID) {
        this.office = office;
        this.name = name;
        this.party = party;
        this.photo = photo;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.website = website;
        this.gpID = gpID;
        this.fbID = fbID;
        this.twID = twID;
        this.ytbID = ytbID;
    }

    public Official(){

    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getGpID() {
        return gpID;
    }

    public void setGpID(String gpID) {
        this.gpID = gpID;
    }

    public String getFbID() {
        return fbID;
    }

    public void setFbID(String fbID) {
        this.fbID = fbID;
    }

    public String getTwID() {
        return twID;
    }

    public void setTwID(String twID) {
        this.twID = twID;
    }

    public String getYtbID() {
        return ytbID;
    }

    public void setYtbID(String ytbID) {
        this.ytbID = ytbID;
    }
}
