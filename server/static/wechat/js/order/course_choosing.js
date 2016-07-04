/**
 * Created by liumengjun on 3/5/16.
 */
$(function(){
    var teacherId = $('#teacherId').val();
    var chosen_grade_id = '';
    var chosen_price = 0;
    var chosen_school_id = '';
    var weekly_time_slot_ids = [];
    var chosen_coupon_id = '';
    var chosen_coupon_amount = 0;
    var chosen_coupon_min_cost = 0;
    var isFirstBuy = $("#isFirstBuy").val() == 'True';
    var evaluateTime = $("#evaluateTime").val();

    var weeklytimeslots = false;

    var $payArea = $('#payArea');
    var $alertDialog = $('#alertDialog');
    var $alertDialogBody = $("#alertDialogBody");
    var showAlertDialog = function(msg) {
        $alertDialogBody.html(msg);
        $payArea.hide();
        $alertDialog.show();
        $alertDialog.find('.weui_dialog').one('click', function() {
            $alertDialog.hide();
            $payArea.show();
        });
    };
    var $loadingToast = $('#loadingToast');
    var $loadingToastText = $("#loadingToastBody");
    var showLoading = function(msg) {
        $loadingToastText.html(msg?msg:"");
        $loadingToast.show();
    };
    var hideLoading = function() {
        $loadingToast.hide();
    };

    var _contains = function(list, v) {
        for (var i in list) {
            if (list[i]==v) return true;
        }
        return false;
    };

    var hideOtherSchools = function($school) {
        var $schools = $('.school');
        if ($schools.length==1) {
            return;
        }
        $schools.each(function(){
            var $this = $(this);
            if (this != $school[0]) {
                $this.hide();
            }
        });
        $schools.last().removeClass('last');
        $('#showMoreSchoolsBtn').show();
    };

    var sortSchools = function(list) {
        var far_map = {};
        for (var i in list) {
            var o = list[i];
            far_map[o.id] = o.far;
        }
        var $schools = $('.school');
        var new_list = [];
        var $chosenone = null;
        $schools.each(function(){
            var $this = $(this), scid = $this.attr('scid');
            if (scid==chosen_school_id) {
                $chosenone = $this;
            }
            var far = far_map[scid];
            if (far=='') {
                $this.find('.distance').html('');
            } else {
                var ifar = parseInt(far);
                if (ifar < 1000) {
                    $this.find('.distance').html(ifar+"m");
                } else {
                    $this.find('.distance').html(parseInt(ifar/100)/10+"km");
                }
            }
            new_list.push({'far': far, 'sc': $this});
        });
        new_list.sort(function(a,b) {
            if (a.far=='') return 1;
            if (b.far=='') return -1;
            return a.far - b.far;
        });
        if ($chosenone == null) {
            $chosenone = new_list[0].sc;
        }
        var scs = [];
        for (var i in new_list) {
            scs.push(new_list[i].sc[0]);
        }
        var $schoolsCon = $('#schoolsContainer');
        $schools.remove();
        $schoolsCon.prepend(scs);
        $chosenone.show();
        hideOtherSchools($chosenone);
    };

    wx.ready(function(res){
        wx.getLocation({
            type: 'wgs84', // 默认为wgs84的gps坐标，如果要返回直接给openLocation用的火星坐标，可传入'gcj02'
            success: function(res){
                var reqparams = {'action': 'schools_dist', 'lat': res.latitude, 'lng': res.longitude};
                $.post(location.href, reqparams, function(result){
                    if (result && result.ok) {
                        sortSchools(result.list);
                    }
                }, 'json')
            },
            fail: function(res){
            }
        });
    });

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
        sessionStorage.chosen_grade_id = chosen_grade_id;
        sessionStorage.chosen_price = chosen_price;
        updateCost();
        e.stopPropagation();
    });

    $('#showMoreSchoolsBtn').click(function(e){
        $(this).hide();
        var $schools = $('.school');
        $schools.show();
        $schools.last().addClass('last');
    });

    var $courseTimePreviewPanel = $('#courseTimePreviewPanel');
    var $courseTimePreview = $("#courseTimePreview");
    var $courseHours = $('#courseHours');
    var _updateCourseTimePreview = function(hours) {
        if (hours==0 || weekly_time_slot_ids.length==0 || !weeklytimeslots) {
            //$courseTimePreviewPanel.addClass('closed');
            //$courseTimePreview.hide();
            return $courseTimePreview.html('');
        }
        if ($courseTimePreviewPanel.hasClass('closed')) {
            return;
        }
        $courseTimePreview.html('');
        courseTimes = calcCourseTimes(hours, weekly_time_slot_ids, weeklytimeslots, isFirstBuy, evaluateTime);
        renderCourseTime(courseTimes, $courseTimePreview);
    };

    var updateCourseTimePreview = function() {
        var $chosenTimeSlot = $('#weeklyTable > tbody > tr > td.available.chosen');
        weekly_time_slot_ids.length=0;
        $chosenTimeSlot.each(function(i, ele){
            var $td = $(ele), tsid = $td.attr('tsid');
            weekly_time_slot_ids.push(tsid);
        });
        var hours = parseInt($courseHours.text());
        if (weekly_time_slot_ids.length==0) {
            hours = 0;
        } else {
            hours = Math.max(weekly_time_slot_ids.length * 2, hours);
        }
        $courseHours.html(hours);
        sessionStorage.weekly_time_slot_ids = weekly_time_slot_ids.join('+');
        sessionStorage.hours = hours;
        _updateCourseTimePreview(hours);
        updateCost();
    };

    var _makeWeeklyTimeSlotToMap = function(json) {
        var _map = {};
        for (var d in json) {
            var timeslots = json[d];
            for (var i in timeslots) {
                var timeslot = timeslots[i];
                var key = timeslot.start+'_'+timeslot.end+'_'+d;
                _map[key]= timeslot;
            }
        }
        return _map;
    };

    var $weeklyTable = $('#weeklyTable');
    var renderWeeklyTableBySchool = function(school_id, chosen_time_slot_ids) {
        $weeklyTable.find('tbody > tr').each(function(){
            $(this).find('td').each(function(i) {
                if (i == 0) return;
                $(this).removeClass('available').addClass('unavailable');
            });
        });
        showLoading();
        var params = {'school_id': school_id};
        $.ajax({'type':"GET", 'url': '/api/v1/teachers/'+teacherId+'/weeklytimeslots', 'data': params, 'success': function(json){
            weeklytimeslots = json;
            var _map = _makeWeeklyTimeSlotToMap(json);
            $weeklyTable.find('tbody > tr').each(function(r){
                var $row = $(this);
                var timespan = $row.attr('start')+'_'+$row.attr('end');
                $row.find('td').each(function(i, ele){
                    if (i==0) {
                        return;
                    }
                    var key = timespan+'_'+ i, ts = _map[key];
                    var $td = $(ele);
                    if (ts) {
                        $td.attr('tsid', ts.id);
                        if (ts.available) {
                            $td.removeClass('unavailable').addClass('available');
                            if (chosen_time_slot_ids && _contains(chosen_time_slot_ids, ts.id)) {
                                $td.addClass('chosen');
                            }
                        }
                        if (ts.reserved) {
                            console.log("reserved");
                            $td.addClass('reserved');
                        } else {
                            $td.removeClass('reserved')
                        }
                    } else {
                        $td.removeClass('available').addClass('unavailable').removeClass('reserved');
                    }
                });
            });
            hideLoading();
            updateCourseTimePreview();
        }, 'dataType': 'json', 'error': function() {
            showAlertDialog('获取上课时间安排失败, 请重试');
            hideLoading();
        }
        });
    };

    $(".school").click(function(e){
        if (!chosen_grade_id) {
            showAlertDialog('请先选择授课年级');
            return;
        }
        var ele = e.target, $school = $(ele).closest('.school');
        var val = $school.attr('scid');
        $(".school").each(function(){
          var $this = $(this), v = $this.attr('scid');
          if (v===val) {
            $this.addClass('chosen');
          } else {
            $this.removeClass('chosen');
          }
        });
        chosen_school_id = val;
        sessionStorage.chosen_school_id = chosen_school_id;
        renderWeeklyTableBySchool(val);
        hideOtherSchools($school);
        e.stopPropagation();
    });

    var _format_money = function(num, isYuan) {
        if (isYuan) {
            num = num * 100;
        }
        // 直接抹零, 但是toFixed 默认是四舍五入
        return (parseInt(num)/100).toFixed(2);
    };

    var $discountCost = $('#discountCost');
    var $origTotalCost = $("#origTotalCost");
    var $realCost = $("#realCost");
    var updateCost = function() {
        var hours = parseInt($courseHours.text());
        var origTotalCost = hours * chosen_price; // 单位是分
        // 检查奖学金
        if (hours==0) {
            $discountCost.html('0');
        } else {
            var $coupon = null;
            if (chosen_coupon_id) {
                var min_cost = parseInt(chosen_coupon_min_cost);
                if (hours * chosen_price < min_cost * 100) {
                    chosen_coupon_id = '';
                    sessionStorage.chosen_coupon_id = chosen_coupon_id;
                    $discountCost.html('0');
                }
            }
            if (chosen_coupon_id) { // get discount
                $discountCost.html(chosen_coupon_amount);
            } else {
                $discountCost.html('0');
            }
        }
        var discount = parseFloat($discountCost.text()); // 单位是元
        var realCost = origTotalCost - discount * 100;
        if (origTotalCost>0) {
            realCost = Math.max(realCost, 1); // 暂时不支持免费订单, 最少1分
        }
        $origTotalCost.text(_format_money(origTotalCost));
        $realCost.text(_format_money(realCost));
    };

    var valid_choose = function(keys) {
        var msg_pre = '请先选择', need_list=[];
        if (_contains(keys, 'grade') && !chosen_grade_id) {
            need_list.push('授课年级');
        }
        if (_contains(keys, 'school') && !chosen_school_id) {
            need_list.push('上课地点');
        }
        if (_contains(keys, 'hour') && parseInt($courseHours.text())<=0) {
            need_list.push('上课时间');
        }
        if (_contains(keys, 'time_slot') && weekly_time_slot_ids.length==0) {
            need_list.push('上课时间');
        }
        if (need_list.length) {
            showAlertDialog(msg_pre+need_list.join('和'));
            return false;
        }
        return true;
    };

    $weeklyTable.find('tbody > tr > td').click(function(e) {
        if (!valid_choose(['grade', 'school'])) {
            return;
        }
        var $this = $(this);
        if ($this.hasClass('available')) {
            $this.toggleClass('chosen');
            updateCourseTimePreview();
        }
    });

    $courseTimePreviewPanel.click(function(){
        if (!valid_choose(['grade', 'school', 'hour'])) {
            return;
        }
        var $panel = $(this);
        $panel.toggleClass('closed');
        if ($panel.hasClass('closed')) {
            $courseTimePreview.hide();
        } else {
            $courseTimePreview.show();
            _updateCourseTimePreview(parseInt($courseHours.text()));
        }
    });

    $(".flag.reserved").click(function(e){
        e.preventDefault();
        showAlertDialog("您在该时间段有课程未上完，直到您在该时间段课程全部结束后的12小时，我们都为您保留老师时间，方便您继续购买。");
        e.stopPropagation();
    });

    $('#decHoursBtn').click(function(e){
        var hours = parseInt($courseHours.text());
        if (hours <= weekly_time_slot_ids.length * 2) {
            return;
        }
        hours -= 2;
        $courseHours.html(hours);
        sessionStorage.hours = hours;
        _updateCourseTimePreview(hours);
        updateCost();
    });
    $('#incHoursBtn').click(function(e){
        if (!valid_choose(['grade', 'school', 'time_slot'])) {
            return;
        }
        var hours = parseInt($courseHours.text());
        hours += 2;
        $courseHours.html(hours);
        sessionStorage.hours = hours;
        _updateCourseTimePreview(hours);
        updateCost();
    });

    // 去奖学金页面
    $('#couponRow').click(function(){
        if (!valid_choose(['grade', 'school', 'hour'])) {
            return;
        }
        location.href = coupon_list_page;
    });

    // 去测评建档服务页面
    $('#evaluateRow').click(function(e){
        location.href = evaluate_list_page;
    });

    var $payBtn = $('#confirmBtn');
    $payBtn.click(function(e){
        e.preventDefault();
        if (!valid_choose(['grade', 'school', 'hour'])) {
            return;
        }
        location.href = confirm_page + '&grade_id=' + chosen_grade_id + '&school_id='+chosen_school_id;
        e.stopPropagation();
    });

    // 从sessionStorage恢复数据
    (function(){
        if (sessionStorage.hours) {
            $courseHours.html(sessionStorage.hours);
        }
        if (sessionStorage.chosen_coupon_id) {
            chosen_coupon_id = sessionStorage.chosen_coupon_id;
            chosen_coupon_min_cost = sessionStorage.chosen_coupon_min_cost;
            chosen_coupon_amount = sessionStorage.chosen_coupon_amount;
        }
        if (sessionStorage.weekly_time_slot_ids) {
            weekly_time_slot_ids = sessionStorage.weekly_time_slot_ids.split('+');
        }
        if (sessionStorage.chosen_grade_id) {
            chosen_grade_id = sessionStorage.chosen_grade_id;
            $('.grade').each(function(){
                var $this = $(this), v = $this.data('gradeid');
                if (v==chosen_grade_id) {
                    chosen_price = parseInt($this.find('input').val());
                    sessionStorage.chosen_price = chosen_price;
                    $this.addClass('chosen');
                    return false;
                }
            });
        }
        if (sessionStorage.chosen_school_id) {
            chosen_school_id = sessionStorage.chosen_school_id;
            var $chosenone = null;
            $('.school').each(function(){
                var $this = $(this), scid = $this.attr('scid');
                if (scid==chosen_school_id) {
                    $chosenone = $this;
                    return false;
                }
            });
            if ($chosenone) {
                $chosenone.addClass('chosen');
                $chosenone.show();
                hideOtherSchools($chosenone);
                renderWeeklyTableBySchool(chosen_school_id, weekly_time_slot_ids);
            }
        }
        updateCost();
    })();
});
