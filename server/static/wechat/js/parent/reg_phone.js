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
function checkStatus(){
  if(checkMobile($('#phoneCode').val()) && $('#smsCode').val().length > 0){
    $('#doCheck').addClass('submit_btn_active');
  }else{
    $('#doCheck').removeClass('submit_btn_active');
  }
}
$('#smsCode, #phoneCode').bind('input propertychange', checkStatus);
$('.ext_btn_primary').click(function(){
  var itm = $('.ext_btn_primary');
  if(!itm.hasClass('ext_btn_disabled')){
    if(checkMobile($('#phoneCode').val()) && TimeEvent.interval == undefined){
      itm.addClass('ext_btn_disabled');
      TimeEvent.start();
      $('.ext_btn_primary').html('60秒');
      getSMSFromServer();
    }else{
      itm.removeClass('ext_btn_disabled');
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
    var phone_code = $('#phoneCode').val();
    $.post("/api/v1/sms",
      {action:"send", phone:phone_code},
      function(data){
      }
    );
}
//验证sms
function checkSMS(){
  var itm = $('.ext_btn_primary');
  var phone_code = $('#phoneCode').val();
  var sms_code = $('#smsCode').val();
  if(!checkMobile(phone_code)){
    itm.removeClass('ext_btn_disabled');
    return false;
  }
  if(!$('#doCheck').hasClass('submit_btn_active')){
    return false;
  }
  $('.msg-error').html('验证码错误');
  $('.msg-error').css('display', 'none');
  $.post("/wechat/add_openid/", {phone:phone_code, code:sms_code, openid: openid},
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
        }
      }else{
        console.log("验证码正确")
        if(teacherId == 'ONLY_REGISTER'){
            wx.closeWindow();
        }
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
