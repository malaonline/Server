/**
 * Created by liumengjun on 2016-06-30.
 */
$(function(){
    var _map = weekly_timeslots_map;
    $weeklyTable = $("#weeklyTable");
    // render
    $weeklyTable.find('tbody > tr').each(function(){
        var $row = $(this);
        var timestr = $row.attr('start') + '_' + $row.attr('end');
        $row.find('td').each(function(d, ele){
            if (d == 0) {
                return;
            }
            var key = d + '_' + timestr, ts = _map[key];
            var $td = $(ele);
            if (ts) {
                $td.attr('tsid', ts.id);
                if (ts.ok) {
                    $td.removeClass('unavailable').addClass('available');
                } else {
                    $td.removeClass('available').addClass('unavailable');
                }
            } else {
                $td.removeClass('available').addClass('unavailable');
            }
            $td.removeClass("removed chosen");
        });
    });
    // action
    $('td.phase').click(function(){
        var $td = $(this);
        if (!$td.attr('tsid')) {
            alert('您所在地区该时间段未开放');
            return;
        }
        if ($td.hasClass("available")) {
            $td.toggleClass("removed");
            $td.removeClass("chosen");
        } else {
            $td.toggleClass("chosen");
            $td.removeClass("removed");
        }
    });
    $('#saveWeeklyConfig').click(function(){
        var newIds = [], removedIds = [], changed = false;
        $("td.phase").each(function(){
            var $td = $(this);
            if ($td.hasClass('removed')) {
                changed = true;
                removedIds.push($td.attr('tsid'));
                return;//continue
            }
            if ($td.hasClass('chosen')) {
                changed = true;
                newIds.push($td.attr('tsid'));
                return;//continue
            }
        });
        // console.log(phases);
        if (!changed) {
            alert('没有变化');
            return;
        }
        var defaultErrMsg = '请求失败,请稍后重试,或联系管理员!';
        var params = {'new_ids': newIds.join('+'), 'removed_ids': removedIds.join('+')};
        malaAjaxPost(location.pathname, params, function(result){
            if (result) {
                if (result.ok) {
                    alert("保存成功");
                    location.reload()
                } else {
                    alert(result.msg);
                }
                return;
            }
            alert(defaultErrMsg);
        }, 'json', function(jqXHR, errorType, errorDesc){
            var errMsg = errorDesc ? ('[' + errorDesc + '] ') : '';
            alert(errMsg + defaultErrMsg);
        });
    });
});
