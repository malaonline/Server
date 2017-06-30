/**
 * Created by erdi on 2/24/16.
 */

$(function(){
    var defaultErrMsg = '请求失败,请稍后重试,或联系管理员!';

    paginationInit();

    $('form[name=query_form]').submit(function() {
        var dateFrom = $('input[name=order_date_from]').val(), dateTo = $('input[name=order_date_to]').val();
        var refundDateFrom = $('input[name=refund_date_from]').val(), refundDateTo = $('input[name=refund_date_to]').val();
        if (dateFrom && dateTo && dateFrom > dateTo || refundDateFrom && refundDateTo && refundDateFrom > refundDateTo) {
            alert("请确保截止查询日期大于等于开始日期");
            return false;
        }
        var phone = $.trim($(this).find('input[name=phone]').val());
        if (phone && (!/^\d+$/.test(phone) || phone.length > 11)) {
            alert('手机号格式错误');
            return false;
        }
        return true;
    });

    // 刷新预览退费信息
    $("[data-action=refresh-refund-preview]").click(function(e){
        // 先清空一下
        $('#orderLessons').html("");
        $('#remainingHours').html("");
        $('#finishLessons').html("");
        $('#completedHours').html("");
        $('#remainingLessons').html("");
        $('#onTheLessonTime').html("");
        $('#discountAmount').html("");
        $('#remainingLessonsPreRefund').html("");
        $('#remainingHoursPreRefund').html("");
        $('#pricePerHour').html("");
        $('#calcDetailPreRefund').html("");

        var orderId = $('#orderId').val();
        var params = {'action': 'preview-refund-info', 'order_id': orderId};
        malaAjaxGet("/staff/orders/action/", params, function (result) {
            if (result) {
                if (result.ok) {
                    // test begin
                    // 一共10次课，20小时；结束5次课，10小时；未开始4次课，8小时；进行中一节课，开课56分钟；优惠券120元；单价：120元
                    // result.orderLessons = 10;
                    // result.remainingHours = 8;
                    // result.finishLessons = 5;
                    // result.completedHours = 10;
                    // result.remainingLessons = 4;
                    // result.onTheLessonTime = 56;
                    // result.pricePerHour = 12000;
                    // result.discountAmount = 12000;
                    // test end
                    $('#orderLessons').html(result.orderLessons);
                    // 暂时认为一节课 2 小时
                    $('#orderHours').html(result.orderLessons * 2);
                    $('#remainingHours').html(result.remainingHours);
                    $('#finishLessons').html(result.finishLessons);
                    $('#completedHours').html(result.completedHours);
                    $('#remainingLessons').html(result.remainingLessons);
                    $('#remainingLessonsPreRefund').html(result.remainingLessons);
                    $('#remainingHoursPreRefund').html(result.remainingHours);
                    $('#pricePerHour').html(result.pricePerHour/100);
                    $('#discountAmount').html(result.discountAmount/100);

                    if (result.onTheLessonTime != 0) {
                        $('#onTheLessonTime').html(result.onTheLessonTime);
                        $('#onTheLessonTip').show();
                    }
                    else {
                        $('#onTheLessonTip').hide();
                    }

                    // 这里显示预计退款费用
                    var calcDetailStr = "";
                    calcDetailStr += result.pricePerHour/100;
                    calcDetailStr += " x ";
                    calcDetailStr += result.remainingHours;
                    calcDetailStr += " - ";
                    calcDetailStr += result.discountAmount/100;
                    calcDetailStr += " = ";
                    calcDetailStr += Math.max(result.pricePerHour/100 * result.remainingHours - result.discountAmount/100, 0);
                    $('#calcDetailPreRefund').html(calcDetailStr);

                    // 确认退款，默认与预计的相同
                    $('#refundLessons').val(result.remainingLessons);
                    $('#refundLessons').triggerHandler("input");

                    // 如果原因未填写, 则默认显示上次提交的原因(如果存在的话)
                    if ($('#refundReason').val().trim() == "")
                        $('#refundReason').val(result.reason);
                    // 状态获取成功, 才显示 dialog
                    $('#refundModal').modal();
                } else {
                    alert(result.msg);
                }
                return;
            }
            alert(defaultErrMsg);
        }, 'json', function () {
            alert(defaultErrMsg);
        });
    });

    // 确认退费输入框改变
    $('#refundLessons').bind('input propertychange', function() {
        var refundHours = $(this).val() * 2;
        var pricePerHour = $('#pricePerHour').html();
        var discountAmount = $('#discountAmount').html();
        $('#refundHours').html(refundHours);
        // 这里显示预计退款费用
        var calcDetailStr = "";
        calcDetailStr += pricePerHour;
        calcDetailStr += " x ";
        calcDetailStr += refundHours;
        calcDetailStr += " - ";
        calcDetailStr += discountAmount;
        calcDetailStr += " = ";
        calcDetailStr += Math.max(pricePerHour * refundHours - discountAmount, 0);
        $('#calcDetail').html(calcDetailStr);
    });

    // 申请退款 link click
    $("[data-action=show-refund-preview]").click(function(e){
        var orderId = $(this).attr("orderId")
        $('#orderId').val(orderId);
        $("[data-action=refresh-refund-preview]").click();
    });

    // 查看记录 link click
    $("[data-action=show-refund-record]").click(function(e){
        var orderId = $(this).attr("orderId")
        var params = {'action': 'get-refund-record', 'order_id': orderId};
        malaAjaxGet("/staff/orders/action/", params, function (result) {
            if (result) {
                if (result.ok) {
                    var order = result;
                    $('#remainingHoursRecord').html(order.remainingHoursRecord);
                    $('#refundHoursRecord').html(order.refundHoursRecord);
                    $('#refundAmountRecord').html(order.refundAmountRecord);
                    $('#refundReasonRecord').val(order.reason);
                    // 状态获取成功, 才显示 dialog
                    $('#refundInfoModal').modal();
                } else {
                    alert(result.msg);
                }
                return;
            }
            alert(defaultErrMsg);
        }, 'json', function () {
            alert(defaultErrMsg);
        });
    });

    // 提交退费申请
    $("[data-action=submit-refund]").click(function(e){
        var orderId = $('#orderId').val();
        var reason = $('#refundReason').val();
        var params = {'action': 'request-refund', 'order_id': orderId, 'reason': reason};
        malaAjaxPost("/staff/orders/action/", params, function (result) {
            if (result) {
                if (result.ok) {
                    alert("退费成功")
                    location.reload();
                } else {
                    alert(result.msg);
                }
                return;
            }
            alert(defaultErrMsg);
        }, 'json', function () {
            alert(defaultErrMsg);
        });
    });

    // 确认退费 link click
    $("[data-action=refund-approve]").click(function(e){
        if(confirm("确定退费审核通过?")) {
            var orderId = $(this).attr("orderId")
            var params = {'action': 'refund-approve', 'order_id': orderId};
            malaAjaxPost("/staff/orders/action/", params, function (result) {
                if (result) {
                    if (result.ok) {
                        location.reload();
                    } else {
                        alert(result.msg);
                    }
                    return;
                }
                alert(defaultErrMsg);
            }, 'json', function () {
                alert(defaultErrMsg);
            });
        }
    });

    // 测评建档, 安排时间
    $("[data-action=show-schedule]").click(function(e) {
        var evaluationId = $(this).attr("evaluationId")
        $('#evaluationId').val(evaluationId);
        $('#scheduleModal').modal();
    });

    // 确定安排时间
    $("[data-action=schedule-evaluation]").click(function (e) {
        var evaluationId = $('#evaluationId').val();
        var scheduleDate = $("[name=schedule_date]").val();
        if (!scheduleDate) {
            alert("请先选择测评日期");
            return;
        }
        // 这里只传递 Weekly Time Slots 的索引下标, 后台根据索引取具体值
        var scheduleTime = $("[name=schedule_time]").val();
        var params = {
            'action': 'schedule-evaluation',
            'eid': evaluationId,
            'schedule_date': scheduleDate,
            'schedule_time': scheduleTime
        };
        malaAjaxPost("/staff/evaluations/action/", params, function (result) {
            if (result) {
                if (result.ok) {
                    location.reload();
                } else {
                    alert(result.msg);
                }
                return;
            }
            alert(defaultErrMsg);
        }, 'json', function () {
            alert(defaultErrMsg);
        });
    });

    // 测评建档, 测评完成
    $("[data-action=complete-evaluation]").click(function(e) {
        if(confirm("确定测评完成?")) {
            var evaluationId = $(this).attr("evaluationId");
            var params = {'action': 'complete-evaluation', 'eid': evaluationId};
            malaAjaxPost("/staff/evaluations/action/", params, function (result) {
                if (result) {
                    if (result.ok) {
                        location.reload();
                    } else {
                        alert(result.msg);
                    }
                    return;
                }
                alert(defaultErrMsg);
            }, 'json', function () {
                alert(defaultErrMsg);
            });
        }
    });
    
    // 导出按钮点击
    $("[data-action=export]").click(function(e) {
        if (window.location.search) {
            window.open(window.location.href + "&export=true");
        } else {
            window.open(window.location.href + "?export=true");
        }
    });
});
