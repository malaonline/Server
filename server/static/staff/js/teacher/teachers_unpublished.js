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
        var $this = $(this), schools = $this.data('schools');
        var fillTableAndShow = function(schools) {
            var $schoolsTable = $schoolsModal.find("table");
            $schoolsTable.find('tr:gt(0)').remove();
            for (var i=0; i<schools.length; i++) {
                var school = schools[i];
                $schoolsTable.append('<tr schoolId="'+school.id+'">' +
                    '<td>'+school.name+'</td>' +
                    '<td><img src="'+school.thumbnail+'" height="66" style="max-height: 100%; max-width: 100%; vertical-align: middle"></td>' +
                    '<td>'+school.address+'</td>' +
                    '<td>TODO</td></tr>');
            }
            $schoolsModal.modal();
        };
        if (schools) {
            fillTableAndShow(schools);
            return;
        }
        $.getJSON('/api/v1/schools/',function(data){
            if (data && data.results)  {
                $this.data('schools', data.results);
                fillTableAndShow(data.results);
            }
        });
    });
    // 查看周上课时间表
    $('[data-action=show-weeklySchedule]').click(function(e){
        var $weeklyScheduleModal = $("#weeklyScheduleModal");
        var $this = $(this), weeklySchedule = $this.data('weeklySchedule');
        var fillTableAndShow = function(weeklySchedule) {
            var heap = [];
            for (var k=0; k<weeklySchedule.length; k++) {
                var wsh = weeklySchedule[k];
                heap[""+wsh.weekday+wsh.start+wsh.end] = true;
            }
            var $weeklyScheduleTable = $weeklyScheduleModal.find("table");
            $weeklyScheduleTable.find('tr:gt(0)').remove();
            var metaTimeSlots = [{'start': '08:00:00', 'end': '10:00:00'},
                    {'start': '08:00:00', 'end': '10:00:00'},
                    {'start': '10:00:00', 'end': '12:00:00'},
                    {'start': '13:00:00', 'end': '15:00:00'},
                    {'start': '15:00:00', 'end': '17:00:00'},
                    {'start': '17:00:00', 'end': '19:00:00'},
                    {'start': '19:00:00', 'end': '21:00:00'},
                ];
            var buf = [];
            for (var i=0; i<metaTimeSlots.length; i++) {
                var timeSlot = metaTimeSlots[i];
                buf.push('<tr><td>'+timeSlot.start+'-'+timeSlot.end+'</td>');
                for (var d=1; d<=7; d++) {
                    if (heap[""+d+timeSlot.start+timeSlot.end]) {
                        buf.push('<td><span class="glyphicon glyphicon-ok"></span></td>');
                    } else {
                        buf.push('<td></td>');
                    }
                }
                buf.push('</tr>');
            }
            $weeklyScheduleTable.append(buf.join(''));
            $weeklyScheduleModal.modal();
        };
        if (weeklySchedule) {
            fillTableAndShow(weeklySchedule);
            return;
        }
        var teacherId = $(this).closest('tr').attr('teacherId');
        $.getJSON('/staff/teachers/action/',{'action': 'get-weekly-schedule', 'tid': teacherId},function(data){
            if (data && data.list)  {
                $this.data('weeklySchedule', data.list);
                fillTableAndShow(data.list);
            }
        });
    });
    // 查看提分榜
    $('[data-action=show-highscores]').click(function(e){
        var $highscoresModal = $("#highscoresModal");
        var $this = $(this), highscores = $this.data('highscores');
        var fillTableAndShow = function(highscores) {
            var $highscoresTable = $highscoresModal.find("table");
            $highscoresTable.find('tr:gt(0)').remove();
            for (var i=0; i<highscores.length; i++) {
                var highscore = highscores[i];
                $highscoresTable.append('<tr>' +
                    '<td>'+highscore.name+'</td>' +
                    '<td>'+highscore.scores+'</td>' +
                    '<td>'+highscore.from+'</td>' +
                    '<td>'+highscore.to+'</td></tr>');
            }
            $highscoresModal.modal();
        };
        if (highscores) {
            fillTableAndShow(highscores);
            return;
        }
        var teacherId = $(this).closest('tr').attr('teacherId');
        $.getJSON('/staff/teachers/action/',{'action': 'list-highscore', 'tid': teacherId},function(data){
            if (data && data.list)  {
                $this.data('highscores', data.list);
                fillTableAndShow(data.list);
            }
        });
    });
    // 查看特殊成果
    $('[data-action=show-achievements]').click(function(e){
        var $achievementsModal = $("#achievementsModal");
        var $this = $(this), achievements = $this.data('achievements');
        var fillTableAndShow = function(achievements) {
            var $achievementsTable = $achievementsModal.find("table");
            $achievementsTable.find('tr:gt(0)').remove();
            for (var i=0; i<achievements.length; i++) {
                var achievement = achievements[i];
                $achievementsTable.append('<tr>' +
                    '<td>'+achievement.title+'</td>' +
                    '<td><img src="'+achievement.img+'" height="66" style="max-height: 100%; max-width: 100%; vertical-align: middle"></td>' +
                    '</tr>');
            }
            $achievementsModal.modal();
        };
        if (achievements) {
            fillTableAndShow(achievements);
            return;
        }
        var teacherId = $(this).closest('tr').attr('teacherId');
        $.getJSON('/staff/teachers/action/',{'action': 'list-achievement', 'tid': teacherId},function(data){
            if (data && data.list)  {
                $this.data('achievements', data.list);
                fillTableAndShow(data.list);
            }
        });
    });
});
