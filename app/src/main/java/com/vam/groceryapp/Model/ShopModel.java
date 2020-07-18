package com.vam.groceryapp.Model;

public class ShopModel {

    String shopname,shopphone,shopaddress,shopdist;

    public ShopModel(String shopname, String shopphone, String shopaddress, String shopdist) {
        this.shopname = shopname;
        this.shopphone = shopphone;
        this.shopaddress = shopaddress;
        this.shopdist = shopdist;
    }

    public String getShopname() {
        return shopname;
    }

    public String getShopphone() {
        return shopphone;
    }

    public String getShopaddress() {
        return shopaddress;
    }

    public String getShopdist() {
        return shopdist;
    }
}
