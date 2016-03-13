package com.malalaoshi.android.entity;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

/**
 * Create order base
 * Created by tianwei on 2/28/16.
 */
public class JsonBodyBase {
    public JSONObject toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return new JSONObject(mapper.writeValueAsString(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }
}
