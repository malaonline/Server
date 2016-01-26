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
    $('.img-edit input[type=file]').change(function(e){
        var ele = e.target, $ele = $(ele);
        var $editBox = $ele.closest(".img-edit"), $uploadBox = $editBox.find(".img-upload-box");
        $editBox.find('.help-block').hide();
        var imtUrl = getObjectURL(ele.files[0]);
        var $previewBox = $uploadBox.find(".img-preview-box");
        $previewBox.find('img').attr("src", imtUrl);
        $previewBox.show();
        return true;
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

    // audio file changed event
    $('.media-edit input[type=file]').change(function(e){
        var ele = e.target, $ele = $(ele), $editBox = $ele.closest(".media-edit");
        $editBox.find('.help-block').hide();
        var type='', file = ele.files[0];
        if ($editBox.hasClass('audio-edit')) {
            type = 'audio';
        } else if ($editBox.hasClass('video-edit')) {
            type = 'video';
        }
        var $uploadBox = $editBox.find("."+type+"-upload-box");
        var mediaUrl = getObjectURL(file);
        var $previewBox = $uploadBox.find(type+".preview");
        $previewBox.attr("src", mediaUrl);
        $previewBox.show();
        var audio = $previewBox[0];
        //audio.play();
        function g(){isNaN(audio.duration) ? requestAnimationFrame(g):console.info("该歌曲的总时间为："+audio.duration+"秒")}
        requestAnimationFrame(g);
        return true;
    });
});
