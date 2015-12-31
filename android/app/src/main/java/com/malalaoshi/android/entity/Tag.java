package com.malalaoshi.android.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zl on 15/12/14.
 */
public class Tag {
    private Long id;
    private String name;

    public Tag() {

    }

    public Tag(Long id, String name) {
        this.setId(id);
        this.setName(name);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public static List<Tag> tags;

    static {
        tags = new ArrayList<Tag>();

//        tags.add(new Tag(1L, "不错"));
//        tags.add(new Tag(2L, "幽默"));
//        tags.add(new Tag(3L, "有责任心"));
//        tags.add(new Tag(4L, "正能量"));
    }

    public static String generateTagViewString(Long[] tagsId, List<Tag> tagList) {
        if (tagsId == null || tagList == null || tagsId.length == 0 || tagList.size() == 0) {
            return null;
        }
        String str = "";
        for (int i = 0; i < tagsId.length; i++) {
            for (Tag tag : tagList) {
                if (tagsId[i].equals(tag.getId())) {
                    str += tag.getName();
                    if (i < tagsId.length - 1) {
                        str += " | ";
                    }
                }
            }
        }

        return str;
    }

    public static String generateTagViewString(String[] tags) {
        if (tags == null || tags.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder(tags.length * 8);
        for (int i = 0; i < tags.length; i++) {
            sb.append(tags[i]);
            if (i < tags.length - 1) {
                sb.append(" | ");
            }
        }

        return sb.toString();
    }

    @Deprecated
    public static Tag findTagById(Long tagId, List<Tag> allTag) {
        for (Tag tag : allTag) {
            if (tag.getId().equals(tagId)) {
                return tag;
            }
        }
        return null;
    }
}
