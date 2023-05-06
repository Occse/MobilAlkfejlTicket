package com.example.mobilalk;


import com.google.firebase.Timestamp;

public class TicketItem {
    private String bandname;
    private String bandimages;
    private String details;
    private String price;
    private Timestamp time;
    private int in_cart;

    public TicketItem() {
    }

    public TicketItem(String bandname, String bandimages, String details, String price, Timestamp time, int in_cart) {
        this.bandname = bandname;
        this.bandimages = bandimages;
        this.details = details;
        this.price = price;
        this.time = time;
        this.in_cart = in_cart;
    }

    public int getIn_cart() {
        return in_cart;
    }

    public void setIn_cart(int in_cart) {
        this.in_cart = in_cart;
    }

    public String getBandname() {
        return bandname;
    }

    public String getBandimages() {
        return bandimages;
    }

    public String getDetails() {
        return details;
    }

    public String getPrice() {
        return price;
    }

    public Timestamp getTime() {
        return time;
    }

}
