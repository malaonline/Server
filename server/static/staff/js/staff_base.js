/**
 * Created by liumengjun on 3/30/16.
 */
$(function() {
    $('input.datetimeInput').datetimepicker({
        format: 'YYYY-MM-DD HH:mm',
        locale: 'zh-cn',
        showClear: true,
        showClose: true
    });
    $('input.dateInput').datetimepicker({
        format: 'YYYY-MM-DD',
        locale: 'zh-cn',
        showClear: true,
        showClose: true
    });
});