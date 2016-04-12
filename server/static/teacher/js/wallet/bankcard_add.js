/**
 * Created by liumengjun on 2/23/16.
 */
$(function () {
    $('[data-toggle="tooltip"]').tooltip({'html':true});

    $('.hint .close-icon').click(function(){
        $(this).closest('.hint').hide();
    });

    $("select[name=province]").change(function(e){
        var pro_id = $(this).val(), $city_sel = $("select[name=city]"), $dist_sel = $("select[name=district]");
        $city_sel.find('option:gt(0)').remove();
        $dist_sel.find('option:gt(0)').remove();
        if (!pro_id) return;
        malaAjaxGet('/api/v1/regions', {'action': 'sub-regions', 'sid': pro_id}, function(json){
            if (json && json.results) {
                for (var i in json.results) {
                    var reg = json.results[i];
                    $city_sel.append('<option value="'+reg.id+'">'+reg.name+'</option>');
                }
            }
        });
    });
    $("select[name=city]").change(function(e){
        var city_id = $(this).val(), $dist_sel = $("select[name=district]");
        $dist_sel.find('option:gt(0)').remove();
        if (!city_id) return;
        malaAjaxGet('/api/v1/regions', {'action': 'sub-regions', 'sid': city_id}, function(json){
            if (json && json.results) {
                for (var i in json.results) {
                    var reg = json.results[i];
                    $dist_sel.append('<option value="'+reg.id+'">'+reg.name+'</option>');
                }
            }
        });
    });

    // 检测身份证号
    var checkIdNum = function(ignoreBlank, hospitable){
        var $idNum = $('#id_num'), $formRow = $idNum.closest('.form-group'), id_num = $.trim($idNum.val());
        if (!id_num && ignoreBlank) return false;
        var ok = checkIDNumber(id_num);
        if (ok) {
            $formRow.removeClass('has-error');
            $formRow.find('.hint-block').html('');
        } else {
            if (!hospitable) {
                $formRow.addClass('has-error');
                $formRow.find('.hint-block').html('身份证号错误');
            }
        }
        return ok;
    };
    checkIdNum(true);
    // 检测银行卡号
    var checkCardNum = function(ignoreBlank, hospitable){
        var $card_number = $('#card_number'), $formRow = $card_number.closest('.form-group'), card_number = $.trim($card_number.val());
        if (!card_number && ignoreBlank) return false;
        card_number = card_number.replace(/\s+/g,"");
        var ok = /^\d{16,19}$/.test(card_number);
        if (ok) {
            $formRow.removeClass('has-error');
            $formRow.find('.hint-block').html('');
        } else {
            if (!hospitable) {
                $formRow.addClass('has-error');
                $formRow.find('.hint-block').html('银行卡号格式错误');
            }
        }
        return ok;
    };
    checkCardNum(true);
    // 检测手机号
    var checkPhone = function(ignoreBlank, hospitable){
        var $phone = $('#phone'), $formRow = $phone.closest('.form-group'), phone = $.trim($phone.val());
        if (!phone && ignoreBlank) return false;
        var ok = checkMobile(phone);
        if (ok) {
            $formRow.removeClass('has-error');
            $formRow.find('.hint-block').html('');
        } else {
            if (!hospitable) {
                $formRow.addClass('has-error');
                $formRow.find('.hint-block').html('手机号格式错误');
            }
        }
        return ok;
    };
    checkPhone(true);
    // 检测所在地区
    var $province = $("#province"), $city = $("#city"), $district = $("#district");
    var getRegion = function(){
        var province = $.trim($province.val()), city = $.trim($city.val()), district = $.trim($district.val());
        return district || city || province;
    };
    var checkRegion = function(hospitable){
        var $formRow = $province.closest('.form-group'), region = getRegion();
        var ok = !!region;
        if (ok) {
            $formRow.removeClass('has-error');
            $formRow.find('.hint-block').html('');
        } else {
            if (!hospitable) {
                $formRow.addClass('has-error');
                $formRow.find('.hint-block').html('所属省市不能为空');
            }
        }
        return ok;
    };
    // 检测开户行
    var $openingBack = $('#opening_bank');
    var getOpeningBack = function(){
        return $.trim($openingBack.val());
    };
    var checkOpeningBank = function(hospitable){
        var $formRow = $openingBack.closest('.form-group'), openingBack = getOpeningBack();
        var ok = !!openingBack;
        if (ok) {
            $formRow.removeClass('has-error');
            $formRow.find('.hint-block').html('');
        } else {
            if (!hospitable) {
                $formRow.addClass('has-error');
                $formRow.find('.hint-block').html('开户行不能为空');
            }
        }
        return ok;
    };

    var canGetCheckcode = function() {
        var ok = true;
        ok = ok && !!$.trim($('#id_num').val()); // check if has ID number
        ok = ok ? ok && checkIdNum(): false;
        ok = ok && !!$.trim($('#card_number').val()); // check if has card number
        ok = ok ? ok && checkCardNum(): false;
        ok = ok && !!getOpeningBack(); // check if has opening bank
        ok = ok ? ok && checkOpeningBank(): false;
        ok = ok && !!getRegion(); // check if has region
        ok = ok ? ok && checkRegion(): false;
        ok = ok && !!$.trim($('#phone').val()); // check if has phone number
        ok = ok ? ok && checkPhone(): false;
        return ok;
    };

    var validateGetCheckcode = function() {
        if (canGetCheckcode()) {
            $getCodeBtn = $("[data-action=get-checkcode]");
            if ($getCodeBtn.data('getting')) {
                return;
            }
            $getCodeBtn.removeClass('disabled');
        } else {
            $("[data-action=get-checkcode]").addClass('disabled');
        }
    };

    var checkCheckcode = function() {
        var $checkcode = $('#checkcode'), $formRow = $checkcode.closest('.form-group'), checkcode = $.trim($checkcode.val());
        if (!checkcode) {
            return false;
        }
        var ok = /^\d+$/.test(checkcode);
        if (ok) {
            $formRow.removeClass('has-error');
            $formRow.find('.hint-block').html('');
        } else {
            $formRow.addClass('has-error');
            $formRow.find('.hint-block').html('验证码格式错误');
        }
        return ok;
    };

    var canNextStep = function() {
        var ok = canGetCheckcode();
        ok = ok ? ok && checkCheckcode(): false;
        return ok;
    };

    var validateNextStep = function() {
        if (canNextStep()) {
            $("[data-action=next-step]").removeClass('disabled');
        } else {
            $("[data-action=next-step]").addClass('disabled');
        }
    };

    var validateUI = function() {
        validateGetCheckcode();
        validateNextStep();
    };

    $('#id_num').blur(function(e){
        checkIdNum();
        validateUI();
    });
    $('#id_num').bind("input propertychange change keyup", function(e){
        checkIdNum(false, true);
        validateUI();
    });
    var formatCardNumber = function() {
        var $card_number = $('#card_number'), card_number = $.trim($card_number.val());
        if (!card_number) return false;
        card_number = card_number.replace(/\s+/g,"");
        var newVal = '';
        for (var i=0; i<card_number.length; i++) {
            newVal += card_number.charAt(i);
            if (i!=card_number.length-1 && (i+1)%4==0) {
                newVal += ' ';
            }
        }
        $card_number.val(newVal);
    };
    $('#card_number').blur(function(e){
        checkCardNum();
        formatCardNumber();
        validateUI();
    });
    $('#card_number').bind("input propertychange change keyup", function(e){
        checkCardNum(false, true);
        formatCardNumber();
        validateUI();
    });
    $('#opening_bank').blur(function(e){
        checkOpeningBank();
        validateUI();
    });
    $('#opening_bank').bind("input propertychange change keyup", function(e){
        checkOpeningBank(true);
        validateUI();
    });
    $('select').blur(function(e){
        checkRegion();
        validateUI();
    });
    $('select').bind("input propertychange change keyup", function(e){
        checkRegion(true);
        validateUI();
    });
    $('#phone').blur(function(e){
        checkPhone();
        validateUI();
    });
    $('#phone').bind("input propertychange change keyup", function(e){
        checkPhone(false, true);
        validateUI();
    });

    $('#checkcode').bind("input propertychange change keyup blur", function(e){
        validateUI();
    });

    // actions
    $("[data-action=get-checkcode]").click(function(e){
        if (!canGetCheckcode()) {
            return alert('请确保以上信息填写完整');
        }
        var $this = $(this), timer, countdown = 60;
        $this.addClass('disabled');
        var phone = $.trim($('#phone').val());
        $.post("/api/v1/sms", {'action':"send", 'phone':phone}, function(data){
            if (data) {
                if (data.sent) {
                    // build a timer, after {countdown} seconds, the user can click this button again
                    timer = window.setInterval(function(){
                        if (countdown<=0) {
                            window.clearInterval(timer);
                            $this.data('getting', false);
                            $this.html("重新获取");
                            $this.val("重新获取");
                            $this.removeClass('disabled');
                            return;
                        }
                        $this.html("重新获取("+countdown+")");
                        $this.val("重新获取("+countdown+")");
                        countdown--;
                    },1000);
                    $this.data('getting', true);
                } else {
                    alert(data.reason);
                    $this.removeClass('disabled');
                }
            } else {
                alert("获取失败, 请稍后再试!");
                $this.removeClass('disabled');
            }
        });
    });
    var defaultErrMsg = '请求失败,请稍后重试,或联系管理员!';
    $("[data-action=next-step]").click(function(e){
        if (!canNextStep()) {
            return alert('请确保以上信息填写完整');
        }
        showLoading();
        var $form = $("#bankcardAddForm");
        $form.ajaxSubmit({
            dataType: 'json',
            success: function(result){
                if (result) {
                    if (result.ok) {
                        location.href = location.pathname + 'success/';
                    } else {
                        alert(result.msg);
                    }
                } else {
                    alert(defaultErrMsg);
                }
                hideLoading();
            },
            error: function(jqXHR, errorType, errorDesc){
                var errMsg = errorDesc?('['+errorDesc+'] '):'';
                alert(errMsg+defaultErrMsg);
                hideLoading();
            }
        });
    });
});
