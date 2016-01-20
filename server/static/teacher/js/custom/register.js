/**
 * Created by caoyawen on 16/1/5.
 */
$(
    function(){
        console.log("TW-1-1");
        var input_phone = $("#phoneNumber");
        //console.log(input_phone);
        input_phone.keyup(function(){
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
            checkPageStatus();
        });

        BlankPhone();

        var get_sms_code_button = $("#get-sms-code");
        get_sms_code_button.click(function(eventObject){
            eventObject.preventDefault();
            DisableGetSMSButton(true);
            TimeEvent.start();
            SetSMSButtonText(TimeEvent.rest_time() + "秒");
            getSMSFromServer();
        });

        var agree_and_continue_button = $("#agree-and-continue");
        agree_and_continue_button.click(function(){
            setAgreeCheck(true);
        });

        $("#agree").change(checkPageStatus);

        setNextButtonDisable(true);

        $("#smsCode").keyup(checkPageStatus);

        checkPageStatus();

        $("#next-button").click(function(eventObject){
            eventObject.preventDefault();
            checkSMS();
        });

    }
);

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
    var invalid_phone_number = $("#invalid-phone-number");
    invalid_phone_number.attr("attrHidden", "true");
    var need_phone_number = $("#need-phone-number");
    need_phone_number.attr("attrHidden", "false");
    window.phone_ok = false;
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

//检查手机号码的正则表达式
function checkMobile(phone_val){
    var pattern=/(^(([0\+]\d{2,3}-)?(0\d{2,3})-)(\d{7,8})(-(\d{3,}))?$)|(^0{0,1}1[3|4|5|6|7|8|9][0-9]{9}$)/;
    if(pattern.test(phone_val)) {
        return true;
    }else{
        return false;
    }
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
    // 检查nextbutton
    if (IsPhoneCodeValid() == false || IsSMSCodeValid() == false || IsAgree() == false){
        setNextButtonDisable(true);
    }else{
        setNextButtonDisable(false);
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
    var phone_code = getPhoneCode();
    var sms_code = getSMSVal();

    $.post("/teacher/verify_sms_code/", {phone:phone_code, code:sms_code},
        function(data){
            console.log(data);
            if(data.result == false){
            //    验证码错误
                console.log("验证码错误")
                var invalidPhoneNumber = $("#invalid-sms-code");
                invalidPhoneNumber.attr("attrHidden", false);
            }else{
            //    验证码正确
                var jump_url = data.url;

                window.location.href = data.url;
            }
        }
    );
}

