/**
 * Created by liumengjun on 2016-06-07.
 */

var HOUR_S = 60*60;
var HOUR_MS = HOUR_S*1000;
var DAY_S = 24*HOUR_S;
var DAY_MS = 24*HOUR_MS;

function calculateCourseTimes(hours, weekly_time_slot_ids, weeklytimeslots, isFirstBuy, evaluateTime) {
    var wts_map = {};
    for (var d in weeklytimeslots) {
        var timeslots = weeklytimeslots[d];
        for (var i in timeslots) {
            var timeslot = timeslots[i];
            timeslot['day'] = d;
            wts_map[timeslot.id]= timeslot;
        }
    }
    chosen_weekly_time_slots = [];
    for (var i in weekly_time_slot_ids) {
        var tsid = weekly_time_slot_ids[i];
        var wts = wts_map[tsid];
        chosen_weekly_time_slots.push(wts);
    }
    var now = new Date(), weekday = now.getDay()==0?7:now.getDay();
    var today = new Date(now.getFullYear(), now.getMonth(), now.getDate()), todayTime = today.getTime();
    chosen_weekly_time_slots.sort(function(a,b){
        var dayA = weekday >= a.day?(7+a.day):a.day,
            dayB = weekday >= b.day?(7+b.day):b.day;
        var dd = dayA - dayB;
        return (dd != 0) ? dd : (a.start - b.start);
    });
    evaluateTime *= 1000;
    var courseStartTime = isFirstBuy?(todayTime+evaluateTime+DAY_MS):todayTime;
    var courseTimes = [];
    var count = hours/ 2, loop = 0;
    while(count>0) {
        for (var i = 0; i < chosen_weekly_time_slots.length && count>0; i++) {
            var wts = chosen_weekly_time_slots[i], day = wts.day, s = wts.start.split(':')[0];
            // 计算天数偏差
            var weekoffset = (weekday < day?0:1) + loop;
            var dayTime = todayTime + (day-weekday + weekoffset * 7)*DAY_MS;
            if (dayTime<courseStartTime) {
                continue;
            }
            var startTime = dayTime + s * HOUR_MS;
            if (wts.last_occupied_end && startTime<=wts.last_occupied_end) {
                continue;
            }
            startTime /= 1000;
            var endTime = dayTime + wts.end.split(':')[0] * HOUR_S;
            courseTimes.push([startTime, endTime]);
            count--;
        }
        loop++;
    }
    courseTimes.sort(function(a,b){
        return a[0] - b[0];
    });
    return courseTimes;
}

function renderCourseTime(courseTimes, $div) {
    var WEEKDAYS = '日一二三四五六';
    // 按天合并
    var courseInDays = [], prevDayVal = 0, prevDay;
    for (var i in courseTimes) {
        var obj = courseTimes[i], start = new Date(obj[0]*1000), se = {'s': obj[0], 'e': obj[1]};
        start.setHours(0,0,0,0);
        var curDayVal = start.getTime(), curDay;
        if (prevDayVal == 0) {
            curDay = {'day': start, 'slots': [se]};
            courseInDays.push(curDay);
        } else {
            if (prevDayVal != curDayVal) {
                curDay = {'day': start, 'slots': [se]};
                courseInDays.push(curDay);
            } else {
                curDay = prevDay;
                curDay.slots.push(se);
            }
        }
        prevDay = curDay;
        prevDayVal = curDayVal;
    }
    for (var i in courseInDays) {
        var obj = courseInDays[i], day = obj.day;
        var m = day.getMonth()+ 1, d = day.getDate();
        var hb = [];
        hb.push('<div class="ct-day">');
        hb.push('<span class="ct-date">'+m+"月"+d+"日</span><br>");
        hb.push("周"+WEEKDAYS[day.getDay()]);
        hb.push("</div>");
        hb.push('<i class="ct-icon"></i>');
        hb.push('<div class="v-line"></div>');
        hb.push('<div class="ct-slots">');
        var slots = obj.slots;
        for (var j in slots) {
            var se = slots[j], start = new Date(se.s*1000), end = new Date(se.e*1000);
            sh = start.getHours(), sm = start.getMinutes(), eh = end.getHours(), em = end.getMinutes();
            hb.push("<span>");
            hb.push((sh<10?('0'+sh):sh)+':'+(sm<10?('0'+sm):sm)+'-'+(eh<10?('0'+eh):eh)+':'+(em<10?('0'+em):em));
            hb.push("</span>");
        }
        hb.push("</div>");
        $div.append('<div class="ct-row">' + hb.join('') + '</div>');
    }
}
