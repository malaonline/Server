/**
 * Created by liumengjun on 1/12/16.
 */

$(function(){
    var defaultErrMsg = '请求失败,请稍后重试,或联系管理员!';

    $("select[name=province]").change(function(e){
        var pro_id = $(this).val(), $city_sel = $("select[name=city]"), $dist_sel = $("select[name=district]");
        $.getJSON('/staff/teachers/action/', {'action': 'list-region', 'sid': pro_id}, function(json){
            if (json && json.list) {
                $city_sel.find('option:gt(0)').remove();
                $dist_sel.find('option:gt(0)').remove();
                for (var i in json.list) {
                    var reg = json.list[i];
                    $city_sel.append('<option value="'+reg.id+'">'+reg.name+'</option>');
                }
            }
        });
    });
    $("select[name=city]").change(function(e){
        var dist_id = $(this).val(), $dist_sel = $("select[name=district]");
        $.getJSON('/staff/teachers/action/', {'action': 'list-region', 'sid': dist_id}, function(json){
            if (json && json.list) {
                $dist_sel.find('option:gt(0)').remove();
                for (var i in json.list) {
                    var reg = json.list[i];
                    $dist_sel.append('<option value="'+reg.id+'">'+reg.name+'</option>');
                }
            }
        });
    });

    paginationInit();

    // 预览头像
    $('[data-action=show-avatar]').click(function(e){
        var url = $(this).attr('url');
        var $modal = $("#avatarModal");
        $modal.find('img').attr('src', url);
        $modal.modal();
    });
    // 查看学习中心
    $('[data-action=show-schools').click(function(e){
        var $schoolsModal = $("#schoolsModal");
        $.getJSON('/api/v1/schools/',function(data){
            if (data && data.results)  {
                var $schoolsTable = $schoolsModal.find("table");
                $schoolsTable.find('tr:gt(0)').remove();
                for (var i=0; i<data.results.length; i++) {
                    var school = data.results[i];
                    $schoolsTable.append('<tr schoolId="'+school.id+'">' +
                        '<td>'+school.name+'</td>' +
                        '<td><img src="'+school.thumbnail+'" height="66" style="max-height: 100%; max-width: 100%; vertical-align: middle"></td>' +
                        '<td>'+school.address+'</td>' +
                        '<td>TODO</td></tr>');
                }
                $schoolsModal.modal();
            }
        });
    });
    // 查看提分榜
    $('[data-action=show-highscores]').click(function(e){
        var $highscoresModal = $("#highscoresModal");
        var teacherId = $(this).closest('tr').attr('teacherId');
        $.getJSON('/staff/teachers/action/',{'action': 'list-highscore', 'tid': teacherId},function(data){
            if (data && data.list)  {
                var $highscoresTable = $highscoresModal.find("table");
                $highscoresTable.find('tr:gt(0)').remove();
                for (var i=0; i<data.list.length; i++) {
                    var highscore = data.list[i];
                    $highscoresTable.append('<tr>' +
                        '<td>'+highscore.name+'</td>' +
                        '<td>'+highscore.scores+'</td>' +
                        '<td>'+highscore.from+'</td>' +
                        '<td>'+highscore.to+'</td></tr>');
                }
                $highscoresModal.modal();
            }
        });
    });
});
