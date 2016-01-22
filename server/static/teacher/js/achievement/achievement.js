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
        var $uploadBox = $ele.closest(".img-upload-box");
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
            $uploadBox.addClass('has-error');
            $uploadBox.find('.help-block').text('选择图片类型错误');
            this.value = "";
            return false;
          }
          return true;
        }
    });
    //form取消操作
    $("#achieveEditForm [data-action=cancel]").click(function(e){
        var $form = $('#achieveEditForm');
        $form[0].reset();
        $form.find('.img-preview-box img').attr('src', '');
        $form.find('.img-preview-box').hide();
    });
    var defaultErrMsg = '请求失败,请稍后重试,或联系管理员!';
    //form保存操作
    $("#achieveEditForm [data-action=save]").click(function(e){
        var $form = $('#achieveEditForm');
        $form.ajaxSubmit({
            dataType: 'json',
            success: function(result){
                if (result) {
                    if (result.ok) {
                        location.href = '/teacher/achievement';
                    } else {
                        alert(result.msg);
                    }
                    return;
                }
                alert(defaultErrMsg);
            },
            error: function(e){
                alert(defaultErrMsg);
            }
        });
    });
});
