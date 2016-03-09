/**
 * Created by liumengjun on 1/12/16.
 */

$(function(){
    var defaultErrMsg = '请求失败,请稍后重试,或联系管理员!';

    $('form[name=query_form]').on('submit', function(e){
        var phone = $.trim($(this).find('input[name=phone]').val());
        if (phone && (!/^\d+$/.test(phone) || phone.length > 11)) {
            alert('手机号格式错误');
            return false;
        }
        return true;
    });

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
    $('[data-action=show-schools]').click(function(e){
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
        $.getJSON('/api/v1/schools',function(data){
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
            for (var k=0; k<weeklySchedule.list.length; k++) {
                var wsh = weeklySchedule.list[k];
                heap[""+wsh.weekday+wsh.start+wsh.end] = true;
            }
            var $weeklyScheduleTable = $weeklyScheduleModal.find("table");
            $weeklyScheduleTable.find('tr:gt(0)').remove();
            var metaTimeSlots = weeklySchedule.dailyTimeSlots;
            var buf = [];
            for (var i=0; i<metaTimeSlots.length; i++) {
                var timeSlot = metaTimeSlots[i];
                buf.push('<tr><td>'+timeSlot.start.substr(0, 5)+'-'+timeSlot.end.substr(0, 5)+'</td>');
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
                $this.data('weeklySchedule', data);
                fillTableAndShow(data);
            }
        });
    });
    // 查看上课安排时间表
    var showCourseSchedule = function(weekOffset) {
        var $courseScheduleModal = $("#courseScheduleModal");
        var teacherId = $courseScheduleModal.data('teacherid');
        var dataKey = 'courseSchedule'+teacherId+'w'+(weekOffset<0?'_'+weekOffset:weekOffset); // jQuery data key不区分大小写和'-'
        var courseSchedule = $courseScheduleModal.data(dataKey);
        var fillTableAndShow = function(courseSchedule) {
            var heap = [];
            for (var k=0; k<courseSchedule.list.length; k++) {
                var wsh = courseSchedule.list[k];
                heap[""+wsh.weekday+wsh.start+wsh.end] = true;
            }
            var courses_heap = [];
            for (var t=0; t<courseSchedule.courses.length; t++) {
                var c = courseSchedule.courses[t];
                courses_heap["" + c.weekday + c.start + c.end] = c;
            }
            var $courseScheduleTable = $courseScheduleModal.find("table");
            $courseScheduleTable.find('tr:eq(0) td:gt(0)').each(function(i){
                $(this).find('span').text('('+courseSchedule.dates[i]+')');
            });
            $courseScheduleTable.find('tr:gt(0)').remove();
            var metaTimeSlots = courseSchedule.dailyTimeSlots;
            var buf = [];
            for (var i=0; i<metaTimeSlots.length; i++) {
                var timeSlot = metaTimeSlots[i];
                buf.push('<tr><td>'+timeSlot.start.substr(0, 5)+'-'+timeSlot.end.substr(0, 5)+'</td>');
                for (var d=1; d<=7; d++) {
                    var _key = ""+d+timeSlot.start+timeSlot.end;
                    var c = courses_heap[_key];
                    buf.push('<td class="'+(c?'text-left ':'')+'">');
                    if (c) {
                        buf.push('科目: ' + c.subject+'<br>');
                        buf.push('学生: ' + c.student+'<br>');
                        buf.push('手机: ' + c.phone+'<br>');
                        buf.push('中心: ' + c.school+'<br>');
                    } else if (heap[_key]) {
                        buf.push('<span class="glyphicon glyphicon-ok"></span>');
                    } else {
                        buf.push('<span class="glyphicon glyphicon-remove"></span>');
                    }
                    buf.push('</td>');
                }
                buf.push('</tr>');
            }
            $courseScheduleTable.append(buf.join(''));
            $courseScheduleModal.modal();
        };
        if (courseSchedule) {
            fillTableAndShow(courseSchedule);
            return;
        }
        var params = {'action': 'get-course-schedule', 'tid': teacherId, 'weekOffset': weekOffset};
        $.getJSON('/staff/teachers/action/', params, function(data){
            if (data && data.list)  {
                $courseScheduleModal.data(dataKey, data);
                fillTableAndShow(data);
            }
        });
    };
    $('[data-action=pre-week-courses]').click(function(e){
        var $courseScheduleModal = $("#courseScheduleModal"), weekOffset = $courseScheduleModal.data('weekoffset');
        if (!weekOffset) weekOffset = 0;
        $courseScheduleModal.data('weekoffset', --weekOffset);
        showCourseSchedule(weekOffset);
    });
    $('[data-action=cur-week-courses]').click(function(e){
        var $courseScheduleModal = $("#courseScheduleModal");
        $courseScheduleModal.data('weekoffset', 0);
        showCourseSchedule(0);
    });
    $('[data-action=next-week-courses]').click(function(e){
        var $courseScheduleModal = $("#courseScheduleModal"), weekOffset = $courseScheduleModal.data('weekoffset');
        if (!weekOffset) weekOffset = 0;
        $courseScheduleModal.data('weekoffset', ++weekOffset);
        showCourseSchedule(weekOffset);
    });
    $('[data-action=show-courseSchedule]').click(function(e){
        var $courseScheduleModal = $("#courseScheduleModal");
        var teacherId = $(this).closest('tr').attr('teacherId');
        $courseScheduleModal.data('teacherid', teacherId);
        $courseScheduleModal.data('weekoffset', 0);
        showCourseSchedule(0);
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

    // 预览视频
    $("#videoModal").on('hide.bs.modal', function(e){
        $(this).find('video')[0].pause();
    });
    $('[data-action=show-video]').click(function(e){
        var url = $(this).attr('url');
        var $modal = $("#videoModal");
        $modal.find('video').attr('src', url);
        $modal.modal();
    });

    // 预览音频
    $("#audioModal").on('hide.bs.modal', function(e){
        $(this).find('audio')[0].pause();
    });
    $('[data-action=show-audio]').click(function(e){
        var url = $(this).attr('url');
        var $modal = $("#audioModal");
        $modal.find('audio').attr('src', url);
        $modal.modal();
    });

    var _publishChange = function(ele, flag) {
        var $row = $(ele).closest('tr');
        var teacherId = $row.attr('teacherId');
        var name = $.trim($row.find('td[field=name]').text());
        var decided = confirm('确定'+(flag?'上架':'下架')+'【'+name+'】?');
        if (!decided) return;
        // do request server
        var params = {'action': 'publish-teacher', 'tid': teacherId, 'flag': flag};
        $.post( "/staff/teachers/action/", params, function( result ) {
            if (result) {
                if (result.ok) {
                    $row.remove();
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

    $('[data-action=publish-teacher]').click(function(e){
        _publishChange(this, true);
    });

    $('[data-action=unpublish-teacher]').click(function(e){
        _publishChange(this, false);
    });
/*    $('[data-action=copy-link]').click(function(e){
        e.preventDefault();
        teacherId = e.currentTarget.dataset.teacherId;
        if (teacherId) {
            //TODO:找到不需要flash的跨平台访问clipboard的解决方案
        };
    });*/
});
