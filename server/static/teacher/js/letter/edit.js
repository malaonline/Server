$(function(){
  $('#title').bind("input propertychange change", function(){
    var vl = this.value;
    if(vl.length > 100){
      this.value = vl.substring(0, 100);
    }
  });
  $('#content').bind("input propertychange change", function(){
    var vl = this.value;
    if(vl.length > 1000){
      this.value = vl.substring(0, 1000);
    }
  });
});
var letterIsSending = false;
function letterSend(o){
  if(letterIsSending){
    return;
  }
  var titleVal = $('#title').val();
  var contentVal = $('#content').val();
  var emsg = null;
  if(titleVal.trim().length == 0){
    emsg = '标题不能为空';
  }
  if(contentVal.trim().length == 0){
    emsg = !emsg ? '内容不能为空' : emsg + '\r内容不能为空';
  }
  if(emsg){
    alert(emsg);
    return false;
  }
  letterSetSendingView(o);
  var params = {
    'title': titleVal.trim(),
    'content': contentVal.trim()
  };
  $.post(document.location.href, params, function(result){
      if(result){
        if(result.ok){
          console.log(result);
          alert("保存成功");
          $('.form-horizontal').css('display', 'none');
          $('.action-btn').css('display', 'none');
          $('#viewLetter').attr('href', $('#viewLetter').attr('href')+result.id);
          $('.letter-send-ok').css('display', 'block');
        }else{
          if(result.code == -1){
            alert('家长不存在！');
          }else if(result.code == -2){
            alert('已经存在邮件，请返回查看！');
            alert(result.id);
          }else{
            alert(result.msg);
          }
        }
      }else{
        alert(pagedefaultErrMsg);
      }
      $('.submit-action').css('display', 'none');
  }, 'json').fail(function(){
    $('.submit-action').css('display', 'none');
  });
}
function letterDocancle(href){
  document.location.href = href;
}
function letterSetSendingView(o){
  $('.submit-action').css('display', 'block');
  letterIsSending = true;
  $(o).attr('disabled', true);
  $('#title').attr('disabled', true);
  $('#content').attr('disabled', true);
}
