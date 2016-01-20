/**
 * Created by liumengjun on 1/12/16.
 */

$(function(){
    var defaultErrMsg = '请求失败,请稍后重试,或联系管理员!';
    var TO_CHOOSE = 1,
        NOT_CHOSEN = 2,
        TO_INTERVIEW = 3,
        INTERVIEW_OK = 4,
        INTERVIEW_FAIL = 5,
        statusDict = {
            1: '待处理',
            2: '初选淘汰',
            3: '邀约面试',
            4: '面试通过',
            5: '面试失败'
        };
    var updateRowByStatus = function($row, status) {
        $row.find('td[field=status]').text(statusDict[status]);
        var actionHtml = '无';
         if (status == TO_CHOOSE) {
             actionHtml = ('<a class="" data-action="donot-choose">初选淘汰</a>'
                 + ' | <a class="" data-action="invite-interview">邀约面试</a>');
         } else if(status == TO_INTERVIEW) {
             actionHtml = ('<a class="" data-action="set-interview-ok">面试通过</a>'
                 + ' | <a class="" data-action="set-interview-fail">面试失败</a>');
         }
        $row.find('td[field=action]').html(actionHtml);
        bindAction($row);
    };
    var doUpdateStatusRequest = function(ele, action, newStatus) {
        var $row = $(ele).closest('tr');
        var teacherId = $row.attr('teacherId');
        var name = $.trim($row.find('td[field=name]').text());
        var decided = confirm('确定要修改"'+name+'"的状态为"'+statusDict[newStatus]+'"吗?');
        if (!decided) return;
        // do request server
        var params = {'action': action, 'teacherId': teacherId};
        $.post( "/staff/teachers/action/", params, function( result ) {
            if (result) {
                if (result.ok) {
                    updateRowByStatus($row, newStatus);
                } else {
                    alert(result.msg);
                }
                return;
            }
            alert(defaultErrMsg);
        }, 'json').fail(function() {
            alert(defaultErrMsg);
        });
    };
    var bindAction = function($ctx) {
        $('[data-action=donot-choose]', $ctx).click(function (e) {
            doUpdateStatusRequest(this, 'donot-choose', NOT_CHOSEN);
        });
        $('[data-action=invite-interview]', $ctx).click(function (e) {
            doUpdateStatusRequest(this, 'invite-interview', TO_INTERVIEW);
        });
        $('[data-action=set-interview-ok]', $ctx).click(function (e) {
            doUpdateStatusRequest(this, 'set-interview-ok', INTERVIEW_OK);
        });
        $('[data-action=set-interview-fail]', $ctx).click(function (e) {
            doUpdateStatusRequest(this, 'set-interview-fail', INTERVIEW_FAIL);
        });
    };
    bindAction();

    $('input[name=reg_date_from]').datetimepicker({
        format: 'YYYY-MM-DD',
        locale: 'zh-cn'
    });

    $('input[name=reg_date_to]').datetimepicker({
        format: 'YYYY-MM-DD',
        locale: 'zh-cn'
    });

    var updateLocationByParam = function(key, val) {
        var key_val = key+'='+val;
        var old_search = location.search;
        if (!old_search) {
            location.search = "?" + key_val;
            return;
        }
        var key_index = old_search.indexOf(key+"=");
        if (key_index<0) { // 原来没有page参数
            location.search = old_search + '&' + key_val;
            return;
        }
        // 有旧的key参数
        var end_index = old_search.indexOf('&', key_index + key.length + 1); // 加"{key}="的长度
        if (end_index<0) { // 无后续参数
            location.search = old_search.substring(0, key_index) + key_val;
        } else { // 有后续参数
            location.search = old_search.substring(0, end_index) + key_val + old_search.substring(end_index);
        }
    };

    $('.pagination a').click(function(e){
        var $this = $(this), $li = $this.closest('li');
        if ($li.hasClass('disabled') || $li.hasClass('active')) return;
        var page_to = $this.data('pageto');
        updateLocationByParam('page', page_to);
    });

    $("[data-action=showGradeSubjectModal]").click(function(e){
        var gsArr = [];
        $(this).find('div p').each(function(){
            var _gs = this.innerHTML;
            var a = _gs.split(',');
            gsArr.push({'grade':a[0], 'subject':a[1]});
        });
        var $gsTabel = $("#gradeSubjectModal table");
        $gsTabel.find('tr:gt(0)').remove();
        for (var i=0; i<gsArr.length; i++) {
            $gsTabel.append('<tr><td>'+gsArr[i].grade+'</td><td>'+gsArr[i].subject+'</td></tr>');
        }
        $("#gradeSubjectModal").modal();
    });
});
