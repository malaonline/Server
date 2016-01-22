$(function(){
  $('#checkboxSelectAll').change(function(e){
    var ck = false;
    if($('#checkboxSelectAll').is(':checked')){
      ck = true;
    }
    $('[name="checkboxSelect"]').each(function(){
      $(this).prop('checked', ck ? 'checked' : false);
    });
  });

  function checkIdInList(id, lst){
    for(var i=0; i<lst.length; i++){
      if(lst[i] == id){
        return true;
      }
    }
    return false;
  }

  $('#delHighscore').click(function(e){
    var ids = [];
    var allItems = $('[name="checkboxSelect"]');
    for(var i=0; i < allItems.length; i++){
      var item = $(allItems[i]);
      if(item.prop('checked')){
        ids[ids.length] = item.prop('value');
      }
    }
    if(ids.length > 0){
      var defaultErrMsg = '请求失败,请稍后重试,或联系管理员!';
      var params = {'action': 'delete', 'ids': ids.join(',')};
      $.post( "/teacher/highscore/", params, function(result){
          if(result){
              if(result.ok){
                  var allItems = $('[name="checkboxSelect"]');
                  for(var i=0; i < allItems.length; i++){
                    var item = $(allItems[i]);
                    if(checkIdInList(item.prop('value'), ids)){
                      item.parent().parent().remove();
                    }
                  }
              }else{
                  alert(result.msg);
              }
              return;
          }
          alert(defaultErrMsg);
      }, 'json').fail(function(){
          alert(defaultErrMsg);
      });
    }
  });

});
