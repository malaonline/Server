/**
 * Created by liumengjun on 3/5/16.
 */
$(function(){
    //alert("course choosing");
    var chosen_grade_id = '';
    var chosen_price = 0;
    var chosen_school_id = '';

    var showAlertDialog = function(msg) {
        $("#alertDialogBody").html(msg);
        var $dialog = $('#alertDialog');
        $dialog.show();
        $dialog.one('click', function () {
            $dialog.hide();
        });
    };

    $('.grade-box > .grade').click(function(e){
        var ele = e.target, $ele = $(ele);
        var val = $ele.data('gradeid');
        $(".grade-box > .grade").each(function(){
          var $this = $(this), v = $this.data('gradeid');
          if (v===val) {
            $this.addClass('chosen');
          } else {
            $this.removeClass('chosen');
          }
        });
        chosen_grade_id = val;
        chosen_price = parseInt($(ele).find('input').val());
        e.stopPropagation();
    });

    $('#showMoreSchoolsBtn').click(function(e){
        $(this).hide();
        $('#moreSchoolsContainer').show();
    });

    var _makeWeeklyTimeSlotToMap = function(json) {
        var _map = {};
        for (var d in json) {
            var timeslots = json[d];
            for (var i in timeslots) {
                var timeslot = timeslots[i];
                var key = timeslot.start+'_'+timeslot.end+'_'+d;
                _map[key]= timeslot.available;
            }
        }
        return _map;
    };

    var renderWeeklyTableBySchool = function(school_id) {
        var $weeklyTable = $('#weeklyTable');
        var teacherId = $('#teacherId').val();
        var params = {'school_id': school_id};
        $.getJSON('/api/v1/teachers/'+teacherId+'/weeklytimeslots', params, function(json){
            //console.log(json);
            var _map = _makeWeeklyTimeSlotToMap(json);
            $weeklyTable.find('tbody > tr').each(function(){
                var $row = $(this);
                var timespan = $row.attr('start')+'_'+$row.attr('end');
                $row.find('td').each(function(i, ele){
                    if (i==0) {
                        return;
                    }
                    var key = timespan+'_'+i;
                    if (_map[key]) {
                        $(ele).addClass('available');
                    } else {
                        $(ele).removeClass('available').addClass('unavailable');
                    }
                });
            });
        });
    };

    $(".school > .icons-area input[type=radio]").click(function(e){
        if (!chosen_grade_id) {
            showAlertDialog('请先选择授课年级');
            return;
        }
        var ele = e.target, $ele = $(ele).closest(".icons-area");
        var val = $ele.find("input")[0].value;
        $(".school > .icons-area").each(function(){
          var $this = $(this), v = $this.find("input")[0].value;
          if (v===val) {
            $this.addClass('chosen');
          } else {
            $this.removeClass('chosen');
          }
        });
        chosen_school_id = val;
        renderWeeklyTableBySchool(val);
        e.stopPropagation();
    });

    var HOUR = 60*60*1000;
    var DAY = 24*HOUR;
    var updateCourseTimePreview = function() {
        var $chosenTimeSlot = $('#weeklyTable > tbody > tr > td.chosen');
        $('#courseHours').html($chosenTimeSlot.length?$chosenTimeSlot.length * 2:0);
        var now = new Date(), weekday = now.getDay()==0?7:now.getDay();
        var today = new Date(now.getFullYear(), now.getMonth(), now.getDate()), todayTime = today.getTime();
        var courseTimes = [];
        $chosenTimeSlot.each(function(i, ele){
            var $td = $(ele), $tr = $td.closest('tr');
            var day = parseInt($td.attr('day')),
                start = parseInt($tr.attr('start').split(':')[0]) + parseInt($tr.attr('start').split(':')[1])/60,
                end = parseInt($tr.attr('end').split(':')[0]) + parseInt($tr.attr('end').split(':')[1])/60;
            var weekoffset = weekday < day?0:1;
            var date = todayTime + (day-weekday + weekoffset * 7)*DAY;
            var s = start * HOUR;
            var e = end * HOUR;
            courseTimes.push({'date': date, 'start': s, 'end': e});
        });
        courseTimes.sort(function(a,b){
            var diff = a.date - b.date;
            if (diff == 0) {
                return a.start - b.start;
            }
            return diff;
        });
        $courseTimePreview = $("#courseTimePreview");
        $courseTimePreview.html('');
        for (var i in courseTimes) {
            var obj = courseTimes[i], start = new Date(obj.date+obj.start), end = new Date(obj.date+obj.end);
            var m = start.getMonth()+ 1, d = start.getDate(),
                sh = start.getHours(), sm = start.getMinutes(), eh = end.getHours(), em = end.getMinutes();
            $courseTimePreview.append('<div>'
                +start.getFullYear()+'/'+(m<10?('0'+m):m)+'/'+(d<10?('0'+d):d)
                +' ('+(sh<10?('0'+sh):sh)+':'+(sm<10?('0'+sm):sm)+'-'+(eh<10?('0'+eh):eh)+':'+(em<10?('0'+em):em)+')'
                +'</div>'
            );
        }
    };

    var _format_money = function(num, isYuan) {
        if (isYuan) {
            num = num * 100;
        }
        // 直接抹零, 但是toFixed 默认是四舍五入
        return (parseInt(num)/100).toFixed(2);
    };

    var updateCost = function() {
        var hours = parseInt($('#courseHours').text());
        var origTotalCost = hours * chosen_price; // 单位是分
        var discount = parseFloat($('#discountCost').text()); // 单位是元
        var realCost = origTotalCost - discount * 100;
        $("#origTotalCost").text(_format_money(origTotalCost));
        $("#realCost").text(_format_money(realCost));
    };

    $('#weeklyTable > tbody > tr > td').click(function(e) {
        if (!chosen_grade_id || !chosen_school_id) {
            showAlertDialog('请先选择授课年级和上课地点');
            return;
        }
        var $this = $(this);
        if ($this.hasClass('available')) {
            $this.toggleClass('chosen');
            updateCourseTimePreview();
            updateCost();
        }
    });

    $('#confirmBtn').click(function(e){
        var hours = parseInt($('#courseHours').text());
        if (hours <= 0) {
            showAlertDialog('请先选择上课时间');
            return;
        }
    });
});
