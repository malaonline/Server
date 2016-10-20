/**
 * Created by erdi on 20/10/2016.
 */
$(function () {
    var defaultErrMsg = '请求失败,请稍后重试,或联系管理员!';
    $('#form').submit(function () {
        var form = $(this);
        showLoading();
        form.ajaxSubmit({
            dataType: 'json',
            success: function (result) {
                if (result) {
                    alert(result.msg);
                }
                else {
                    alert(defaultErrMsg);
                }
                hideLoading();
            },
            error: function (jqXHR, errorType, errorDesc) {
                var errMsg = errorDesc ? ('[' + errorDesc + '] ') : '';
                alert(errMsg + defaultErrMsg);
                hideLoading();
            }
        });
        return false;
    });
});
