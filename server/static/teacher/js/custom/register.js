/**
 * Created by caoyawen on 16/1/5.
 */
$(
    function(){
        console.log("TW-1-1");
        var input_phone = $("#phoneNumber");
        window.can_check_phone_empty = false;
        //console.log(input_phone);

        //input_phone.keyup(check_phone_function);
        input_phone.blur(function(){
            window.can_check_phone_empty = true;
            checkPageStatus();
        });
        //自动填充是一个大坑,不同浏览器事件不同,甚至没有事件触发,比如chrome,所以周期查询是目前最好的解决办法
        BlankPhone();

        var get_sms_code_button = $("#get-sms-code");
        get_sms_code_button.click(function(eventObject){
            eventObject.preventDefault();
            DisableGetSMSButton(true);
            TimeEvent.start();
            SetSMSButtonText(TimeEvent.rest_time() + "秒");
            getSMSFromServer();
            ErrorOutputClear();
        });

        var agree_and_continue_button = $("#agree-and-continue");
        agree_and_continue_button.click(function(){
            setAgreeCheck(true);
            checkPageStatus();
        });

        $("#agree").change(checkPageStatus);

        setNextButtonDisable(true);

        $("#smsCode").keyup(checkPageStatus);

        checkPageStatus();

        $("#next-button").click(function(eventObject){
            eventObject.preventDefault();
            checkSMS();
        });

        //追加网络异常检测情况
        $(document).ajaxError(function(event, jqxhr, settings, thrownError){
            console.log(event);
            console.log(jqxhr);
            console.log(settings);
            console.log(thrownError);
            ErrorOutput("网络异常,请稍后再试");
        });
        //追加网络超时限制,最多2秒
        $.ajaxSetup({
            timeout:2000
        });

    }
);

function ErrorOutput(msg){
    var invalidPhoneNumber = $("#invalid-sms-code");
    invalidPhoneNumber.html("<i class='glyphicon glyphicon-remove-sign'></i>"+msg);
    invalidPhoneNumber.attr("attrHidden", false);
}

function ErrorOutputClear(){
    var invalidPhoneNumber = $("#invalid-sms-code");
    invalidPhoneNumber.attr("attrHidden", true);
}

function CheckPhoneFunction(){
    var phone_code = $("#phoneNumber").val();
    if (phone_code == ""){
        BlankPhone();
    }else{
        if (checkMobile(phone_code) == true){
            PhoneOK();
        }else{
            InvalidPhone();
        }
    }
}


var TimeEvent = {
    duration: 60,
    start:function(){
        this.interval = setInterval((function(){
            this.tick += 1;
            if(this.tick >= this.duration){
                clearInterval(this.interval);
                this.interval = undefined;
                this.tick = 0;
                DisableGetSMSButton(false);
                SetSMSButtonText("获取");
            }else{
                SetSMSButtonText(this.rest_time() + "秒");
            }
        }).bind(this), 1000);
    },
    tick: 0,
    rest_time: function(){
        return this.duration - this.tick;
    }
};


function SetSMSButtonText(text){
    var get_sms_code_button = $("#get-sms-code");
    get_sms_code_button.html(text);
}

//是否禁止获取按钮
function DisableGetSMSButton(disable){
    var get_sms_code_button = $("#get-sms-code");
    get_sms_code_button.prop("disabled", disable);
}

//无效手机号码
function InvalidPhone(){
    var invalid_phone_number = $("#invalid-phone-number");
    invalid_phone_number.attr("attrHidden", "false");
    var need_phone_number = $("#need-phone-number");
    need_phone_number.attr("attrHidden", "true");
    window.phone_ok = false;
}

//手机号码为空
function BlankPhone(){
    if (window.can_check_phone_empty == true) {
        var invalid_phone_number = $("#invalid-phone-number");
        invalid_phone_number.attr("attrHidden", "true");
        var need_phone_number = $("#need-phone-number");
        need_phone_number.attr("attrHidden", "false");
        window.phone_ok = false;
    }
}

//手机号码正常
function PhoneOK(){
    var invalid_phone_number = $("#invalid-phone-number");
    invalid_phone_number.attr("attrHidden", "true");
    var need_phone_number = $("#need-phone-number");
    need_phone_number.attr("attrHidden", "true");
    window.phone_ok = true;
}

function setNotifyLabel(need_phone_number, invalid_phone_number){
    $("#need-phone-number").attr("attrHidden", need_phone_number);
    $("#invalid-phone-number").attr("attrHidden", invalid_phone_number);
}

//设置阅读同意的钩
function setAgreeCheck(check){
    $("#agree").prop("checked", check)
}

//设置下一步按钮的状态
function setNextButtonDisable(disable){
    $("#next-button").prop("disabled", disable);
}

function checkPageStatus(){
    var ret_val = false;
    CheckPhoneFunction();
    // 检查nextbutton
    if (IsPhoneCodeValid() == false || IsSMSCodeValid() == false || IsAgree() == false){
        setNextButtonDisable(true);
        ret_val = false;
    }else{
        setNextButtonDisable(false);
        ret_val = true;
    }
    // 检查获取按钮
    if (TimeEvent.interval == undefined){
        //只有Time没有运行的时候才会调用
        if (IsPhoneCodeValid() == false){
            DisableGetSMSButton(true);
        }else{
            DisableGetSMSButton(false);
        }
    }
    return ret_val;
}

function IsAgree(){
    return $("#agree").prop("checked");
}

function IsPhoneCodeValid(){
    if (window.phone_ok == false){
        return false;
    }else{
        return true;
    }
}

function IsSMSCodeValid(){
    var sms_code = $("#smsCode").val();
    if (sms_code == ""){
        return false;
    }else{
        return true;
    }
}

function getPhoneCode(){
    return $("#phoneNumber").val();
}

//得到sms
function getSMSFromServer(){
    var phone_code = getPhoneCode();
    $.post("/api/v1/sms", {action:"send", phone:phone_code},
    function(data){
        console.log(data);
    });
}

function getSMSVal(){
    return $("#smsCode").val();
}

//验证sms
function checkSMS(){
    //先做一次全页面检查
    if(checkPageStatus() == false){
        return false;
    }

    var phone_code = getPhoneCode();
    var sms_code = getSMSVal();
    console.log(window.location.href);
    var post_url = window.location.href;
    //var post_url = "/teacher/verify_sms_code/";
    $.post(post_url, {phone:phone_code, code:sms_code},
        function(data){
            console.log(data);
            if(data.result == false){
            //    验证码错误
                console.log("验证码错误");
                var invalidPhoneNumber = $("#invalid-sms-code");
                invalidPhoneNumber.html("<i class='glyphicon glyphicon-remove-sign'></i>"+data.msg);
                invalidPhoneNumber.attr("attrHidden", false);

            }else{
            //    验证码正确
                var jump_url = data.url;

                window.location.href = data.url;
            }
        }
    );
}

