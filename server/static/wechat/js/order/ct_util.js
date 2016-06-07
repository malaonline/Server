/**
 * Created by liumengjun on 2016-06-07.
 */

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
