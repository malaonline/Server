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

    var clsRowError = function($formRow) {
        $formRow.removeClass('has-error');
        $formRow.find('.hint-block').html('');
    };
    var setRowError = function($formRow, msg) {
        $formRow.addClass('has-error');
        $formRow.find('.hint-block').html(msg);
    };

    // 检测身份证号
    var $idNum = $('#id_num');
    var getIdNum = function(){
        return $.trim($idNum.val());
    };
    var checkIdNum = function(ignoreBlank, hospitable){
        var $formRow = $idNum.closest('.form-group'), id_num = getIdNum();
        if (!id_num && ignoreBlank) return false;
        var ok = checkIDNumber(id_num);
        if (ok) {
            clsRowError($formRow);
        } else {
            if (!hospitable) {
                setRowError($formRow, '身份证号错误');
            }
        }
        return ok;
    };
    checkIdNum(true);
    // 检测银行卡号
    var $cardNumber = $('#card_number');
    var getCardNumer = function(){
        return $.trim($cardNumber.val())
    };
    var checkCardNum = function(ignoreBlank, hospitable){
        var $formRow = $cardNumber.closest('.form-group'), card_number = getCardNumer();
        if (!card_number && ignoreBlank) {
            if (!hospitable) {
                setRowError($formRow, '银行卡号格式错误');
            }
            return false;
        }
        card_number = card_number.replace(/\s+/g,"");
        var ok = /^\d{16,19}$/.test(card_number);
        if (ok) {
            clsRowError($formRow);
        } else {
            if (!hospitable) {
                setRowError($formRow, '银行卡号格式错误');
            }
        }
        return ok;
    };
    checkCardNum(true, true);
    // 检测手机号
    var $phone = $('#phone');
    var getPhone = function(){
        return $.trim($phone.val());
    };
    var checkPhone = function(ignoreBlank, hospitable){
        var $formRow = $phone.closest('.form-group'), phone = getPhone();
        if (!phone && ignoreBlank) {
            if (!hospitable) {
                setRowError($formRow, '手机号格式错误');
            }
            return false;
        }
        var ok = checkMobile(phone);
        if (ok) {
            clsRowError($formRow);
        } else {
            if (!hospitable) {
                setRowError($formRow, '手机号格式错误');
            }
        }
        return ok;
    };
    checkPhone(true, true);
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
            clsRowError($formRow);
        } else {
            if (!hospitable) {
                setRowError($formRow, '所属省市不能为空');
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
            clsRowError($formRow);
        } else {
            if (!hospitable) {
                setRowError($formRow, '开户行不能为空');
            }
        }
        return ok;
    };

    var canGetCheckcode = function(force) {
        var ok = true;
        ok = ok && !!getIdNum(); // check if has ID number
        ok = ok ? ok && checkIdNum(): false;
        ok = ok && !!getCardNumer(); // check if has card number
        ok = force ? checkCardNum(false, !force) && ok : (ok ? ok && checkCardNum(false, !force): false);
        ok = ok && !!getOpeningBack(); // check if has opening bank
        ok = force ? checkOpeningBank(false, !force) && ok : (ok ? ok && checkOpeningBank(!force): false);
        ok = ok && !!getRegion(); // check if has region
        ok = force ? checkRegion(false, !force) && ok : (ok ? ok && checkRegion(!force): false);
        ok = ok && !!getPhone(); // check if has phone number
        ok = force ? checkPhone(false, !force) && ok : (ok ? ok && checkPhone(false, !force): false);
        return ok;
    };

    var validateGetCheckcode = function(force) {
        if (canGetCheckcode(force)) {
            $getCodeBtn = $("[data-action=get-checkcode]");
            if ($getCodeBtn.data('getting')) {
                return;
            }
            $getCodeBtn.removeClass('disabled');
        } else {
            $("[data-action=get-checkcode]").addClass('disabled');
        }
    };

    var $checkcode = $('#checkcode');
    var checkCheckcode = function() {
        var $formRow = $checkcode.closest('.form-group'), checkcode = $.trim($checkcode.val());
        if (!checkcode) {
            return false;
        }
        var ok = /^\d+$/.test(checkcode);
        if (ok) {
            clsRowError($formRow);
        } else {
            setRowError($formRow, '验证码格式错误');
        }
        return ok;
    };

    var canNextStep = function(force) {
        var ok = canGetCheckcode(force);
        ok = ok ? ok && checkCheckcode(): false;
        return ok;
    };

    var validateNextStep = function(force) {
        if (canNextStep(force)) {
            $("[data-action=next-step]").removeClass('disabled');
        } else {
            $("[data-action=next-step]").addClass('disabled');
        }
    };

    var validateUI = function(force) {
        validateGetCheckcode(force);
        validateNextStep(force);
    };

    $idNum.blur(function(e){
        checkIdNum();
        validateUI();
    });
    $idNum.bind("input propertychange change keyup", function(e){
        checkIdNum(false, true);
        validateUI();
    });
    var formatCardNumber = function() {
        var card_number = getCardNumer();
        if (!card_number) return false;
        card_number = card_number.replace(/\s+/g,"");
        var newVal = '';
        for (var i=0; i<card_number.length; i++) {
            newVal += card_number.charAt(i);
            if (i!=card_number.length-1 && (i+1)%4==0) {
                newVal += ' ';
            }
        }
        $cardNumber.val(newVal);
    };
    $cardNumber.blur(function(e){
        checkCardNum();
        formatCardNumber();
        validateUI();
    });
    $cardNumber.bind("input propertychange change keyup", function(e){
        checkCardNum(false, true);
        formatCardNumber();
        validateUI();
    });
    $openingBack.blur(function(e){
        checkOpeningBank();
        validateUI();
    });
    $openingBack.bind("input propertychange change keyup", function(e){
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
    $phone.blur(function(e){
        checkPhone();
        validateUI();
    });
    $phone.bind("input propertychange change keyup", function(e){
        checkPhone(false, true);
        validateUI();
    });

    $checkcode.bind("focus input propertychange change keyup blur", function(e){
        validateUI(true);
    });

    $('input').bind("focus", function(e){
        var $input = $(this), $formRow = $input.closest('.form-group');
        clsRowError($formRow);
    });

    // actions
    $("[data-action=get-checkcode]").click(function(e){
        if (!canGetCheckcode()) {
            return alert('请确保以上信息填写完整');
        }
        var $this = $(this), timer, countdown = 60;
        $this.addClass('disabled');
        var phone = getPhone();
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
