$('.ext_btn_primary').click(function(){
  var itm = $('.ext_btn_primary');
  if(!itm.hasClass('ext_btn_disabled')){
    if(checkMobile($('#phoneCode').val())){
      itm.addClass('ext_btn_disabled');
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
function checkPhone(){

}
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
  $.post("/wechat/add_openid/", {phone:phone_code, code:sms_code, openid: openid},
    function(data){
      console.log(data);
      if(data.result == false){
        //    验证码错误
        console.log("验证码错误")
      }else{
        console.log("验证码正确")
        if(nextpage != "None"){
          location.href = nextpage;
        }
      }
    }
  );
}
