package com.example.pengllrn.publishcertificate.bean;

/**
 * Created by pengllrn on 2019/1/7.
 */

public class Tagg {

    private String uid;
    private String certificate;
    private String obflag;
    private String brand;
    private String group_number;
    private String goods_name;
    private String material;
    private String model;
    private String sn;
    private String manufacturer;
    private String produce_date;
    private String in_storage;
    private String date_out_storage = "";
    private String date_in_storage = "";

    public Tagg(String uid, String certificate, String goods_name, String material, String model, String sn, String manufacturer, String produce_date) {
        this.uid = uid;
        this.certificate = certificate;
        this.goods_name = goods_name;
        this.material = material;
        this.model = model;
        this.sn = sn;
        this.manufacturer = manufacturer;
        this.produce_date = produce_date;
    }

    public Tagg(String uid, String certificate,String goods_name, String date_in_storage, String date_out_storage) {
        this.uid = uid;
        this.certificate = certificate;
        this.goods_name = goods_name;
        this.date_in_storage = date_in_storage;
        this.date_out_storage = date_out_storage;
    }

    public String getUid() {
        return uid;
    }

    public String getCertificate() {
        return certificate;
    }

    public String getObflag() {
        return obflag;
    }

    public String getBrand() {
        return brand;
    }

    public String getGroup_number() {
        return group_number;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public String getMaterial() {
        return material;
    }

    public String getModel() {
        return model;
    }

    public String getSn() {
        return sn;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getProduce_date() {
        return produce_date;
    }

    public String getIn_storage() {
        return in_storage;
    }

    public String getDate_out_storage() {
        return date_out_storage;
    }

    public String getDate_in_storage() {
        return date_in_storage;
    }
}
