function showToast(txt){
  var $toast = $('#toast');
  if ($toast.css('display') != 'none'){
    return;
  }
  $('.weui_toast').html(txt);
  $toast.show();
  setTimeout(function(){
    $toast.hide();
  }, 1500);
}
function getStuName() {
  return $.trim($('#stuName').val());
}
function checkStuName(stu_name) {
    var stuName = stu_name || getStuName();
    if (!stuName) {
        return '请输入学生姓名';
    }
    if (stuName.length > 4) {
        return '姓名不能多余4个汉字';
    }
    return 0;
}
function checkStatus(){
  var nameOk = checkStuName()==0;
  if(nameOk && checkMobile($('#phoneCode').val()) && $('#smsCode').val().length > 0){
    $('#doCheck').addClass('submit_btn_active');
  }else{
    $('#doCheck').removeClass('submit_btn_active');
  }
}
$('#smsCode, #phoneCode, #stuName').bind('input propertychange', checkStatus);
$('.ext_btn_primary').click(function(){
  var itm = $('.ext_btn_primary');
  if(!itm.hasClass('ext_btn_disabled')){
    var ckName = checkStuName();
    if((ckName==0) && checkMobile($('#phoneCode').val()) && TimeEvent.interval == undefined){
      itm.addClass('ext_btn_disabled');
      getSMSFromServer();
    }else{
      itm.removeClass('ext_btn_disabled');
      if (ckName) {
        return showToast(ckName);
      }
      showToast('请输入正确手机号');
    }
  }
});
$('#doCheck').click(function(){
  checkSMS();
});
$('#usePolic').click(function(){
  var $dialog = $('#usePolicContent');
  $dialog.show();
  $('.weui_dialog_bd').css('height', $(window).height()-45);
  if(!window.isLoadPolicy){
    $.post("/wechat/policy/",null,
      function(data){
        if(data.result){
          $('.weui_dialog_bd').html(JSON.parse(data.policy));
          window.isLoadPolicy = true;
        }
      }
    );
  }
  $dialog.find('.weui_btn_dialog').one('click', function(){
    $dialog.hide();
  });
});
function checkMobile(phone_val){
    var pattern=/(^(([0\+]\d{2,3}-)?(0\d{2,3})-)(\d{7,8})(-(\d{3,}))?$)|(^0{0,1}1[3|4|5|6|7|8|9][0-9]{9}$)/;
    return !!pattern.test(phone_val);
}
//得到sms
function getSMSFromServer(){
    var $btn = $('.ext_btn_primary');
    var phone_code = $('#phoneCode').val();
    $.ajaxSettings.error = function(){ // in zepto.js
        showToast("网络出现故障");
        $btn.removeClass('ext_btn_disabled');
    };
    $.post("/api/v1/sms",
      {action:"send", phone:phone_code},
      function(data){
        if (data && data.sent) {
            TimeEvent.start();
        } else {
            showToast("请稍后重试或联系客服");
            $btn.removeClass('ext_btn_disabled');
        }
      }
    );
}
//验证sms
function checkSMS(){
  var itm = $('.ext_btn_primary');
  var phone_code = $('#phoneCode').val();
  var sms_code = $('#smsCode').val();
  var stu_name = getStuName();
  if(!(checkStuName(stu_name)==0) && !checkMobile(phone_code)){
    itm.removeClass('ext_btn_disabled');
    return false;
  }
  if(!$('#doCheck').hasClass('submit_btn_active')){
    return false;
  }
  $('.msg-error').html('验证码错误');
  $('.msg-error').css('display', 'none');
  $.post("/wechat/add_openid/", {phone:phone_code, code:sms_code, openid: openid, 'name': stu_name},
    function(data){
      console.log(data);
      if(data.result == false){
        //    验证码错误
        $('.msg-error').css('display', 'block');
        if(data.code == '-1'){
          $('.msg-error').html('openid错误');
        }else if(data.code == '-2'){
          $('.msg-error').html('验证码错误');
        }else if(data.code == '-3'){
          showToast('该手机号已绑定其他微信号');
        }else if(data.code == '-4'){
          showToast('请输入学生姓名');
        }
      }else{
        console.log("验证码正确");
        if(nextpage != "None"){
          location.href = nextpage;
        }
      }
    }
  );
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
                var getMsgBtn = $('.ext_btn_primary');
                getMsgBtn.html("获取验证码");
                getMsgBtn.removeClass('ext_btn_disabled');
            }else{
                $('.ext_btn_primary').html(this.rest_time() + "秒");
            }
        }).bind(this), 1000);
    },
    tick: 0,
    rest_time: function(){
        return this.duration - this.tick;
    }
};
