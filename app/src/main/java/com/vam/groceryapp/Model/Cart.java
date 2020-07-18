package com.vam.groceryapp.Model;

import java.util.HashMap;

class Cart {
    HashMap<Integer,String> local_hm=new HashMap<Integer,String>();
    String username,x;

    public Cart(String x, String username) {
//        this.local_hm = local_hm;
        this.x = x;
        this.username = username;
    }
}
