/**
 * Created by caoyawen on 16/2/1.
 */
$(
    function(){
        console.log("TW-5-1");

    }
);

function day_cell_hover(day_id, today_date, today_week_day){
    //console.log(day_id);
    $("#today-date").html(today_date);
    $("#today-week-day").html(today_week_day);
    var header_template = $("#time_slot_header_template").html();
    var header_content = _.template(header_template, {variable: 'datas'})({today_date:today_date,
        today_week_day:today_week_day});
    if (window.time_slot_data[day_id] == undefined){
    //  今天暂无课程
    //    console.log("no-class");
        var no_class_template = $("#today_has_no_details").html();
        var no_class_content = _.template(no_class_template)();
        var list_content = header_content+no_class_content;
        $("#today-time-slot-list").html(list_content);
    }else{
    //  有课程,需要跟着模版走
        var time_slot_list_template = $("#time_slot_list_template").html();
        var time_slot_list_content = _.template(time_slot_list_template, {variable: 'datas'})(window.time_slot_data[day_id]);
        var list_content = header_content+time_slot_list_content;
        $("#today-time-slot-list").html(list_content);

    }
}
