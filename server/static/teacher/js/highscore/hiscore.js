$(function(){
  var hiscorepageDefaultErrMsg = '请求失败,请稍后重试,或联系管理员!';
  $('#admittedTo, #schoolname').bind("blur", function(){
    var vl = this.value;
    if(vl.length > 6){
      this.value = vl.substring(0, 6);
    }
  });
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
      var params = {'action': 'delete', 'ids': ids.join(',')};
      malaAjaxPost("/teacher/highscore/", params, function(result){
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
          } else {
            alert(hiscorepageDefaultErrMsg);
          }
      }, 'json', function(jqXHR, errorType, errorDesc){
          var errMsg = errorDesc?('['+errorDesc+'] '):'';
          alert(errMsg+hiscorepageDefaultErrMsg);
      });
    }
  });
  var isSaving = false;
  $('#saveNewItem').click(function(e){
    if (isSaving) return;
    isSaving = true;
    var $saveBtn = $(this);
    $saveBtn.addClass('disabled');
    var teacherId = $('#teacherId').val();
    var stname = $('#stname').val();
    var inscore = $('#inscore').val();
    var schoolname = $('#schoolname').val();
    var admittedTo = $('#admittedTo').val();

    if(stname == '' ||
      inscore == '' ||
      schoolname == '' ||
      admittedTo == ''
    ){
      alert("必须填写所有内容！");
      $saveBtn.removeClass('disabled');
      isSaving = false;
      return false;
    }

    if(schoolname.length > 6){
      schoolname = schoolname.substring(0, 6);
    }
    if(admittedTo.length > 6){
      admittedTo = admittedTo.substring(0, 6);
    }

    var params = {
      'action': 'add',
      'id': teacherId,
      'name': stname,
      'increased_scores': inscore,
      'school_name': schoolname,
      'admitted_to': admittedTo,
    };
    malaAjaxPost("/teacher/highscore/", params, function(result){
        if(result){
          if(result.ok){
            $('#addItemsModal').modal('hide');
            location.reload();
          }else{
            alert(result.msg);
          }
        }else{
          alert(hiscorepageDefaultErrMsg);
        }
      $saveBtn.removeClass('disabled');
      isSaving = false;
    }, 'json', function(jqXHR, errorType, errorDesc){
      var errMsg = errorDesc?('['+errorDesc+'] '):'';
      $('#addItemsModal').modal('hide');
      alert(errMsg+hiscorepageDefaultErrMsg);
      $saveBtn.removeClass('disabled');
      isSaving = false;
    });
  });
  $('#addHighscore').click(function(e){
    $('#addItemsModal').modal({backdrop: 'static', keyboard: false});
  });
});
