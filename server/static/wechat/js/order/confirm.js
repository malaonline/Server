/**
 * Created by liumengjun on 3/5/16.
 */
$(function(){
    //alert("course choosing");
    var teacherId = $('#teacherId').val();
    var chosen_grade_id = '';
    var chosen_price = 0;
    var chosen_school_id = '';
    var weekly_time_slot_ids = [];
    var chosen_coupon_id = '';
    var chosen_coupon_amount = 0;
    var chosen_coupon_min_count = 0;
    var chosen_hours = 0;
    var WEEKDAYS = '日一二三四五六';

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


    var previewCourseTimeUrl = '/api/v1/concrete/timeslots';
    var $courseTimePreviewPanel = $('#courseTimePreviewPanel');
    var $courseTimePreview = $("#courseTimePreview");
    var $courseHours = $('#courseHours');
    var __showCourseTime = function(courseTimes){
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
            $courseTimePreview.append('<div class="ct-row">' + hb.join('') + '</div>');
        }
    };
    var _updateCourseTimePreview = function(hours) {
        if (hours==0 || weekly_time_slot_ids.length==0) {
            $courseTimePreviewPanel.addClass('closed');
            $courseTimePreview.hide();
            return $courseTimePreview.html('');
        }
        if ($courseTimePreviewPanel.hasClass('closed')) {
            return;
        }
        $courseTimePreview.html('');
        var params = {'hours':hours, 'weekly_time_slots':weekly_time_slot_ids.join(' '), 'teacher': teacherId};
        $.ajax({'type':"GET", 'url': previewCourseTimeUrl, 'data': params, 'success': function(json){
            if (json && json.data) {
                __showCourseTime(json.data);
            }
            hideLoading();
        }, 'dataType': 'json', 'error': function() {
            hideLoading();
            $courseTimePreview.html('<p>&nbsp;加载失败</p>');
        }
        });
    };

    var _format_money = function(num, isYuan) {
        if (isYuan) {
            num = num * 100;
        }
        // 直接抹零, 但是toFixed 默认是四舍五入
        return (parseInt(num)/100).toFixed(2);
    };

    var $realCost = $("#realCost");
    var updateCost = function() {
        var origTotalCost = chosen_hours * chosen_price; // 单位是分
        var discount = parseFloat(chosen_coupon_amount); // 单位是元
        var realCost = origTotalCost - discount * 100;
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
        if (_contains(keys, 'hour') && chosen_hours<=0) {
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

    var isPaying = false;
    var $payBtn = $('#confirmBtn');
    var beginPaying = function() {
        isPaying = true;
        $payBtn.addClass('weui_btn_disabled');
        showLoading();
    };
    var stopPaying = function() {
        hideLoading();
        $payBtn.removeClass('weui_btn_disabled');
        isPaying = false;
    };
    $payBtn.click(function(e){
        e.preventDefault();
        if (!valid_choose(['grade', 'school', 'hour'])) {
            return;
        }
        if (isPaying) {
            return;
        }
        beginPaying();
        var params = {
            'action': 'confirm',
            'teacher': teacherId,
            'school': chosen_school_id,
            'grade': chosen_grade_id,
            'coupon': chosen_coupon_id,
            'hours': chosen_hours,
            'weekly_time_slots': weekly_time_slot_ids.join('+')
        };
        var defaultErrMsg = '请求失败, 请稍后重试或联系客户人员!';
        $.ajax({'type': "POST", 'url': order_pay_url, 'data': params, 'success': function (result) {
            if (result) {
                if (result.ok) {
                    var data = result.data, prepay_id = data.prepay_id, order_id = data.order_id;
                    if (data.TESTING) {
                        $.ajax({ // 取消订单@TESTING
                            'type': "DELETE", 'url': data.orders_api_url, 'success': function (result) {
                                stopPaying();
                            }, 'error': function (xhr, errorType, errorDesc) {
                                console.log('[' + errorType + '] ' + errorDesc);
                                stopPaying();
                            }
                        });
                        return;
                    }
                    wx.chooseWXPay({
                        timestamp: data.timeStamp, // 支付签名时间戳，注意微信jssdk中的所有使用timestamp字段均为小写。但最新版的支付后台生成签名使用的timeStamp字段名需大写其中的S字符
                        nonceStr: data.nonceStr, // 支付签名随机串，不长于 32 位
                        package: data.package, // 统一支付接口返回的prepay_id参数值，提交格式如：prepay_id=***）
                        signType: data.signType, // 签名方式，默认为'SHA1'，使用新版支付需传入'MD5'
                        paySign: data.paySign, // 支付签名
                        success: function (res) {
                            var verify_params = {
                                'action': 'verify',
                                'prepay_id': prepay_id,
                                'order_id': order_id
                            };
                            $.ajax({'type': "POST", 'url': location.pathname, 'data': verify_params, 'success': function(verify_ret){
                                if (verify_ret) {
                                    if (verify_ret.ok) {
                                        //showAlertDialog('支付成功');
                                        wx.closeWindow();
                                        return;
                                    } else {
                                        showAlertDialog(result.msg);
                                    }
                                } else {
                                    showAlertDialog(defaultErrMsg);
                                }
                                stopPaying();
                            }, 'dataType': 'json', 'error': function() {
                                showAlertDialog('获取支付结果失败');
                                stopPaying();
                            }
                            });
                        },
                        fail: function(res){
                            $.ajax({ // 取消订单
                                'type': "DELETE", 'url': data.orders_api_url, 'success': function(result){
                                    stopPaying();
                                }, 'error': function(){
                                    stopPaying();
                                }
                            });
                        },
                        //complete: function(){
                        //    stopPaying();
                        //},
                        cancel: function(){
                            $.ajax({ // 取消订单
                                'type': "DELETE", 'url': data.orders_api_url, 'success': function(result){
                                    stopPaying();
                                }, 'error': function(){
                                    stopPaying();
                                }
                            });
                        }
                    });
                } else {
                    showAlertDialog(result.msg);
                    stopPaying();
                    if (result.code==3) {
                        renderWeeklyTableBySchool(chosen_school_id);
                    }
                }
            } else {
                showAlertDialog(defaultErrMsg);
                stopPaying();
            }
        }, 'dataType': 'json', 'error': function() {
            stopPaying();
        }
        });
        e.stopPropagation();
    });

    // 从sessionStorage恢复数据
    (function(){
        if (sessionStorage.hours) {
            chosen_hours = parseInt(sessionStorage.hours);
            $("#totalHours").html(sessionStorage.hours);
        }
        if (sessionStorage.chosen_coupon_id) {
            chosen_coupon_id = sessionStorage.chosen_coupon_id;
            chosen_coupon_min_count = sessionStorage.chosen_coupon_min_count;
            chosen_coupon_amount = sessionStorage.chosen_coupon_amount;
        }
        if (sessionStorage.weekly_time_slot_ids) {
            weekly_time_slot_ids = sessionStorage.weekly_time_slot_ids.split('+');
        }
        if (sessionStorage.chosen_grade_id) {
            chosen_grade_id = sessionStorage.chosen_grade_id;
        }
        if (sessionStorage.chosen_price) {
            chosen_price = parseFloat(sessionStorage.chosen_price);
        }
        if (sessionStorage.chosen_school_id) {
            chosen_school_id = sessionStorage.chosen_school_id;
        }
        updateCost();
    })();

    // 显示上课时间
    (function(){
        $courseTimePreviewPanel.removeClass('closed');
        $courseTimePreview.show();
        showLoading();
        _updateCourseTimePreview(chosen_hours);
    })();
});
