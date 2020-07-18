package com.vam.groceryapp.Model;

public class OwnerProduct {

    private int pid;
    private String PName,Price,QType,desc;
    private boolean contains;
    private String img;



    public OwnerProduct(int pid, String PName, String price, String QType, boolean contains, String img, String desc) {
        this.pid = pid;
        this.PName = PName;
        Price = price;
        this.QType = QType;
        this.contains = contains;
        this.img = img;
        this.desc=desc;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getPName() {
        return PName;
    }

    public void setPName(String PName) {
        this.PName = PName;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getQType() {
        return QType;
    }

    public void setQType(String QType) {
        this.QType = QType;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isContains() {
        return contains;
    }

    public void setContains(boolean contains) {
        this.contains = contains;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
