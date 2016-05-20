package com.malalaoshi.android.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.result.ReportListResult;
/**
 * Created by kang on 16/5/20.
 */
public class LearningReportApi extends BaseApi {


    private static final String URL_REPORT = "/api/v1/study_report";
    @Override
    protected String getPath() {
        return URL_REPORT;
    }

    public ReportListResult get() throws Exception {
        return httpGet(getPath(), ReportListResult.class);
    }
}