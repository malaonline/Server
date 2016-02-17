/**
 * Created by walter-wu on 16/2/18.
 */
var pagedefaultErrMsg = '请求失败,请稍后重试,或联系管理员!';
$(function(){
  $('input.datetimeInput').datetimepicker({
      format: 'YYYY-MM-DD',
      locale: 'zh-cn'
  });
});