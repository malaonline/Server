package com.malalaoshi.android.result;

import com.malalaoshi.android.entity.Course;
import com.malalaoshi.android.entity.Report;

import java.util.List;

/**
 * Created by kang on 16/5/20.
 */
public class ReportListResult  {
    List<Report> reports;

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }
}
