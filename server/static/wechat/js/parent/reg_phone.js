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
      var $toast = $('#toast');
      if ($toast.css('display') != 'none'){
        return;
      }
      $toast.show();
      setTimeout(function(){
        $toast.hide();
      }, 1500);
    }
  }
});
$('#doCheck').click(function(){
  checkSMS();
});
$('#usePolic').click(function(){
  location.href = policyHref;
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
        }
      }else{
        console.log("验证码正确")
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
