/**
 * Created by walter-wu on 16/2/18.
 */
var pagedefaultErrMsg = '请求失败,请稍后重试,或联系管理员!';
$(function() {
    $('input.datetimeInput').datetimepicker({
        format: 'YYYY-MM-DD',
        locale: 'zh-cn'
    });

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
    $("[data-action=suspend-class]").click(function(e){
        if(confirm("确定停课?")) {
            var timeSlotId = $(this).attr("tid")
            var params = {'action': 'suspend-class', 'tid': timeSlotId};
            $.post("/staff/students/schedule/action/", params, function (result) {
                if (result) {
                    if (result.ok) {
                        location.reload();
                    } else {
                        alert(result.msg);
                    }
                    return;
                }
                alert(pagedefaultErrMsg);
            }, 'json').fail(function () {
                alert(pagedefaultErrMsg);
            });
        }
    });

    // 调课按钮点击, 获取老师可用时间表
    $("[data-action=view-available]").click(function(e){
        var timeSlotId = $(this).attr("tid");

        $("[data-action=view-available]").hide();
        $("[data-action=suspend-class]").hide();
        $("[data-action=cancel-transfer][tid=" + timeSlotId + "]").show();

        var params = {'action': 'view-available', 'tid': timeSlotId};
        $.post("/staff/students/schedule/action/", params, function (result) {
            if (result) {
                if (result.ok) {
                    var saDict = result.sa_dict;
                    for (i in saDict) {
                        var weekday = "[weekday='" + saDict[i].weekday + "']";
                        var start = "[start='" + saDict[i].start + "']";
                        var end = "[end='" + saDict[i].end + "']";
                        var selector = weekday + start + end;
                        if (!saDict[i].available) {
                            $(selector).attr("available", false);
                            $(selector).css("background", "pink");
                            $(selector).css("display", "none");
                            $(selector).fadeIn();
                        } else {
                            $(selector).attr("available", true);
                            $(selector).css("opacity", "0");
                            $(selector).css("background", "green");
                        }
                    }
                    $("[available=true]").mouseenter(function (e) {
                        $(this).fadeTo(200, 1);
                    });
                    $("[available=true]").mouseleave(function (e) {
                        $(this).fadeTo(50, 0);
                    });
                    $("[available=true]").click(function (e) {
                        console.log($(this).attr("weekday"), $(this).attr("start"), $(this).attr("end"));
                    });
                } else {
                    alert(result.msg);
                }
                return;
            }
            alert(pagedefaultErrMsg);
        }, 'json').fail(function () {
            alert(pagedefaultErrMsg);
        });
    });

    // 取消调课按钮点击
    $("[data-action=cancel-transfer]").click(function (e) {
        //location.reload()
        $("[available]").css("background", "none")
        $("[available]").unbind();
        $("[available]").removeAttr("available")
        $(this).hide();
        $("[data-action=view-available]").fadeIn();
        $("[data-action=suspend-class]").fadeIn();
    });
});