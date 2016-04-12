var isSending = false;
function send(o){
  if(isSending){
    return;
  }
  $('.submit-action').css('display', 'block');
  isSending = true;
  $(o).attr('disabled', true);
}
function docancle(href){
  document.location.href = href;
}
