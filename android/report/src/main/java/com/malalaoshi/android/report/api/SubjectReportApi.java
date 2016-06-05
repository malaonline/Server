package com.malalaoshi.android.report.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.report.entity.SubjectReport;

/**
 * Define: http://b.malalaoshi.com/projects/MALA/repos/mala/browse/server/app/api/api.yaml
 * Created by tianwei on 6/4/16.
 */
public class SubjectReportApi extends BaseApi {

    private static final String URL_REPORT = "/api/v1/study_report/";

    @Override
    protected String getPath() {
        return URL_REPORT;
    }

    public SubjectReport get(int subject) throws Exception {
        return httpGet(getPath() + subject, SubjectReport.class);
    }

}
