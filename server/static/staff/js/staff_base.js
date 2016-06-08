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
    $(document).ready(function () {
        // template 页面默认隐藏侧边栏, 这里显示出来, 可以避免闪烁
        $('#staff_menu').show();
        $('.mui-heading').each(function () {
            if ($(this).next().has('a:visible').length == 0) {
                $(this).hide();
            }
        });
    });
});