package com.example.blackbox;

import java.io.Serializable;
import java.util.Comparator;

public class CardModel implements Serializable {

    private int img;
    private int id, order;
    private String acc, pass, title, salt, iv;

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getAcc() {
        return acc;
    }

    public void setAcc(String acc) {
        this.acc = acc;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static Comparator<CardModel> byOrder = new Comparator<CardModel>() {
        @Override
        public int compare(CardModel m1, CardModel m2) {
            //m1.getAcc().compareTo(m2.getAcc());
            // descending order
            return m1.getOrder() > m2.getOrder() ? 1 : m1.getOrder() < m2.getOrder()  ? -1 : 0;
        }
    };

}
