/**
 * Created by liumengjun on 2016-07-14.
 */
$(function(){
    var showInput = function($row){
        var $mod = $row.find('.modify'), $cancel = $row.find('.cancel'), $save = $row.find('.save');
        var $in = $row.find('.price-in'), $show = $row.find('.price-show');
        $in.val($show.text());
        $show.addClass('hide');
        $in.removeClass('hide');
        $mod.addClass('disabled');
        $cancel.removeClass('hide');
        $save.removeClass('hide');
    };
    var hideInput = function($row){
        var $mod = $row.find('.modify'), $cancel = $row.find('.cancel'), $save = $row.find('.save');
        var $in = $row.find('.price-in'), $show = $row.find('.price-show');
        $in.addClass('hide');
        $show.removeClass('hide');
        $cancel.addClass('hide');
        $save.addClass('hide');
        $mod.removeClass('disabled');
    };
    var destName = {'price': '价格', 'commission_percentage': '佣金比例'};

    $('.modify').click(function(){
        var $row = $(this).closest('tr');
        showInput($row);
    });
    $('.cancel').click(function(){
        var $row = $(this).closest('tr');
        hideInput($row);
    });
    $('.save').click(function(){
        var $row = $(this).closest('tr'), $in = $row.find('.price-in'), $show = $row.find('.price-show');
        var oldVal = $show.text(), newVal = $in.val();
        if (parseFloat(oldVal) == parseFloat(newVal)) {
            hideInput($row);
            return;
        }
        var dest = $in.attr('dest'), name = $.trim($row.find('td[field=name]').text());
        var decided = confirm('确定要修改"' + name + '"的' + destName[dest] + '为"' + newVal + '"吗?');
        if (!decided) return;

        var levelId = $row.attr('levelId');
        var params = {};
        params['region'] = $('#targetRegion').val();
        params['level'] = levelId;
        params[dest] = newVal;
        malaAjaxPost(location.pathname, params, function(result){
            if (result) {
                if (result.ok) {
                    alert("保存成功");
                    //location.reload()
                    $show.text(newVal);
                    hideInput($row);
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
