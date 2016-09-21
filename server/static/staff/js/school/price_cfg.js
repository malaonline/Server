/**
 * Created by liumengjun on 2016-07-14.
 */
$(function(){
    var _format_money = function(num) {
        return (parseInt(num*100)/100).toFixed(2);
    };
    var showInput = function($ele){
        var $btnEle = $ele.find('.btns');
        var $mod = $btnEle.find('.modify'), $cancel = $btnEle.find('.cancel'), $save = $btnEle.find('.save');
        var $in = $ele.find('.price-in'), $show = $ele.find('.price-show'), $unit = $ele.find('.unit-show');
        $in.val($show.text());
        $show.addClass('hide');
        $unit.addClass('hide');
        $in.removeClass('hide');
        $mod.addClass('disabled');
        $cancel.removeClass('hide');
        $save.removeClass('hide');
        $btnEle.removeClass('hide');
    };
    var hideInput = function($ele){
        var $btnEle = $ele.find('.btns');
        var $mod = $btnEle.find('.modify'), $cancel = $btnEle.find('.cancel'), $save = $btnEle.find('.save');
        var $in = $ele.find('.price-in'), $show = $ele.find('.price-show'), $unit = $ele.find('.unit-show');
        $in.addClass('hide');
        $show.removeClass('hide');
        $unit.removeClass('hide');
        $cancel.addClass('hide');
        $save.addClass('hide');
        $mod.removeClass('disabled');
        $btnEle.addClass('hide');
    };

    $('td.one, td.all').dblclick(function(){
        var $td = $(this);
        showInput($td);
        if ($td.hasClass('all')) {
            $td.find('.price-in').val('');
        }
        $td.find('input').focus();
    });

    var updateRow = function($row, newVal, all) {
        if (all) {
            $row.find('.price-show').text(_format_money(newVal));
        } else {
            var prices = [];
            $row.find('td.one .price-show').each(function(){
                prices.push(parseFloat($(this).text()));
            });
            var min = _.min(prices), max = _.max(prices);
             $row.find('td.all .price-show').text(min==max?_format_money(min):(_format_money(min)+' ~ '+_format_money(max)));
        }
    };

    //$('.modify').click(function(){
    //    var $row = $(this).closest('tr');
    //    showInput($row);
    //});
    $('.cancel').click(function(){
        hideInput($(this).closest('td'));
    });
    $('td.one input, td.all input').keydown(function(e){
        if (e.keyCode == 27) {
            hideInput($(this).closest('td'));
        }
    });
    $('.save').click(function(){
        var $td = $(this).closest('td'), $row = $td.closest('tr');
            $in = $td.find('.price-in'), $show = $td.find('.price-show');
        var oldVal = $show.text(), newVal = $.trim($in.val());
        var gradeId = $td.attr('gradeId'), subjectId = $('#targetSubject').val();
        if (newVal=='') {
            hideInput($td);
            return;
        }
        if (gradeId) {
            if (parseFloat(oldVal) == parseFloat(newVal)) {
                hideInput($td);
                return;
            }
        }
        var levelName = $.trim($row.find('td[field=name]').text());
        var gradeName = gradeId ? $('th[gradeId='+gradeId+']').text() : '所有年级';
        var decided = confirm('确定要修改"'+levelName+gradeName+'"的价格为"' + _format_money(newVal) + '元/小时"吗?');
        if (!decided) return;

        var params = {};
        params['pk'] = $td.attr('priceId');
        params['price'] = newVal;
        malaAjaxPost(location.pathname, params, function(result){
            if (result) {
                if (result.ok) {
                    alert("保存成功");
                    //location.reload()
                    $show.text(_format_money(newVal));
                    hideInput($td);
                    updateRow($row, newVal, !gradeId);
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
