/**
 * Created by liumengjun on 2016-09-20.
 */
$(function(){
    $('#submitBtn').click(function(){
        var params = $('#accountInfoEditForm').serialize();
        malaAjaxPost("/staff/school/account/info/", params, function(result){
            if (result) {
                if (result.ok) {
                    alert("保存成功");
                    location.reload();
                } else {
                    alert(result.msg);
                }
                return;
            }
            alert(DEFAULT_ERR_MSG);
        }, 'json', function(jqXHR, errorType, errorDesc){
            var errMsg = errorDesc ? ('[' + errorDesc + '] ') : '';
            alert(errMsg + DEFAULT_ERR_MSG);
        });
    });
});