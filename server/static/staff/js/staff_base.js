/**
 * Created by liumengjun on 3/30/16.
 */
$(function() {
  $('input.datetimeInput').datetimepicker({
    format: 'YYYY-MM-DD HH:mm',
    locale: 'zh-cn',
    showClear: true,
    showClose: true
  });
  $('input.dateInput').datetimepicker({
    format: 'YYYY-MM-DD',
    locale: 'zh-cn',
    showClear: true,
    showClose: true
  });
  $(document).ready(function() {
    // template 页面默认隐藏侧边栏, 这里显示出来, 可以避免闪烁
    $('#staff_menu').show();
    $('.mui-heading').each(function() {
      if ($(this).next().has('a:visible').length == 0) {
        $(this).hide();
      }
    });
  });
  $('#modPswd').click(function(e) {
    $('#modPswdForm')[0].reset();
    $("#modPswdModal").modal('show');
  });
  $('#modPswdSubmitBtn').click(function(e) {
    let rpswd = /^[\W\w]{6,32}$/,
        oldpswd = $('#oldpswd').val(),
        newpswd = $('#newpswd').val(),
        newpswd2 = $('#newpswd2').val();
    if (!oldpswd || !rpswd.test(newpswd)) {
      return alert('密码格式错误');
    }
    if (newpswd != newpswd2) {
      return alert('两次新密码不一样');
    }
    let data = {'action': 'modpswd', 'oldpswd': oldpswd, 'newpswd': newpswd};
    malaAjaxPost("/staff/auth/", data, function(result) {
      if (result) {
        if (result.ok) {
          alert('修改成功，请重新登录');
          $("#modPswdModal").modal('hide');
          $('#modPswdForm')[0].reset();
          location.reload();
        } else {
          alert(result.msg);
        }
      } else {
        alert(DEFAULT_ERR_MSG);
      }
    }, 'json', function(jqXHR, errorType, errorDesc) {
      let errMsg = errorDesc ? ('[' + errorDesc + '] ') : '';
      alert(errMsg + DEFAULT_ERR_MSG);
    });
  });
});