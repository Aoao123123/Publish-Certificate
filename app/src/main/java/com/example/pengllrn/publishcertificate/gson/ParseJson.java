package com.example.pengllrn.publishcertificate.gson;

import com.example.pengllrn.publishcertificate.bean.TaggServer;
import com.google.gson.Gson;

/**
 * Created by pengllrn on 2019/1/7.
 */

public class ParseJson {

    public TaggServer Json2TaggServer(String json) {
        Gson gson = new Gson();
        TaggServer taggServer = gson.fromJson(json,TaggServer.class);
        return taggServer;
    }
}
