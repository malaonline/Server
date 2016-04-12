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
