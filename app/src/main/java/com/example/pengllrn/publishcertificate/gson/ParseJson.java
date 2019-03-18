package com.example.pengllrn.publishcertificate.gson;

import com.example.pengllrn.publishcertificate.bean.Tagg;
import com.example.pengllrn.publishcertificate.bean.TaggServer;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pengllrn on 2019/1/7.
 */

public class ParseJson {

    public TaggServer Json2TaggServer(String json) {
        Gson gson = new Gson();
        TaggServer taggServer = gson.fromJson(json,TaggServer.class);
        return taggServer;
    }

    public List<Tagg> TaggPoint(String json) {
        List<Tagg> listTaggInStorage = new ArrayList<Tagg>();
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONArray taginstorageArray = jsonObject.getJSONArray("tags");
            for (int i = 0;i < taginstorageArray.length();i++) {
                JSONObject jObject = taginstorageArray.getJSONObject(i);
                String uid = jObject.getString("uid");
                String certificate = jObject.getString("certificate");
                String goods_name = jObject.getString("goods_name");
                String material = jObject.getString("material");
                String model = jObject.getString("model");
                String sn = jObject.getString("sn");
                String manufacturer = jObject.getString("manufacturer");
                String produce_date = jObject.getString("produce_date");
                listTaggInStorage.add(new Tagg(uid,certificate,goods_name,material,model,sn,manufacturer,produce_date));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return listTaggInStorage;
    }

    public List<Tagg> TaggCheckStorageTimePoint(String json) {
        List<Tagg> listTaggsHaveStorageTime = new ArrayList<Tagg>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray alltaginstorageArray = jsonObject.getJSONArray("tags");
            for (int i = 0;i <alltaginstorageArray.length();i++) {
                JSONObject jObject = alltaginstorageArray.getJSONObject(i);
                if (jObject.has("date_in_storage")) {
                    String uid = jObject.getString("uid");
                    String certificate = jObject.getString("certificate");
                    String goods_name = jObject.getString("goods_name");
                    String date_in_storage = jObject.getString("date_in_storage");
                    String date_out_storage;
                    if (jObject.has("date_out_storage")) {
                        date_out_storage = jObject.getString("date_out_storage");
                    } else {
                        date_out_storage = "";
                    }
                    listTaggsHaveStorageTime.add(new Tagg(uid,certificate,goods_name,date_in_storage,date_out_storage));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listTaggsHaveStorageTime;
    }
}
