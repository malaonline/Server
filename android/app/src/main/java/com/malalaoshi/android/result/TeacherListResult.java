package com.malalaoshi.android.result;

import com.malalaoshi.android.entity.Tag;
import com.malalaoshi.android.entity.Teacher;

import java.util.List;

/**
 * 教师列表返回结果
 */
public class TeacherListResult extends BaseResult<List<Teacher>> {
    protected List<Tag> tags;

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

}
