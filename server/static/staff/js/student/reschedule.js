/**
 * Created by walter-wu on 16/2/18.
 */
var pagedefaultErrMsg = '请求失败,请稍后重试,或联系管理员!';
$(function() {
    paginationInit();

    $('form[name=query_form]').submit(function() {
        var phone = $.trim($(this).find('input[name=phone]').val());
        // 为了测试方便, 暂时允许纯数字手机号码, 不检查位数
        if (phone && (!/^\d+$/.test(phone) || phone.length > 11)) {
            alert('手机号格式错误');
            return false;
        }
        //if (phone && (!/^\d+$/.test(phone) || phone.length !== 11)) {
        //    alert('手机号格式错误, 请输入11位的数字');
        //    return false;
        //}
        return true;
    });

    // 停课操作
    $("[data-action=suspend-course]").click(function(e){
        if(confirm("确定停课?")) {
            var timeSlotId = $(this).attr("tid")
            var params = {'action': 'suspend-course', 'tid': timeSlotId};
            malaAjaxPost("/staff/students/schedule/action/", params, function (result) {
                if (result) {
                    if (result.ok) {
                        location.reload();
                    } else {
                        alert(result.msg);
                    }
                    return;
                }
                alert(pagedefaultErrMsg);
            }, 'json', function () {
                alert(pagedefaultErrMsg);
            });
        }
    });

    var doTransfer = function(tid, newDate, newStart, newEnd) {
        var params = {
            'action': 'transfer-course',
            'tid': tid,
            'new_date': newDate,
            'new_start': newStart,
            'new_end': newEnd
        };
        malaAjaxPost("/staff/students/schedule/action/", params, function (result) {
            if (result) {
                if (result.ok) {
                    location.reload();
                } else {
                    alert(result.msg);
                    // 请求失败刷新页面
                    location.reload();
                }
                return;
            }
            alert(pagedefaultErrMsg);
            // 请求失败刷新页面
            location.reload();
        }, 'json', function () {
            alert(pagedefaultErrMsg);
            // 请求失败刷新页面
            location.reload();
        });
    }

    // 调课按钮点击, 获取老师可用时间表
    $("[data-action=view-available]").click(function(e){
        var timeSlotId = $(this).attr("tid");

        $("[data-action=view-available]").hide();
        $("[data-action=suspend-course]").hide();
        $("[data-action=cancel-transfer][tid=" + timeSlotId + "]").show();

        var contentElement = $("[data-action=course-content][tid=" + timeSlotId + "]");
        var courseElement = contentElement.closest("td");
        var courseContent = contentElement.html();
        var oldDate = courseElement.attr("date");
        var oldWeekday = courseElement.attr("weekday");
        var oldStart = courseElement.attr("start");
        var oldEnd = courseElement.attr("end");

        var params = {'action': 'view-available', 'tid': timeSlotId};
        malaAjaxPost("/staff/students/schedule/action/", params, function (result) {
            if (result) {
                if (result.ok) {
                    var saDict = result.sa_dict;
                    var nowDate = result.now_date;
                    var nowTime = result.now_time;
                    for (i in saDict) {
                        var weekday = "[weekday='" + saDict[i].weekday + "']";
                        var start = "[start='" + saDict[i].start + "']";
                        var end = "[end='" + saDict[i].end + "']";
                        var selector = weekday + start + end;
                        // 根据不同的时段, 显示对应状态
                            // 本老师时间不可用
                        if (!saDict[i].available ||
                            // 或者该时段, 该学生有课
                            $(selector).find("[data-action=course-content]").length > 0 ||
                            // 或者该时段已经开始
                            (nowDate == $(selector).attr("date") && nowTime >= $(selector).attr("start"))) {
                            // 设置为 不可用 状态
                            $(selector).attr("available", false);
                            $(selector).css("background", "#ff8080"); // %50 红色
                            $(selector).css("display", "none");
                            $(selector).attr("title", "这个时段不可用");
                            $(selector).css("cursor", "no-drop");
                            // 单独设置已经过去或已经开始的时段
                            if (nowDate == $(selector).attr("date") &&
                                nowTime >= $(selector).attr("start")) {
                                $(selector).css("background", "#FFC080"); // 浅橙色
                                $(selector).attr("title", "时段已开始或已结束");
                            }
                            $(selector).fadeIn();
                        } else {
                            // 可用时段
                            $(selector).attr("available", true);
                            $(selector).css("opacity", "0");
                            $(selector).css("background", "green"); // 绿色, 整体半透明
                            $(selector).css("color", "white");
                            $(selector).css("cursor", "pointer");
                        }
                    }
                    $("[available=true]").mouseenter(function (e) {
                        $(this).html(courseContent);
                        $(this).fadeTo(200, 0.5);
                    });
                    $("[available=true]").mouseleave(function (e) {
                        $(this).html("");
                        $(this).fadeTo(50, 0);
                    });
                    $("[available=true]").click(function (e) {
                        var newDate = $(this).attr("date");
                        var newWeekday = $(this).attr("weekday");
                        var newStart = $(this).attr("start");
                        var newEnd = $(this).attr("end");
                        var msg = "原上课时间:\n" + oldDate
                            + " 周" + oldWeekday + " "
                            + oldStart + " - " + oldEnd + "\n\n"
                            + "调课后上课时间:\n" + newDate
                            + " 周" + newWeekday + " "
                            + newStart + " - " + newEnd + "\n\n"
                            + "确定调课吗?";
                        if (confirm(msg)) {
                            $("[available]").unbind();
                            doTransfer(timeSlotId, newDate, newStart, newEnd);
                        }
                    });
                } else {
                    alert(result.msg);
                }
                return;
            }
            alert(pagedefaultErrMsg);
        }, 'json', function () {
            alert(pagedefaultErrMsg);
        });
    });

    // 取消调课按钮点击
    $("[data-action=cancel-transfer]").click(function (e) {
        // location.reload()
        // 恢复之前的状态
        $("[available]").css("opacity", "1");
        $("[available]").css("cursor", "auto");
        $("[available]").css("background", "none");
        $("[available]").removeAttr("title");
        $("[available]").unbind();
        $("[available]").removeAttr("available");
        $(this).hide();
        $("[data-action=view-available]").fadeIn();
        $("[data-action=suspend-course]").fadeIn();
    });

    $(document).keyup(function (e) {
        // ESC 按键取消调课
        if (e.keyCode == 27) {
            $("[data-action=cancel-transfer]").click();
        }
    });
});
