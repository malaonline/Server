/**
 * Created by liumengjun on 1/15/16.
 */
$(function(){
    var defaultErrMsg = '请求失败,请稍后重试,或联系管理员!';

    $("#achievementsList [data-action=delete-achievement]").click(function (e){
        var decided = confirm('确定要删除这个记录吗?');
        if (!decided) return false;
        var $item = $(this).closest('.list-group-item'), achieveId = $item.attr('achieveId');
        if (achieveId) {
            $.post( "/teacher/achievement/delete/"+achieveId, function( result ) {
                if (result) {
                    if (result.ok) {
                        $item.remove();
                    } else {
                        alert(result.msg);
                    }
                    return;
                }
                alert(defaultErrMsg);
            }, 'json').fail(function() {
                alert(defaultErrMsg);
            });
        }
    });

    $("#btnAddAchievement").click(function (e){
        var old_count = $("#achievementsList > .list-group-item").length;
        if (old_count >= parseInt(maxCount)) {
            alert('最多添加'+maxCount+'个特殊成果');
            return false;
        }
        location.href = $(this).data('url');
    });

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
        location.href = listPageUrl; // go back list page, do not reset form
    });
    var hasImg = function($editBox) {
        var hasImg = !!$editBox.find('.img-box img').attr('src');
        var hasPreImg = !!$editBox.find('.img-preview-box img').attr('src');
        return hasImg || hasPreImg;
    };
    //form保存操作
    $('#achieveEditForm').submit(function(e){
        return false;
    });
    $("#achieveEditForm [data-action=save]").click(function(e){
        var $form = $('#achieveEditForm');
        var title = $.trim($form.find('input[name=title]').val());
        if (!title) {
            alert('名称不能为空');
            return false;
        }
        if (title.length > 10) {
            alert('名称不能超过10个字');
            return false;
        }
        if (!hasImg($form.find('.img-upload-box'))) {
            return alert("请上传图片");
        }
        $form.ajaxSubmit({
            dataType: 'json',
            success: function(result){
                if (result) {
                    if (result.ok) {
                        location.href = listPageUrl;
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
