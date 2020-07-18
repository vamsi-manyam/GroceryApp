package com.vam.groceryapp.Model;

public class UserProduct {

    int pid;
    String pName,pDesc,qType,pPrice,pImage;

    public UserProduct(int pid, String pName, String pDesc, String qType, String pPrice, String pImage) {
        this.pid = pid;
        this.pName = pName;
        this.pDesc = pDesc;
        this.qType = qType;
        this.pPrice = pPrice;
        this.pImage = pImage;
    }

    public int getPid() {
        return pid;
    }

    public String getpName() {
        return pName;
    }

    public String getpDesc() {
        return pDesc;
    }

    public String getqType() {
        return qType;
    }

    public String getpPrice() {
        return pPrice;
    }

    public String getpImage() {
        return pImage;
    }
}
