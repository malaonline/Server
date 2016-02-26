package com.malalaoshi.android.entity;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Create course order entity
 * Created by tianwei on 2/27/16.
 */
public class CreateCourseOrderResultEntity extends CreateCourseOrderEntity {
    private String order_id;
    private String parent;
    private String id;
    private String total;
    private String price;
    private String status;

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static CreateCourseOrderResultEntity parse(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(obj.toString(), CreateCourseOrderResultEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
