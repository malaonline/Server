$(function(){
  var defaultErrMsg = '请求失败,请稍后重试,或联系管理员!';
  var getObjectURL = function(file){
    var url = null;
    if(window.createObjectURL != undefined){
      url = window.createObjectURL(file);
    }else if(window.URL != undefined){
      url = window.URL.createObjectURL(file);
    }else if(window.webkitURL != undefined){
      url = window.webkitURL.createObjectURL(file);
    }
    return url;
  };
  var clearImgEditBox = function($editBox){
      $editBox.find('.img-box').hide().find('img').attr('src', '');
      $editBox.find('.img-preview-box').hide().find('img').attr('src', '');
      $editBox.find('input[type=file]').val('');
  };
  $('.image-view input[type=file]').change(function(e){
      var ele = e.target, $ele = $(ele);
      var $editBox = $ele.closest(".image-edit-box");
      var $uploadBox = $editBox.find(".img-upload-box");
      $editBox.find('.help-block').hide();
      var imtUrl = getObjectURL(ele.files[0]);
      var $previewBox = $uploadBox.find(".img-preview-box");
      $previewBox.find('img').attr("src", imtUrl);
      $previewBox.show();
      var type = $editBox.attr('for');
      if(type=='photo'){
          $editBox.find('input[name=photoId]').val('');
      }
      return true;
  });
  $('[data-action=add-more-photo]').click(function(){
      if($('.image-edit-box').length > 5){
        return;
      }
      var $photoEdit = $('.image-edit-box:last');
      var $newPhotoEdit = $photoEdit.clone(true);
      $newPhotoEdit.find('input[name=photoId]').val('');
      clearImgEditBox($newPhotoEdit);
      $photoEdit.after($newPhotoEdit);
  });
  $('[data-action=delete-photo]').click(function(e){
      var $this = $(this), $editBox = $this.closest('.image-edit-box');
      var count = $('.image-edit-box').length;
      if(count > 1){
          $editBox.remove();
      }else{
          $editBox.find('input[name=photoId]').val('');
          $editBox.find('input[name=schoolImgId]').val('');
          clearImgEditBox($editBox);
      }
  });
  $('[data-action=integer]').keyup(function(){
    if(this.value.length==1){
      this.value=this.value.replace(/[^1-9]/g,'');
    }else{
      this.value=this.value.replace(/\D/g,'');
    }
  });
  $('input').bind("input propertychange change", function(){
    var vl = this.value;
    if(vl.length > 50){
      this.value = vl.substring(0, 50);
    }
  });
  $('textarea').bind("input propertychange change", function(){
    var vl = this.value;
    if(vl.length > 200){
      this.value = vl.substring(0, 200);
    }
  });
  $('[name=phone]').bind("input propertychange change", function(){
    var vl = this.value;
    if(vl.length > 20){
      this.value = vl.substring(0, 20);
    }
  });
  $('#subBtn').click(function(){
    var fm = $('#editForm');
    schoolName = $('#schoolName');
    latitude = $('#latitude');
    longitude = $('#longitude');
    if(schoolName.val().trim().length == 0){
      alert("名字不能为空！");
      return false;
    }
    if(latitude.val().trim().length == 0){
      alert("纬度不能为空！\n参考地址：http://api.map.baidu.com/lbsapi/getpoint/index.html");
      return false;
    }
    if(longitude.val().trim().length == 0){
      alert("经度不能为空！\n参考地址：http://api.map.baidu.com/lbsapi/getpoint/index.html");
      return false;
    }

    showLoading();
    fm.ajaxSubmit({
        dataType: 'json',
        success: function(result){
            if(result){
                if(result.ok){
                    alert("保存成功");
                    location.href="/staff/schools";
                }else{
                    alert(result.msg);
                }
            } else {
                alert(defaultErrMsg);
            }
            hideLoading();
        },
        error: function(jqXHR, errorType, errorDesc){
            var errMsg = errorDesc?('['+errorDesc+'] '):'';
            alert(errMsg+defaultErrMsg);
            hideLoading();
        }
    });
    return false;
  });
  function checkSetLongLatValue(reg, obj){
    var str = obj.value;
    if(obj.value.charAt(obj.value.length-1) == '.'){
      str += "0";
    }
    if(str == ""){
      $(obj).attr("data-old", "");
      return;
    }
    if(!reg.test(str)){
      obj.value = $(obj).attr("data-old");
    }else{
      $(obj).attr("data-old", obj.value);
    }
  }
  $('#longitude').keyup(function(e){
    var reg = /^(\+|-)?(?:180(?:(?:\.0{1,6})?)|(?:[0-9]|[1-9][0-9]|1[0-7][0-9])(?:(?:\.[0-9]{1,6})?))$/;
    checkSetLongLatValue(reg, this);
  });
  $('#latitude').keyup(function(e){
    var reg = /^(\+|-)?(?:90(?:(?:\.0{1,6})?)|(?:[0-9]|[1-8][0-9])(?:(?:\.[0-9]{1,6})?))$/;
    checkSetLongLatValue(reg, this);
  });
});
