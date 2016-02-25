/**
 * Created by liumengjun on 2/25/16.
 */

$(function() {
    var defaultErrMsg = '请求失败,请稍后重试,或联系管理员!';

    $('form[name=query_form]').on('submit', function (e) {
        var phone = $.trim($(this).find('input[name=phone]').val());
        if (phone && (!/^\d+$/.test(phone) || phone.length > 11)) {
            alert('手机号格式错误');
            return false;
        }
        return true;
    });

    paginationInit();
});
