/**
 * Created by liumengjun on 1/15/16.
 */
$(function(){
    $("select[name=province]").change(function(e){
        var pro_id = $(this).val(), $city_sel = $("select[name=city]"), $dist_sel = $("select[name=district]");
        $.getJSON('/staff/teachers/action/', {'action': 'list-region', 'sid': pro_id}, function(json){
            if (json && json.list) {
                $city_sel.find('option:gt(0)').remove();
                $dist_sel.find('option:gt(0)').remove();
                for (var i in json.list) {
                    var reg = json.list[i];
                    $city_sel.append('<option value="'+reg.id+'">'+reg.name+'</option>');
                }
            }
        });
    });
    $("select[name=city]").change(function(e){
        var dist_id = $(this).val(), $dist_sel = $("select[name=district]");
        $.getJSON('/staff/teachers/action/', {'action': 'list-region', 'sid': dist_id}, function(json){
            if (json && json.list) {
                $dist_sel.find('option:gt(0)').remove();
                for (var i in json.list) {
                    var reg = json.list[i];
                    $dist_sel.append('<option value="'+reg.id+'">'+reg.name+'</option>');
                }
            }
        });
    });
    $('select[name=subject]').change(function(e){
        var subjectId = $(this).val();
        // 首先禁用所有grades
        var $allGrades = $('input[type=checkbox][name=grade]');
        $allGrades.each(function(){
            this.disabled = true;
        });
        $.getJSON('/staff/teachers/action/', {'action': 'get-subject-grades-range', 'sid': subjectId}, function(json){
            if (json && json.list) {
                $allGrades.each(function(){
                    var curGradeVal = this.value;
                    var found = _.find(json.list, function(obj){return obj==curGradeVal;});
                    this.disabled = !found;
                    if (found) {
                        $(this).closest('label').removeClass('disabled');
                    } else {
                        $(this).closest('label').addClass('disabled');
                    }
                });
            }
        });
    });
    $('input[type=checkbox][name=grade]').click(function(e){
        var $this = $(this);
        if ($this.attr('super')) {
            var superId = $this.attr('super');
            var isAllChecked = true;
            $('input[type=checkbox][name=grade][super='+superId+']').each(function(){
                isAllChecked = isAllChecked && this.checked;
            });
            $('input[type=checkbox][name=grade][value='+superId+']')[0].checked = isAllChecked;
        } else {
            var isChecked = $this.is(':checked');
            $('input[type=checkbox][name=grade][super='+$this.val()+']').each(function(){
                this.checked = isChecked;
            });
        }
    });
    $('[data-action=choose-tag]').click(function(e){
        var CHOSEN_FLAG = 'btn-success', MAX_TAGS_COUNT = 3;
        var $this = $(this), inInput = $this.find('input')[0], wasChosen = inInput.checked;
        if (wasChosen) {
            inInput.checked = false;
            $this.removeClass(CHOSEN_FLAG);
            return true;
        }
        var count = 0;
        $('input[type=checkbox][name=tag]').each(function(){
            if (this.checked) {
                count++;
            }
        });
        if (count >= MAX_TAGS_COUNT) {
            var $tagsBox = $this.closest('div[for=tags]').addClass('has-error'),
                $helpBlock = $tagsBox.find('.help-block').show();
            setTimeout(function(){
                $helpBlock.fadeOut();
                $tagsBox.removeClass('has-error');
            }, 800);
            return false;
        }
        inInput.checked = true;
        $this.addClass(CHOSEN_FLAG);
        return true;
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

    $('[data-action=add-more-achieve]').click(function(){
        var $imgEdit = $('.img-edit[for=achieve]:last');
        var $newImgEdit = $imgEdit.clone(true);
        $newImgEdit.find('input[name=achieve_name]').val('');
        $newImgEdit.find('.img-box img').attr('src','');
        $newImgEdit.find('.img-preview-box img').attr('src','');
        $newImgEdit.find('.img-preview-box').hide();
        $imgEdit.after($newImgEdit);
    });

    var defaultErrMsg = '请求失败,请稍后重试,或联系管理员!';
    $('[data-action=submit]').click(function(e){
        $teacherEditForm = $("#teacherEditForm");
        $teacherEditForm.ajaxSubmit({
            dataType: 'json',
            success: function(result){
                if (result) {
                    if (result.ok) {
                        alert("保存成功");
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
