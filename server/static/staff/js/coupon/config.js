var pagedefaultErrMsg = '请求失败,请稍后重试,或联系管理员!';
$(function(){
  $('#couponType').change(function(){
    if(this.value == 'give'){
      $('.config-coupon-new').hide();
      $('.config-coupon-give').show();
    }else if(this.value == 'new'){
      $('.config-coupon-give').hide();
      $('.config-coupon-new').show();
    }
  });
  $('input[name=validatedStart], input[name=expiredAt]').datetimepicker({
      format: 'YYYY-MM-DD',
      locale: 'zh-cn'
  });
  $('[data-action=integer]').keyup(function(){
    if(this.value.length==1){
      this.value=this.value.replace(/[^1-9]/g,'');
    }else{
      this.value=this.value.replace(/\D/g,'');
    }
  });
  $('[data-action=integer]').bind("input propertychange change", function(){
    var vl = this.value;
    if(vl.length > 5){
      this.value = vl.substring(0, 5);
    }
  });
  $('input').bind("input propertychange change", function(){
    var vl = this.value;
    if(vl.length > 50){
      this.value = vl.substring(0, 50);
    }
  });
  $('[data-action=remove-rule]').click(function(e){
    var delObj = $(e.target).closest(".form-group");
    var firstObj = $('[name=configRuleCont]:first');
    if(firstObj[0] == delObj[0]){
      delObj.find('input').val('');
    }else{
      delObj.remove();
    }
  });
  $('[data-action=add-more-rule]').click(function(){
    var lasObj = $('[name=configRuleCont]:last');
    var newObj = lasObj.clone(true);
    newObj.find('label').html('');
    newObj.find('input').val('');
    lasObj.after(newObj);
  });
  $('[data-action=save]').click(function(){
    var rules = [], validatedStart = null, expiredAt = null, mini_course_count = null, amount = null, parent_phone = null, couponType = null, opened = 0;
    _.each($('[name=couponRule]'), function(item){
      rules[rules.length] = $(item).val();
    });
    couponType = $('#couponType').val();
    if(couponType == 'new'){
      opened = $("input[type='radio']:checked").val();
      if(typeof opened == 'undefined'){
        opened = '0';
      }
      validatedStart = $('#validatedStart_new').val();
      expiredAt = $('#expiredAt_new').val();
      mini_course_count = $('#mini_course_count').val();
      amount = $('#amount').val();
    }else if(couponType == 'give'){
      validatedStart = $('#validatedStart_give').val();
      expiredAt = $('#expiredAt_give').val();
      mini_course_count = $('#mini_course_count_give').val();
      amount = $('#amount_give').val();
      parent_phone = $('#parent_phone').val();
      if(parent_phone.trim().length == 0){
        alert('请输入家长电话号码！');
        return false;
      }
    }

    if(Number(mini_course_count) <= 0){
      alert('请输入正确课时！');
      return false;
    }
    if(Number(amount) <= 0){
      alert('请输入正确代金券金额！');
      return false;
    }

    var params = {
      'opened': opened,
      'couponType': couponType,
      'parent_phone': parent_phone,
      'amount': amount,
      'mini_course_count': mini_course_count,
      'expiredAt': expiredAt,
      'validatedStart': validatedStart,
      'couponName': '赠送奖学金',
      'couponRules': JSON.stringify(rules)
    };

    $.post("/staff/coupon/config/", params, function(result){
        if(result){
          if(result.ok){
            alert("保存成功");

            if(couponType == 'give'){
              $('#validatedStart_give').val("");
              $('#expiredAt_give').val("");
              $('#mini_course_count_give').val("");
              $('#amount_give').val("");
              $('#parent_phone').val("");
            }
          }else if(result.code == '-1'){
            alert('家长不存在，请输入正确的家长手机号!');
          }else{
            alert(result.msg);
          }
        }else{
          alert(pagedefaultErrMsg);
        }
    }, 'json').fail(function(){
      $('#complaintModal').modal('hide');
      alert(pagedefaultErrMsg);
    });
  });
});
