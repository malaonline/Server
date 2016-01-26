/**
 * Created by liumengjun on 1/15/16.
 */
$(function(){
    var getObjectURL = function(file) {
      var url = null;
      if (window.createObjectURL != undefined) {
        url = window.createObjectURL(file);
      } else if (window.URL != undefined) {
        url = window.URL.createObjectURL(file);
      } else if (window.webkitURL != undefined) {
        url = window.webkitURL.createObjectURL(file);
      }
      return url;
    };
    // input file changed event
    $('input[type=file]').change(function(e){
        var ele = e.target, $ele = $(ele);
        var $editBox = $ele.closest(".img-edit"), $uploadBox = $editBox.find(".img-upload-box");
        $editBox.find('.help-block').hide();
        var imgType = ["gif", "jpeg", "jpg", "bmp", "png"];
        var flag = validImgFile();
        if (!flag) {
          return false;
        }
        var imtUrl = getObjectURL(ele.files[0]);
        var $previewBox = $uploadBox.find(".img-preview-box");
        $previewBox.find('img').attr("src", imtUrl);
        $previewBox.show();
        return true;

        // valid image properties
        function validImgFile() {
          if (!ele.value || !ele.files) {
            return false;
          }
          //验证上传文件格式是否正确
          if (!RegExp("\.(" + imgType.join("|") + ")$", "i").test(ele.value.toLowerCase())) {
            $editBox.addClass('has-error');
            $editBox.find('.help-block').text('选择图片类型错误').show();
            this.value = "";
            return false;
          }
          return true;
        }
    });

    $('[data-action=add-more-photo]').click(function(){
        var $photoEdit = $('.img-edit[for=photo]:last');
        var $newPhotoEdit = $photoEdit.clone(true);
        $newPhotoEdit.find('.img-box img').attr('src','');
        $newPhotoEdit.find('.img-preview-box img').attr('src','');
        $newPhotoEdit.find('.img-preview-box').hide();
        $photoEdit.after($newPhotoEdit);
    });

    $('[data-action=add-more-cert]').click(function(){
        var $certEdit = $('.img-edit[for=otherCert]:last');
        var $newCertEdit = $certEdit.clone(true);
        $newCertEdit.find('input[name=cert_name]').val('');
        $newCertEdit.find('.img-box img').attr('src','');
        $newCertEdit.find('.img-preview-box img').attr('src','');
        $newCertEdit.find('.img-preview-box').hide();
        $certEdit.after($newCertEdit);
    });

});
