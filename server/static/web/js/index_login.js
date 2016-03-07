/**
 * Created by caoyawen on 16/3/4.
 */
$(
    function(){
        console.log("OW-5");
        var input_phone = $("#phone_input");
        input_phone.blur(function(){
            CheckInput.check();
        });
        var sms_button = $("#sms_get");
        sms_button.click(function(){
            TimeEvent.start();
            var phone_val = _.extend(_.clone(InputVal), {element: "phone_input"});
            if(CheckInput.check() == true){
                $.post("/api/v1/sms", {action:"send", phone:phone_val.val()},
                    function(data){
                        console.log(data);
                    }
                ).fail(function(){
                    OutputVal.setVal("网络出现故障");
                });
            }
        });
        var verify_button = $("#verify_button");
        verify_button.click(function(){
            var phone_val = _.extend(_.clone(InputVal), {element: "phone_input"});
            var sms_val = _.extend(_.clone(InputVal), {element: "sms_input"});
            if(CheckAgree.check() == true){
                $.post("/teacher/verify_sms_code/", {phone:phone_val.val(), code:sms_val.val()},
                    function(data){
                        if(data.result == false){
                            OutputVal.setVal(data.msg);
                        }else{
                            var jump_url = data.url;
                            window.location.href = data.url;
                        }
                    }
                ).fail(function(){
                    OutputVal.setVal("网络出现故障");
                });
            }
            phone_val.val()
        });
        OutputVal.clearVal();
    }
);

function CheckPhoneFunction(){
    var phone_code = $("#phone_input").val();
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

function BlankPhone(){
    if (window.can_check_phone_empty == true) {
        var invalid_phone_number = $("#invalid-phone-number");
        invalid_phone_number.attr("attrHidden", "true");
        var need_phone_number = $("#need-phone-number");
        need_phone_number.attr("attrHidden", "false");
        window.phone_ok = false;
    }
}

var InputVal = {
    element: "phone_input",
    val: function(){
        return $("#"+this.element).val()
    }
};

var OutputVal = {
    element: "error_output",
    setVal: function(val){
        var output = $("#"+this.element);
        var output_msg = $("#error_msg");
        output_msg.html(val);
        output.attr("attrHidden", false);
    },
    clearVal: function(){
        var output = $("#"+this.element);
        output.attr("attrHidden", true);
    }
};

//动态获取按钮
var DynamicButton = {
    element: "sms_get",

    setVal: function(val){
        var output = $("#"+this.element);
        output.html(val);
        output.prop("disabled", true);
    },
    restoreVal: function(){
        var output = $("#"+this.element);
        output.html("获取");
        output.prop("disabled", false);
    }
};

var CheckInput = {
    check: function(){
        try{
            this.is_valid();
        }catch(e) {
            this.output_error(e);
            return false;
        }
        this.error_element.clearVal();
        return true;
    },
    is_valid: function(){
        var val = this.element.val();
        if (val == ""){
            throw "电话号码不能为空";
        }
        if(checkMobile(val) != true){
            throw "电话号码不正确";
        }
    },
    output_error: function(msg){
        this.error_element.setVal(msg);
    },
    element: _.extend(_.clone(InputVal), {}),
    error_element: _.extend(_.clone(OutputVal), {})
};

var CheckAgree = _.extend(_.clone(CheckInput),{
    is_valid: function(){
        var check_input = $("#agree_checkbox");
        if (check_input.prop("checked") == false){
            throw "麻辣老师服务协议没有同意";
        }
    }
});

var TimeEvent = {
    duration: 60,
    start:function(){
        this.interval = setInterval((function(){
            this.tick += 1;
            if(this.tick >= this.duration){
                clearInterval(this.interval);
                this.interval = undefined;
                this.tick = 0;
                DynamicButton.restoreVal();
            }else{
                DynamicButton.setVal(this.rest_time() + "秒");
            }
        }).bind(this), 1000);
    },
    tick: 0,
    rest_time: function(){
        return this.duration - this.tick;
    }
};

