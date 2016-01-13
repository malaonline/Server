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
        $row.find('td[field=status]').text(statusDict[NOT_CHOSEN]);
        var actionHtml = '无';
         if (status == TO_CHOOSE) {
             actionHtml = ('<a class="" data-action="donot-choose">初选淘汰</a>'
                 + '| <a class="" data-action="invite-interview">邀约面试</a>');
         } else if(status == TO_INTERVIEW) {
             actionHtml = ('<a class="" data-action="set-interview-ok">面试通过</a>'
                 + '| <a class="" data-action="set-interview-fail">面试失败</a>');
         }
        $row.find('td[field=action]').html(actionHtml);
    }
    $('[data-action=donot-choose]').click(function(e){
        var $row = $(this).closest('tr');
        var teacherId = $row.attr('teacherId');
        var name = $row.find('td[field=name]').text();
        var decided = confirm('确定要修改"'+name+'"的状态为"'+statusDict[NOT_CHOSEN]+'"吗?');
        if (!decided) return;
        // do request server
        var params = {'action': "donot-choose", 'teacherId': teacherId};
        $.post( "/staff/teachers/action/", params, function( result ) {
            if (result) {
                if (result.ok) {
                    updateRowByStatus($row, NOT_CHOSEN);
                } else {
                    alert(result.msg);
                }
                return;
            }
            alert(defaultErrMsg);
        }, 'json').fail(function() {
            alert(defaultErrMsg);
        });
    });
    $('[data-action=invite-interview]').click(function(e){
        alert("你邀请了这个老师");
    });
    $('[data-action=set-interview-ok]').click(function(e){
        alert("欢迎这位老师的加入");
    });
    $('[data-action=set-interview-fail]').click(function(e){
        alert("哎哟,这个老师被pass掉了");
    });
});
