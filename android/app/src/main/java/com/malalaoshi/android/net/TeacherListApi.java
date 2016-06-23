package com.malalaoshi.android.net;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.result.TeacherListResult;

/**
 * 老师列表页
 * Created by tianwei on 4/17/16.
 */
public class TeacherListApi extends BaseApi {

    @Override
    protected String getPath() {
        return "/api/v1/teachers";
    }

    @Override
    protected boolean addAuthHeader() {
        return false;
    }

    public TeacherListResult getTeacherList(Long gradeId, Long subjectId, long[] tags)
            throws Exception {
        String subUrl = "";
        boolean hasParam = false;
        if (gradeId!=null) {
            subUrl += "?grade=" + gradeId;
            hasParam = true;
        }
        if (subjectId!=null) {
            subUrl += hasParam ? "&subject=" : "?subject=";
            subUrl += subjectId;
            hasParam = true;
        }
        if (tags != null && tags.length > 0) {
            subUrl += hasParam ? "&tags=" : "?tags=";
            for (int i = 0; i < tags.length; ) {
                subUrl += tags[i];
                if (++i < tags.length) {
                    subUrl += "+";
                }
            }
        }
        return httpGet(getPath() + subUrl, TeacherListResult.class);
    }
}
