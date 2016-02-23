$(function(){
  $('#couponType').change(function(){
    console.log(this);
    console.log(this.value);
    if(this.value == 'give'){

    }else if(this.value == 'new'){

    }
  });
  $('input[name=validatedStart], input[name=expiredAt]').datetimepicker({
      format: 'YYYY-MM-DD',
      locale: 'zh-cn'
  });
});
