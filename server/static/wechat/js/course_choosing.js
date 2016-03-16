/**
 * Created by liumengjun on 3/5/16.
 */
$(function(){
    //alert("course choosing");
    var teacherId = $('#teacherId').val();
    var chosen_grade_id = '';
    var chosen_price = 0;
    var chosen_school_id = '';
    var chosen_weekly_time_slots = [];
    var chosen_coupon_id = 0;
    var HOUR = 60*60*1000;
    var DAY = 24*HOUR;
    var is_first_buy = $("#isFirstBuy").val()=='True';
    var evaluate_time = parseInt($("#evaluateTime").val())*1000; // 原单位秒
    var now = new Date(), weekday = now.getDay()==0?7:now.getDay();
    var today = new Date(now.getFullYear(), now.getMonth(), now.getDate()), todayTime = today.getTime();
    var courseStartTime = is_first_buy?(todayTime+evaluate_time+DAY):todayTime;

    var pre_chosen_coupon_id = $("#preChosenCoupon").val();
    var $coupons = $('.coupon');
    if (pre_chosen_coupon_id) {
        chosen_coupon_id = pre_chosen_coupon_id;
        $coupons.each(function () {
            var $this = $(this), cpid = $this.attr('couponId');
            if (cpid == pre_chosen_coupon_id) {
                $this.addClass('chosen');
                return false;
            }
        });
    }

    var $payArea = $('#payArea');
    var $alertDialog = $('#alertDialog');
    var $alertDialogBody = $("#alertDialogBody");
    var showAlertDialog = function(msg) {
        $alertDialogBody.html(msg);
        $payArea.hide();
        $alertDialog.show();
        $alertDialog.one('click', function () {
            $alertDialog.hide();
            $payArea.show();
        });
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
                $this.find('.distance').html('...');
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
        $chosenone.css('display', 'flex');
        hideOtherSchools($chosenone);
    };

    $(document).on('ajaxError', function(e, xhr, options){
        showAlertDialog('请求失败, 请重试');
    });
    wx.ready(function(res){
        console.log("wx.ready");
        wx.getLocation({
            type: 'wgs84', // 默认为wgs84的gps坐标，如果要返回直接给openLocation用的火星坐标，可传入'gcj02'
            success: function(res){
                console.log('wx.getLocation success');
                console.log(res);
                var reqparams = {'action': 'schools_dist', 'lat': res.latitude, 'lng': res.longitude};
                $.post(location.href, reqparams, function(result){
                    console.log(result);
                    if (result && result.ok) {
                        sortSchools(result.list);
                    }
                }, 'json')
            },
            fail: function(res){
                console.log('wx.getLocation fail');
            }
        });
    });

    wx.error(function(){
        console.log("wx.config错误");
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
        updateCost();
        e.stopPropagation();
    });

    $('#showMoreSchoolsBtn').click(function(e){
        $(this).hide();
        var $schools = $('.school');
        $schools.css('display', 'flex');
        $schools.last().addClass('last');
    });

    var $courseTimePreview = $("#courseTimePreview");
    var _updateCourseTimePreview = function(hours) {
        if (hours==0 || chosen_weekly_time_slots.length==0) {
            return $("#courseTimePreview").html('');
        }
        chosen_weekly_time_slots.sort(function(a,b){
            var dayA = weekday >= a.day?(7+a.day):a.day,
                dayB = weekday >= b.day?(7+b.day):b.day;
            var dd = dayA - dayB;
            return (dd != 0) ? dd : (a.start - b.start);
        });
        var courseTimes = [];
        var count = hours/ 2, loop = 0;
        while(count>0) {
            for (var i = 0; i < chosen_weekly_time_slots.length && count>0; i++) {
                var wts = chosen_weekly_time_slots[i], day = wts.day, s = wts.start, e = wts.end;
                var weekoffset = (weekday < day?0:1) + loop;
                var date = todayTime + (day-weekday + weekoffset * 7)*DAY;
                if (date<courseStartTime) {
                    continue;
                }
                courseTimes.push({'date': date, 'start': s, 'end': e});
                count--;
            }
            loop++;
        }
        courseTimes.sort(function(a,b){
            var diff = a.date - b.date;
            return (diff != 0) ? diff : (a.start - b.start);
        });
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

    var updateCourseTimePreview = function() {
        var $chosenTimeSlot = $('#weeklyTable > tbody > tr > td.available.chosen');
        chosen_weekly_time_slots.length=0;
        $chosenTimeSlot.each(function(i, ele){
            var $td = $(ele), $tr = $td.closest('tr');
            var day = parseInt($td.attr('day')),
                start = parseInt($tr.attr('start').split(':')[0]) + parseInt($tr.attr('start').split(':')[1])/60,
                end = parseInt($tr.attr('end').split(':')[0]) + parseInt($tr.attr('end').split(':')[1])/60;
            var s = start * HOUR;
            var e = end * HOUR;
            chosen_weekly_time_slots.push({'id': $td.attr('tsid'), 'day': day, 'start': s, 'end': e})
        });
        console.log(chosen_weekly_time_slots);
        var hours = parseInt($('#courseHours').text());
        if (chosen_weekly_time_slots.length==0) {
            hours = 0;
        } else {
            hours = Math.max(chosen_weekly_time_slots.length * 2, hours);
        }
        $('#courseHours').html(hours);
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
    var renderWeeklyTableBySchool = function(school_id) {
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
                    var key = timespan+'_'+ i, ts = _map[key];
                    var $td = $(ele);
                    if (ts && ts.available) {
                        $td.attr('tsid', ts.id);
                        $td.addClass('available');
                    } else {
                        $td.removeClass('available').addClass('unavailable');
                    }
                });
            });
            updateCourseTimePreview();
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

    var updateCost = function() {
        var hours = parseInt($('#courseHours').text());
        var origTotalCost = hours * chosen_price; // 单位是分
        // 检查奖学金
        if (hours==0) {
            $('#discountCost').html('0');
        } else {
            var $coupon = null;
            if (chosen_coupon_id) {
                $coupon = $('.coupon[couponId="' + chosen_coupon_id + '"]');
                var min_count = parseInt($coupon.find('.ccount').text());
                if (hours < min_count) {
                    chosen_coupon_id = 0;
                    $coupon.removeClass('chosen');
                    $('#discountCost').html('0');
                }
            }
            if (!chosen_coupon_id) {
                // auto choose another one
                $coupons.each(function () {
                    var _$cp = $(this), _min_count = parseInt(_$cp.find('.ccount').text());
                    if (hours >= _min_count) {
                        _$cp.addClass('chosen');
                        chosen_coupon_id = _$cp.attr('couponId');
                        return false;
                    }
                });
            }
            if (chosen_coupon_id) { // get discount
                $coupon = $('.coupon[couponId="' + chosen_coupon_id + '"]');
                $('#discountCost').html($coupon.find('.amount').text());
            }
        }
        var discount = parseFloat($('#discountCost').text()); // 单位是元
        var realCost = origTotalCost - discount * 100;
        if (origTotalCost>0) {
            realCost = Math.max(realCost, 1); // 暂时不支持免费订单, 最少1分
        }
        $("#origTotalCost").text(_format_money(origTotalCost));
        $("#realCost").text(_format_money(realCost));
    };

    $weeklyTable.find('tbody > tr > td').click(function(e) {
        if (!chosen_grade_id || !chosen_school_id) {
            showAlertDialog('请先选择授课年级和上课地点');
            return;
        }
        var $this = $(this);
        if ($this.hasClass('available')) {
            $this.toggleClass('chosen');
            updateCourseTimePreview();
        }
    });

    $('#decHoursBtn').click(function(e){
        var hours = parseInt($('#courseHours').text());
        if (hours <= chosen_weekly_time_slots.length * 2) {
            return;
        }
        hours -= 2;
        $('#courseHours').html(hours);
        _updateCourseTimePreview(hours);
        updateCost();
    });
    $('#incHoursBtn').click(function(e){
        if (chosen_weekly_time_slots.length==0) {
            showAlertDialog('请先选择上课时间');
            return;
        }
        var hours = parseInt($('#courseHours').text());
        hours += 2;
        $('#courseHours').html(hours);
        _updateCourseTimePreview(hours);
        updateCost();
    });

    $('#couponRow').click(function(){
        if ($coupons.length==0) {
            showAlertDialog('您没有可用奖学金');
            return;
        }
        if ($($coupons[0]).css('display')!='none') {
            $coupons.hide();
        } else {
            $coupons.show();
        }
    });
    $coupons.click(function(){
        var hours = parseInt($('#courseHours').text());
        if (hours==0) {
            showAlertDialog('请先选择上课时间');
            return;
        }
        var $this = $(this), cpid = $this.attr('couponId');
        if (cpid==chosen_coupon_id) {
            $coupons.hide();
            return;
        }
        var min_count = parseInt($this.find('.ccount').text());
        if (hours<min_count){
            showAlertDialog('课时数不足');
            return;
        }
        // choose this one
        chosen_coupon_id = cpid;
        $coupons.each(function () {
            var _$this = $(this), _cpid = _$this.attr('couponId');
            if (_cpid == chosen_coupon_id) {
                _$this.addClass('chosen');
            } else {
                _$this.removeClass('chosen');
            }
        });
        // 更新discount不在这里做, 在后面的updateCost()方法里
        updateCost();
        $coupons.hide();
    });

    $('#confirmBtn').click(function(e){
        var hours = parseInt($('#courseHours').text());
        if (hours <= 0) {
            showAlertDialog('请先选择上课时间');
            return;
        }
        var weekly_time_slot_ids = [];
        for (var i in chosen_weekly_time_slots) {
            weekly_time_slot_ids.push(chosen_weekly_time_slots[i].id)
        }
        var params = {
            'action': 'confirm',
            'teacher': teacherId,
            'school': chosen_school_id,
            'grade': chosen_grade_id,
            'coupon': chosen_coupon_id,
            'hours': hours,
            'weekly_time_slots': weekly_time_slot_ids.join('+')
        };
        var defaultErrMsg = '请求失败, 请稍后重试或联系客户人员!';
        $.post(location.pathname, params, function (result) {
            if (result) {
                if (result.ok) {
                    var data = result.data, prepay_id = data.prepay_id, order_id = data.order_id;
                    wx.chooseWXPay({
                        timestamp: data.timeStamp, // 支付签名时间戳，注意微信jssdk中的所有使用timestamp字段均为小写。但最新版的支付后台生成签名使用的timeStamp字段名需大写其中的S字符
                        nonceStr: data.nonceStr, // 支付签名随机串，不长于 32 位
                        package: data.package, // 统一支付接口返回的prepay_id参数值，提交格式如：prepay_id=***）
                        signType: data.signType, // 签名方式，默认为'SHA1'，使用新版支付需传入'MD5'
                        paySign: data.paySign, // 支付签名
                        success: function (res) {
                            console.log('wx.chooseWXPay success');
                            console.log(res);
                            var verify_params = {
                                'action': 'verify',
                                'prepay_id': prepay_id,
                                'order_id': order_id
                            };
                            $.post(location.pathname, verify_params, function(verify_ret){
                                if (verify_ret) {
                                    if (verify_ret.ok) {
                                        location.href = teacher_detail_page;
                                        //showAlertDialog('支付成功');
                                        return;
                                    } else {
                                        showAlertDialog(result.msg);
                                    }
                                } else {
                                    showAlertDialog(defaultErrMsg);
                                }
                            });
                        },
                        fail: function(res){
                            console.log('wx.chooseWXPay fail');
                        }
                    });
                } else {
                    showAlertDialog(result.msg);
                }
            } else {
                showAlertDialog(defaultErrMsg);
            }
        }, 'json');
    });

    // 初始化"测评建档服务"内容
    var $evaluateRow = $('#evaluateRow');
    if ($evaluateRow[0]) {
        var $evaluateDialog = $('#evaluateItemsDialog');
        var $evaluateItemsBody = $('#evaluateItemsBody');
        var buf = [];
        for (var i in evaluateItems) {
            var obj = evaluateItems[i];
            buf.push('<div class="evaluate-item">');
            buf.push('<div class="evaluate-title">');
            buf.push(obj.title);
            buf.push('</div>');
            buf.push('<div class="evaluate-photo">');
            buf.push('<img src="'+obj.photo+'"/>');
            buf.push('</div>');
            buf.push('<div class="evaluate-note">');
            buf.push(obj.desc);
            buf.push('</div>');
            buf.push('</div>');
        }
        $evaluateItemsBody.append(buf.join(''));
        $evaluateRow.click(function(e){
            $payArea.hide();
            $evaluateDialog.show();
            $evaluateDialog.one('click', function () {
                $evaluateDialog.hide();
                $payArea.show();
            });
        });
    }
});
