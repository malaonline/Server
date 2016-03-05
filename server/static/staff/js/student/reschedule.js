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
});