$(function(){
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
          $editBox.find('input[name=photoId]').val(''); // 重传照片按新建处理
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
          clearImgEditBox($editBox);
      }
  });
});
