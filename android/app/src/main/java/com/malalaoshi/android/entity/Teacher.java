package com.malalaoshi.android.entity;

/**
 * Created by zl on 15/11/26.
 */
public class Teacher {
    private String id;
    private String name;
    private char degree;
    private User user;

    private Double minPrice;
    private Double maxPrice;

    private Long subject;
    private Long[] grades;
    private Long[] tags;

    private String[] tagsName;

    private String avatar;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public char getDegree() {
        return degree;
    }


    public User getUser() {
        return user;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDegree(char degree) {
        this.degree = degree;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getMinPrice(){
        return minPrice;
    }

    public void setMinPrice(Double minPrice){
        if(Double.isNaN(minPrice)){
            this.minPrice = null;
        }else{
            this.minPrice = minPrice;
        }
    }

    public String getAvatar(){
        return avatar;
    }

    public void setAvatar(String avatar){
        this.avatar = avatar;
    }

    public Long[] getTags(){
        return tags;
    }

    public void setTags(Long[] tags){
        this.tags = tags;
    }

    public Long[] getGrades(){
        return grades;
    }

    public void setGrades(Long[] grades){
        this.grades = grades;
    }

    public Double getMaxPrice(){
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice){
        if(Double.isNaN(maxPrice)){
            this.maxPrice = null;
        }else{
            this.maxPrice = maxPrice;
        }
    }

    public Long getSubject(){
        return subject;
    }

    public void setSubject(Long subject){
        this.subject = subject;
    }

    public String[] getTagsName(){
        return tagsName;
    }
    public String[] setTagsName(String[] tagsName){
        return this.tagsName = tagsName;
    }
}
