/**
 * Created by liumengjun on 2016-10-19.
 */
$(function(){
    var WEEKDAYS = '日一二三四五六日';
    // global variables
    var chosen_time_slots = [];  // [{start: moment, end moment}]
    var daily_time_slots = [];  // [{start: seconds, end seconds}]
    var cur_week_days = [];  // [moment]
    var now = moment($('#server_timestamp').val() * 1000),
        today = moment([now.year(), now.month(), now.date()]);
    // functions
    var init_daily_time_slots = function(){
        $('tr.phases-row').each(function(i, r){
            var $row = $(r), start_str = $row.data('start'), end_str = $row.data('end');
            var startArr = start_str.split(':'), endArr = end_str.split(':');
            // 转化为秒
            daily_time_slots.push({
                'start': parseInt(startArr[0]) * 60 * 60 + parseInt(startArr[1]) * 60,
                'end': parseInt(endArr[0]) * 60 * 60 + parseInt(endArr[1]) * 60
            });
        });
        //console.log(daily_time_slots);
    };
    var render_time_slots_style = function(){
        $('.phase').removeClass('chosen past');
        $('tr.phases-row').each(function(i, r){
            var $row = $(r), seq = $row.data('seq'), time_slot = daily_time_slots[seq - 1];
            $row.find('td.phase').each(function(j, t){
                var $td = $(t), day = $td.data('day'), date = cur_week_days[day - 1];
                var start = date.unix() + time_slot.start, end = date.unix() + time_slot.end;
                var chosen = _.find(chosen_time_slots, function(o){
                    return o.start.unix() == start && o.end.unix() == end;
                });
                if (chosen) {
                    $td.addClass('chosen');
                }
                if (now.isAfter(moment(start * 1000))) {
                    $td.addClass('past');
                }
            });
        });
    };
    var init_weekly_time_table = function(weekoffset){
        var curWeekday = today.isoWeekday();  // 1..7
        var monday = today.clone().add(1 - curWeekday + weekoffset * 7, 'days');  // 该周一
        var days = [monday];
        for (var i = 1; i < 7; i++) {
            days.push(monday.clone().add(i, 'days'));
        }
        $("#week_date_start").text(days[0].format('YYYY-MM-DD'));
        $("#week_date_end").text(days[6].format('YYYY-MM-DD'));
        $('tr.days-row>td').each(function(i, t){
            if (i == 0)
                return true;
            var today_str = now.isSame(days[i - 1], 'day') ? " 今天" : "";
            $(t).find('span').text(days[i - 1].format('MM-DD') + today_str);
        });
        cur_week_days = days;
        //console.log(cur_week_days);
        if (weekoffset == 0) {
            $('#returnToday').hide();
        } else {
            $('#returnToday').show();
        }
        render_time_slots_style();
    };
    var update_lessons_preview = function(){
        $('#lessons_count').text(chosen_time_slots.length);
        var lesson_template = $("#lesson_template").html();
        var htmlBuf = [];
        chosen_time_slots.sort(function(a, b){
            return a.start.unix() - b.start.unix();
        });
        for (var s in chosen_time_slots) {
            var slot = chosen_time_slots[s];
            htmlBuf.push(_.template(lesson_template)({
                'start': slot.start.format('HH:mm'),
                'end': slot.end.format('HH:mm'),
                'weekday': WEEKDAYS[slot.start.isoWeekday()],
                'short_date': slot.start.format('MM-DD')
            }));
        }
        $lessons_preview = $('#lessons_preview');
        $lessons_preview.html('');
        $lessons_preview.append(htmlBuf);
    };
    var check_submit_params = function(params){
        if (!params.course_no) {
            return "课程编号不能为空";
        }
        if (!params.name) {
            return "课程名称不能为空";
        }
        if (!params.course_times.length) {
            return "上课时间不能为空";
        }
        if (!params.period_desc) {
            return "时间段不能为空";
        }
        if (!params.grade_desc) {
            return "年级不能为空";
        }
        if (!params.subject) {
            return "科目不能为空";
        }
        if (!params.fee || !/^\d+$/.test(params.fee) || parseInt(params.fee) <= 0) {
            return "费用不能为空, 并且必须是大于0的整数";
        }
        if (!params.description) {
            return "课程介绍不能为空";
        }
        if (!params.lecturer) {
            return "讲师不能为空";
        }
        if (!params.class_rooms.length) {
            return "没有校区教室";
        }
        var chosen_as = [];
        for (var i in params.class_rooms) {
            var room = params.class_rooms[i];
            if (!room.assistant) {
                return "每个校区教室必须分配助教";
            }
            if (_.contains(chosen_as, room.assistant)) {
                return "助教选择不能重复"
            }
            chosen_as.push(room.assistant);
        }
        return 'ok';
    };
    // init
    if (is_show) {
        for(var i in timeslots) {
            var t = timeslots[i];
            chosen_time_slots.push({
                'start': moment(t.start * 1000),
                'end': moment(t.end * 1000)
            });
        }
        update_lessons_preview();
    }
    init_daily_time_slots();
    init_weekly_time_table(0);
    // events
    $('#preWeekBtn').click(function(e){
        e.preventDefault();
        var $weekly_time_table = $("#weekly_time_table"),
            weekoffset = $weekly_time_table.data('weekoffset');
        if (!weekoffset) weekoffset = 0;
        $weekly_time_table.data('weekoffset', --weekoffset);
        init_weekly_time_table(weekoffset);
        e.stopPropagation();
    });
    $('#nextWeekBtn').click(function(e){
        e.preventDefault();
        var $weekly_time_table = $("#weekly_time_table"),
            weekoffset = $weekly_time_table.data('weekoffset');
        if (!weekoffset) weekoffset = 0;
        $weekly_time_table.data('weekoffset', ++weekoffset);
        init_weekly_time_table(weekoffset);
        e.stopPropagation();
    });
    $('#curWeekBtn').click(function(e){
        e.preventDefault();
        var $weekly_time_table = $("#weekly_time_table");
        $weekly_time_table.data('weekoffset', 0);
        init_weekly_time_table(0);
        e.stopPropagation();
    });
    if (is_show) {
        $("#submitBtn").hide();
        return;
    }
    $("td.phase").click(function(e){
        e.preventDefault();
        var $td = $(this);
        if ($td.hasClass('past')) {
            return false;
        }
        var $tr = $td.closest('tr');
        // 计算start,end
        var day = $td.data('day'), seq = $tr.data('seq');
        var date = cur_week_days[day - 1], time_slot = daily_time_slots[seq - 1];
        var start = date.unix() + time_slot.start,
            end = date.unix() + time_slot.end;
        if ($td.hasClass('chosen')) {
            chosen_time_slots = _.reject(chosen_time_slots, function(o){
                return o.start.unix() == start && o.end.unix() == end;
            });
            $td.removeClass('chosen');
        } else {
            chosen_time_slots.push({
                'start': moment(start * 1000),
                'end': moment(end * 1000)
            });
            $td.addClass('chosen');
        }
        //console.log(chosen_time_slots);
        update_lessons_preview();
        e.stopPropagation();
    });
    $("#submitBtn").click(function(e){
        e.preventDefault();
        var params = {
            'course_no': $.trim($('#course_no').val()),
            'name': $.trim($('#name').val()),
            'period_desc': $.trim($('#period_desc').val()),
            'grade_desc': $.trim($('#grade_desc').val()),
            'subject': $.trim($('#subject').val()),
            'fee': $.trim($('#fee').val()),
            'description': $.trim($('#description').val()),
            'lecturer': $.trim($('#lecturer').val())
        };
        var class_rooms = [];
        $('.class-room').each(function(i, d){
            var $div = $(d), id = $div.data('crid');
            var assistant = $div.find('select[name=assistant]').val();
            class_rooms.push({'id': id, 'assistant': $.trim(assistant)});
        });
        params['class_rooms'] = class_rooms;
        var course_times = [];  // 转化为时间戳(秒)
        for (var s in chosen_time_slots) {
            var slot = chosen_time_slots[s];
            course_times.push({
                'start': slot.start.unix(),
                'end': slot.end.unix()
            });
        }
        params['course_times'] = course_times;
        //console.log(params);
        var msg = check_submit_params(params);
        if (msg !== 'ok') {
            alert(msg);
            return false;
        }
        var jsonstr = JSON.stringify(params);
        //console.log(jsonstr);
        malaAjaxPost(location.pathname, {'data': jsonstr}, function(result){
            if (result) {
                if (result.ok) {
                    alert("创建成功");
                    location.reload();
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
        e.stopPropagation();
    });
});
