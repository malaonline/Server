package com.malalaoshi.android.result;

import com.malalaoshi.android.entity.GTag;
import com.malalaoshi.android.entity.GTeacher;

import java.util.List;

/**
 * 教师列表返回结果
 */
public class TeacherListResult extends BaseResult<List<GTeacher>> {
    protected List<GTag> tags;

    public List<GTag> getTags() {
        return tags;
    }

    public void setTags(List<GTag> tags) {
        this.tags = tags;
    }

}
