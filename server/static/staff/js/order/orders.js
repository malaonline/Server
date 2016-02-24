/**
 * Created by erdi on 2/24/16.
 */

$(function(){
    $('input.datetimeInput').datetimepicker({
        format: 'YYYY-MM-DD',
        locale: 'zh-cn'
    });

    $('form[name=query_form]').submit(function() {
        var dateFrom = $('input[name=order_date_from]').val(), dateTo = $('input[name=order_date_to]').val();
        if (dateFrom && dateTo && dateFrom > dateTo) {
            alert("请确保截止查询日期大于开始日期");
            return false;
        }
        var phone = $.trim($(this).find('input[name=phone]').val());
        if (phone && (!/^\d+$/.test(phone) || phone.length > 11)) {
            alert('手机号格式错误');
            return false;
        }
        return true;
    });
});
