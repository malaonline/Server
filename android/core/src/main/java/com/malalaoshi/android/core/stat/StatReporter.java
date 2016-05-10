package com.malalaoshi.android.core.stat;

import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.malalaoshi.android.core.MalaContext;

/**
 * Stat reporter
 * Created by tianwei on 1/2/16.
 */
public class StatReporter {


    public static void teacherListPage() {
        sendBrowseEvent("找老师页面");
    }

    public static void coursePage() {
        sendBrowseEvent("课表页面");
    }

    public static void myPage() {
        sendBrowseEvent("我的页面");
    }

    public static void filterGrade() {
        sendClickEvent("年级标签");
    }

    public static void filterSubject() {
        sendClickEvent("课表标签");
    }

    public static void ClickAllTag() {
        sendClickEvent("风格标签：不限");
    }

    public static void ClickFilterTag(String tag) {
        sendClickEvent("风格标签: " + tag);
    }

    public static void filterFinish() {
        sendClickEvent("筛选完成按钮");
    }

    public static void switchFilterGrade() {
        sendClickEvent("切换年级标签按钮");
    }

    public static void switchFilterSubject() {
        sendClickEvent("切换科目标签按钮");
    }

    public static void switchFilterFeature() {
        sendClickEvent("切换风格标签按钮");
    }

    public static void filterEmptyTeacherList() {
        sendBrowseEvent("筛选结果为空页面");
    }

    public static void ClickToday() {
        sendClickEvent("今日按钮");
    }

    //上课时间dialog
    public static void courseTimePage() {
        sendBrowseEvent("上课时间页面");
    }

    public static void commentPage(boolean hasComment) {
        sendBrowseEvent(hasComment ? "去评价页面" : "已评价页面");
    }

    public static void commentSubmit() {
        sendClickEvent("评论提交按钮");
    }

    public static void clickScholarship(String pageName) {
        sendClickEvent(pageName + "-我的奖学金");
    }
    public static void clickOrders(String pageName) {
        sendClickEvent(pageName + "-我的订单");
    }

    public static void aboutMalaTeacher() {
        sendClickEvent("关于麻辣老师页面");
    }

    public static void couponPage() {
        sendBrowseEvent("我的奖学金页面");
    }

    public static void userLogOut() {
        sendClickEvent("用户退出");
    }

    public static void specialCertPage() {
        sendBrowseEvent("特殊成就照片页面");
    }

    public static void moreSchool() {
        sendClickEvent("更多社区按钮");
    }

    public static void soonRoll() {
        sendClickEvent("马上报名");
    }

    public static void evaluatePage(String statName) {
        sendBrowseEvent(statName + "-测评建档服务页面");
    }

    public static void submitCourse(String statName) {
        sendClickEvent(statName, "确认按钮");
    }

    public static void pay() {
        sendClickEvent("支付按钮");
    }

    public static void fetchCode(String statName) {
        sendClickEvent(statName, "获取验证码按钮");
    }

    public static void verifyCode(String statName) {
        sendClickEvent(statName, "验证按钮");
    }

    public static void userProtocol(String statName) {
        sendClickEvent(statName, "协议按钮");
    }

    public enum EventName {
        //Following is just test event.
        APP_LAUNCH
    }

    private static Tracker tracker;

    public static void init() {
        StatManager.getInstance().init();
        GoogleAnalyticsTrackers.initialize(MalaContext.getContext());
        tracker = GoogleAnalyticsTrackers.getInstance().get(GoogleAnalyticsTrackers.Target.APP);
    }

    public static void onPause() {
        tracker.setScreenName(null);
    }

    private static void sendBrowseEvent(String name) {
        tracker.send(new HitBuilders.EventBuilder()
                .setAction(name)
                .setCategory("浏览")
                .build());
        Log.d("MalaStat", "浏览: " + name);
    }

    private static void sendClickEvent(String name) {
        tracker.send(new HitBuilders.EventBuilder()
                .setAction(name)
                .setCategory("点击")
                .build());
        Log.d("MalaStat", "点击: " + name);
    }

    private static void sendClickEvent(String page, String name) {
        tracker.send(new HitBuilders.EventBuilder()
                .setAction(name)
                .setCategory("点击")
                .build());
        Log.d("MalaStat", "点击: " + page + "-" + name);
    }

    public static void ClickTeacherFilter() {
        sendClickEvent("筛选按钮");
    }

    public static void onAppLaunch() {
        StatManager.getInstance().logEvent(EventName.APP_LAUNCH.name());
    }

    /**
     * 点击首页的城市名称
     */
    public static void ClickCityLocation() {
        sendClickEvent("城市选择按钮");
    }


    /**
     * 用户打开一个UI界面
     */
    public static void onResume(String page) {
        tracker.setScreenName(page);
        tracker.send(new HitBuilders.EventBuilder().build());
        sendBrowseEvent(page);
    }
}
