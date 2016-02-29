/**
 * Created by liumengjun on 1/12/16.
 */

$(function(){
    var defaultErrMsg = '请求失败,请稍后重试,或联系管理员!';

    $('form[name=query_form]').on('submit', function(e){
        var phone = $.trim($(this).find('input[name=phone]').val());
        if (phone != 'None') {
            if (phone && (!/^\d+$/.test(phone) || phone.length > 11)) {
                alert('手机号格式错误');
                return false;
            }
        }
        var dateFrom = $('input[name=dateFrom]').val(), dateTo = $('input[name=dateTo]').val();
        if (dateFrom && dateTo && dateFrom>dateTo) {
            alert("请确保截止查询日期大于等于开始日期");
            return false;
        }
        return true;
    });

    $('input.datetimeInput').datetimepicker({
      format: 'YYYY-MM-DD',
      locale: 'zh-cn'
  });

});
