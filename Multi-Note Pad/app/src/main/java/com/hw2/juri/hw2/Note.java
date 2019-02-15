package com.hw2.juri.hw2;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Note {
    private String title;
    private String text;
    private String lastSave;
    private int pos;

    public Note(String title, String text) {
        this.title = title;
        this.text = text;
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd, hh:mm aa", Locale.ENGLISH);
        lastSave = format.format(date.getTime());
    }

    public Note(){

    }

    public String updateDate(){
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd, hh:mm aa", Locale.ENGLISH);
        lastSave = format.format(date.getTime());
        return lastSave;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getLastSave() {
        return lastSave;
    }

    public int getPos() { return pos; }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setLastSave(String lastSave) {
        this.lastSave = lastSave;
    }

    public void setPos(int pos) { this.pos = pos; }

    @Override
    public String toString() {
        return "";
    }
}
