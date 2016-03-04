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
});