package com.vam.groceryapp.Model;

import java.util.HashMap;

public class User {

    private String name,number,password;
    private HashMap<Integer,String> cart_hm= new HashMap<Integer,String>();

    public User(String name, String number, String password, HashMap<Integer, String> cart_hm) {
        this.name = name;
        this.number = number;
        this.password = password;
        this.cart_hm = cart_hm;
    }



    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public HashMap<Integer, String> getCart_hm() {
        return cart_hm;
    }

    public void setCart_hm(HashMap<Integer, String> cart_hm) {
        this.cart_hm = cart_hm;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getPassword() {
        return password;
    }

    public  void clearHashMap(){
        this.cart_hm.clear();
    }
}
