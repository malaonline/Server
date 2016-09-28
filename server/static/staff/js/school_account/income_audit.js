/**
 * Created by liumengjun on 2016-09-27.
 */
$(function(){
    paginationInit();
    $('[data-action=switch_status]').click(function(e){
        var $btn = $(this), $tr = $btn.closest('tr'),
            status = $btn.attr('status'), recordId = $tr.attr('recordId');
        var params = {'rid': recordId};
        if (status != 'yes') {
            params['action'] = 'mark_yes';
        } else {
            params['action'] = 'mark_no';
        }
        malaAjaxPost(location.pathname, params, function(result){
            if (result) {
                if (result.ok) {
                    alert("操作成功");
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