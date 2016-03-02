/**
 * Created by erdi on 2/24/16.
 */

$(function(){
    var defaultErrMsg = '请求失败,请稍后重试,或联系管理员!';

    $('input.datetimeInput').datetimepicker({
        format: 'YYYY-MM-DD',
        locale: 'zh-cn'
    });

    $('form[name=query_form]').submit(function() {
        var dateFrom = $('input[name=order_date_from]').val(), dateTo = $('input[name=order_date_to]').val();
        if (dateFrom && dateTo && dateFrom > dateTo) {
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
        $('#remainingHours').html("");
        $('#refundHours').html("");
        $('#refundAmount').html("");

        var orderId = $('#orderId').val();
        var params = {'action': 'preview-refund-info', 'order_id': orderId};
        $.get("/staff/orders/action/", params, function (result) {
            if (result) {
                if (result.ok) {
                    var order = result;
                    $('#remainingHours').html(order.remainingHours);
                    $('#refundHours').html(order.refundHours);
                    $('#refundAmount').html(order.refundAmount);
                    // 状态获取成功, 才显示 dialog
                    $('#refundModal').modal();
                } else {
                    alert(result.msg);
                }
                return;
            }
            alert(defaultErrMsg);
        }, 'json').fail(function () {
            alert(defaultErrMsg);
        });
    });

    // 申请退款 link click
    $("[data-action=show-refund-preview]").click(function(e){
        var orderId = $(this).attr("orderId")
        $('#orderId').val(orderId);
        $("[data-action=refresh-refund-preview]").click();
    });

    // 提交退费申请
    $("[data-action=submit-refund]").click(function(e){
        var orderId = $('#orderId').val();
        var params = {'action': 'request-refund', 'order_id': orderId};
        $.post("/staff/orders/action/", params, function (result) {
            if (result) {
                if (result.ok) {
                    var order = result;
                    alert("提交成功");
                    //var order = result;
                    //$('#remainingHours').html(order.remainingHours);
                    //$('#refundHours').html(order.refundHours);
                    //$('#refundAmount').html(order.refundAmount);
                    //// 状态获取成功, 才显示 dialog
                    //$('#refundModal').modal();
                } else {
                    alert(result.msg);
                }
                return;
            }
            alert(defaultErrMsg);
        }, 'json').fail(function () {
            alert(defaultErrMsg);
        });
    });
});
