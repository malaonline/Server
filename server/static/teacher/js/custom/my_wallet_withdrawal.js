/**
 * Created by caoyawen on 16/2/25.
 */
$(
    function () {
        $('[data-toggle="tooltip"]').tooltip();
        var get_sms_button = $('#get_sms');
        get_sms_button.click(function(){
            DisableGetSMSButton(true);
            TimeEvent.start();
            SetSMSButtonText(TimeEvent.rest_time() + "秒");
            getSMSFromServer();
            var output_msg = "验证码已经发送至手机"+ window.phone.code + ",如未收到验证码,请60s后重试!";
            OutputInfo(output_msg);
        });
        var next_page = $("#next_page");
        next_page.click(function(){
            console.log("next page");
            var sms_code = $("#sms_code");
            $.post("/teacher/verify_login_sms_code/", {code: sms_code.val()},
                function(data){
                    if (data.verify == true){
                        window.location.href = data.url;
                    }else{
                        console.log(data.msg);
                        OutputInfo(data.msg);
                    }
                    console.log(data);
                });
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
                SetSMSButtonText("获得验证码");
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

//是否禁止获取按钮
function DisableGetSMSButton(disable){
    var get_sms_code_button = $("#get_sms");
    get_sms_code_button.prop("disabled", disable);
}

function SetSMSButtonText(text){
    var get_sms_code_button = $("#get_sms");
    get_sms_code_button.html(text);
}

//得到sms
function getSMSFromServer(){
    $.post("/teacher/generate_sms/",
    function(data){
        console.log(data);
    });
}

//输出当前页面的信息
function OutputInfo(msg){
    var output_info = $("#output_info");
    output_info.html(msg);
}