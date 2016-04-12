var pagedefaultErrMsg = '请求失败,请稍后重试,或联系管理员!';
$(function(){
  $('#saveComplaintItem').click(function(e){
    var complaintContent = $('#complaintContent').val();
    var complaintId = $('#complaintId').val();
    var timeslotId = $('#timeslotId').val();

    if(
      complaintContent == ''
    ){
      alert("必须填写投诉内容！");
      return false;
    }

    var params = {
      'action': 'saveComplaint',
      'complaintId': complaintId,
      'timeslotId': timeslotId,
      'complaintContent': complaintContent
    };
    $.post("/staff/school/timeslot", params, function(result){
        if(result){
          if(result.ok){
            $('#complaintModal').modal('hide');
            location.reload();
          }else{
            alert(result.msg);
          }
        }else{
          alert(pagedefaultErrMsg);
        }
    }, 'json').fail(function(jqXHR, errorType, errorDesc){
      var errMsg = errorDesc?('['+errorDesc+'] '):'';
      $('#complaintModal').modal('hide');
      alert(errMsg+pagedefaultErrMsg);
    });
  });

  $('#saveAttendanceItem').click(function(e){
    var attendanceId = $('#attendanceId').val();
    var timeslotId = $('#timeslotId').val();
    var attendanceValue = getAttendanceValue();

    var params = {
      'action': 'saveAttendace',
      'attendanceId': attendanceId,
      'timeslotId': timeslotId,
      'attendanceValue': attendanceValue
    };
    $.post("/staff/school/timeslot", params, function(result){
        if(result){
          if(result.ok){
            $('#attendanceModal').modal('hide');
            location.reload();
          }else{
            alert(result.msg);
          }
        }else{
          alert(pagedefaultErrMsg);
        }
    }, 'json').fail(function(jqXHR, errorType, errorDesc){
      var errMsg = errorDesc?('['+errorDesc+'] '):'';
      $('#attendanceModal').modal('hide');
      alert(errMsg+pagedefaultErrMsg);
    });
  });
});
function doComplaint(timeslotId, complaintId, compContent){
  if(complaintId == ''){
    $('#complaintLegend').html('添加新的投诉');
  }else{
    $('#complaintLegend').html('修改投诉内容');
    if(compContent.length > 2){
      compContent = compContent.substring(1, compContent.length-1)
    }
  }
  $('#timeslotId').val(timeslotId);
  $('#complaintId').val(complaintId);
  $('#complaintContent').val(compContent);
  $('#complaintModal').modal({backdrop: 'static', keyboard: false});
}
function doAttendance(timeslotId, attendanceId, attendanceValue){
  $('#timeslotId').val(timeslotId);
  $('#attendanceId').val(attendanceId);
  $('#attendanceValue').val(attendanceValue);
  setAttendValue(attendanceValue, true);
  $('#attendanceModal').modal({backdrop: 'static', keyboard: false});
}
function setAttendValue(newVal, force){
  if(newVal == undefined || newVal == ''){
    $('[name="attendVal"]').removeClass('item-select');
  }else{
    var all = $('[name="attendVal"]');
    for(var i=0; i< all.length; i++){
      var itm = $(all[i]);
      if(itm.attr('value') == newVal && (force || !itm.hasClass('item-select'))){
        itm.addClass('item-select');
      }else{
        itm.removeClass('item-select');
      }
    }
  }
}
function getAttendanceValue(){
  var all = $('[name="attendVal"]');
  for(var i=0; i< all.length; i++){
    var itm = $(all[i]);
    if(itm.hasClass('item-select')){
      return itm.attr('value');
    }
  }
  return 'a';
}
