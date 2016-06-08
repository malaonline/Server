package com.malalaoshi.android.report;

import com.malalaoshi.android.report.entity.AbilityStructure;
import com.malalaoshi.android.report.entity.ExerciseErrorDistribution;
import com.malalaoshi.android.report.entity.ExerciseMonthTrend;
import com.malalaoshi.android.report.entity.KnowledgePointAccuracy;
import com.malalaoshi.android.report.entity.ScoreAnalyses;
import com.malalaoshi.android.report.entity.SubjectReport;

import java.util.ArrayList;
import java.util.List;

/**
 * 模板
 * Created by tianwei on 6/5/16.
 */
public final class MathReportTemplate {


    private static final String[] SUBJECT_CLASSES = new String[]{
            "实数", "函数初步", "多边形", "相似", "全等", "微积分", "几何变换", "圆", "其他"};
    private static final float[] SUBJECT_CLASSES_RATE = new float[]{8, 9, 12, 17, 21, 3, 19, 7, 4};

    private static final String[] KNOW_CLASSES = new String[]{
            "实数", "函数初步", "多边形", "圆", "全等", "相似", "几何变换"};

    /**
     * 数学的模板
     */
    public static SubjectReport getMathTemplate() {
        SubjectReport report = new SubjectReport();
        report.setSubject_id(-1);
        report.setGrade_id(5);

        report.setExercise_total_nums(1706);
        report.setTotal_nums(102);

        //错题分布
        List<ExerciseErrorDistribution> distributions = new ArrayList<>();
        for (int i = 0; i < SUBJECT_CLASSES.length; i++) {
            distributions.add(new ExerciseErrorDistribution(i, SUBJECT_CLASSES[i], SUBJECT_CLASSES_RATE[i]));
        }
        report.setError_rates(distributions);

        //题目数据分析
        List<ExerciseMonthTrend> trends = new ArrayList<>();
        trends.add(new ExerciseMonthTrend(2016, 4, 1, 106, 55));
        trends.add(new ExerciseMonthTrend(2016, 4, 16, 145, 25));
        trends.add(new ExerciseMonthTrend(2016, 5, 1, 85, 75));
        trends.add(new ExerciseMonthTrend(2016, 5, 16, 120, 95));
        report.setMonth_trend(trends);

        //知识点分析
        List<KnowledgePointAccuracy> accuracies = new ArrayList<>();
        accuracies.add(new KnowledgePointAccuracy(KNOW_CLASSES[0], 30, 11));
        accuracies.add(new KnowledgePointAccuracy(KNOW_CLASSES[1], 50, 21));
        accuracies.add(new KnowledgePointAccuracy(KNOW_CLASSES[2], 117, 109));
        accuracies.add(new KnowledgePointAccuracy(KNOW_CLASSES[3], 50, 21));
        accuracies.add(new KnowledgePointAccuracy(KNOW_CLASSES[4], 120, 114));
        accuracies.add(new KnowledgePointAccuracy(KNOW_CLASSES[5], 50, 21));
        accuracies.add(new KnowledgePointAccuracy(KNOW_CLASSES[6], 62, 54));
        report.setKnowledges_accuracy(accuracies);

        //能力结构
        List<AbilityStructure> abilityStructures = new ArrayList<>();
        abilityStructures.add(new AbilityStructure(70, "推理论证"));
        abilityStructures.add(new AbilityStructure(40, "数据分析"));
        abilityStructures.add(new AbilityStructure(26, "空间想象"));
        abilityStructures.add(new AbilityStructure(80, "运算求解"));
        abilityStructures.add(new AbilityStructure(50, "实际应用"));
        report.setAbilities(abilityStructures);

        //提分点
        List<ScoreAnalyses> scoreAnalyses = new ArrayList<>();
        scoreAnalyses.add(new ScoreAnalyses(60, 55, "实数"));
        scoreAnalyses.add(new ScoreAnalyses(90, 60, "函数初步"));
        scoreAnalyses.add(new ScoreAnalyses(80, 70, "多边形"));
        scoreAnalyses.add(new ScoreAnalyses(85, 60, "圆"));
        scoreAnalyses.add(new ScoreAnalyses(78, 65, "全等"));
        scoreAnalyses.add(new ScoreAnalyses(95, 80, "相似"));
        scoreAnalyses.add(new ScoreAnalyses(100, 80, "几何变换"));
        report.setScore_analyses(scoreAnalyses);
        return report;
    }

    public static String getStudent() {
        return "欧阳娜娜";
    }

    public static String getGrade() {
        return "初中二年级";
    }
}
