package com.malalaoshi.android.report.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 科目的学习报告
 * Created by tianwei on 6/4/16.
 */
public class SubjectReport implements Serializable {
    private int subject_id;
    private int grade_id;
    private int total_nums;
    private int right_nums;
    private int exercise_total_nums;
    private int exercise_fin_nums;
    private List<ExerciseErrorDistribution> error_rates;
    private List<ExerciseMonthTrend> month_trend;
    private List<KnowledgePointAccuracy> knowledges_accuracy;
    private List<AbilityStructure> abilities;
    private List<ScoreAnalyses> score_analyses;

    public int getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(int subject_id) {
        this.subject_id = subject_id;
    }

    public int getGrade_id() {
        return grade_id;
    }

    public void setGrade_id(int grade_id) {
        this.grade_id = grade_id;
    }

    public int getTotal_nums() {
        return total_nums;
    }

    public void setTotal_nums(int total_nums) {
        this.total_nums = total_nums;
    }

    public int getRight_nums() {
        return right_nums;
    }

    public void setRight_nums(int right_nums) {
        this.right_nums = right_nums;
    }

    public int getExercise_total_nums() {
        return exercise_total_nums;
    }

    public void setExercise_total_nums(int exercise_total_nums) {
        this.exercise_total_nums = exercise_total_nums;
    }

    public int getExercise_fin_nums() {
        return exercise_fin_nums;
    }

    public void setExercise_fin_nums(int exercise_fin_nums) {
        this.exercise_fin_nums = exercise_fin_nums;
    }

    public List<ExerciseErrorDistribution> getError_rates() {
        return error_rates;
    }

    public void setError_rates(List<ExerciseErrorDistribution> error_rates) {
        this.error_rates = error_rates;
    }

    public List<ExerciseMonthTrend> getMonth_trend() {
        return month_trend;
    }

    public void setMonth_trend(List<ExerciseMonthTrend> month_trend) {
        this.month_trend = month_trend;
    }

    public List<KnowledgePointAccuracy> getKnowledges_accuracy() {
        return knowledges_accuracy;
    }

    public void setKnowledges_accuracy(List<KnowledgePointAccuracy> knowledges_accuracy) {
        this.knowledges_accuracy = knowledges_accuracy;
    }

    public List<AbilityStructure> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<AbilityStructure> abilities) {
        this.abilities = abilities;
    }

    public List<ScoreAnalyses> getScore_analyses() {
        return score_analyses;
    }

    public void setScore_analyses(List<ScoreAnalyses> score_analyses) {
        this.score_analyses = score_analyses;
    }
}
