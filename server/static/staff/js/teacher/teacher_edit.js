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
        var type = $editBox.attr('for');
        if (type=='photo') {
            $editBox.find('input[name=photoId]').val(''); // 重传照片按新建处理
        }
        return true;
    });

    var clearImgEditBox = function($editBox) {
        $editBox.find('.img-box').hide().find('img').attr('src', '');
        $editBox.find('.img-preview-box').hide().find('img').attr('src', '');
        $editBox.find('input[type=file]').val('');
    };

    $('[data-action=delete-photo]').click(function(e){
        var $this = $(this), $editBox = $this.closest('.img-edit');
        var type = $editBox.attr('for');
        if (type=='avatar') {
            $editBox.find('input[name=toDeleteAvatar]').val('1');
            clearImgEditBox($editBox);
        } else if (type=='photo') {
            var count = $('.img-edit[for=photo]').length;
            if (count > 1) {
                $editBox.remove();
            } else {
                $editBox.find('input[name=photoId]').val('');
                clearImgEditBox($editBox);
            }
        }
    });

    $('[data-action=add-more-photo]').click(function(){
        var $photoEdit = $('.img-edit[for=photo]:last');
        var $newPhotoEdit = $photoEdit.clone(true);
        $newPhotoEdit.find('input[name=photoId]').val('');
        clearImgEditBox($newPhotoEdit);
        $photoEdit.after($newPhotoEdit);
    });

    // 提分榜
    $('#addHighscore').click(function(e){
        $('#addItemsModal').modal({backdrop: 'static', keyboard: false});
    });
    $('#ckHighScoreAll').change(function(e){
        var ck = this.checked;
        $('[name="highscore"]').each(function(){
            this.checked = ck;
        });
    });
    var validIsAllHsChk = function() {
        var $chkHS = $('input[name=highscore]');
        if ($chkHS.length==0){
            $('#ckHighScoreAll')[0].checked = false;
            return;
        }
        var allCk = true;
        $chkHS.each(function(){
            allCk = allCk && this.checked;
        });
        $('#ckHighScoreAll')[0].checked = allCk;
    };
    $('input[name=highscore]').click(function(e){
        validIsAllHsChk();
    });
    $('#doAddHighScore').click(function(e){
        var stname = $('#stname').val();
        var inscore = $('#inscore').val();
        var schoolname = $('#schoolname').val();
        var admittedTo = $('#admittedTo').val();

        if (stname == '' || inscore == '' || !/^\d+$/.test(inscore) || schoolname == '' || admittedTo == '') {
            alert("必须填写所有内容, 并确保辅导提分格式正确！");
            return false;
        }
        var $highScoreTable = $('#highScoreTable'), newSeq = $highScoreTable.data('newSeq');
        if (!newSeq) {
            newSeq = 1;
        }
        var newHsSeq = "newHS"+newSeq;
        var $newHsRow = $('<tr>'
                + '<td><input type="checkbox" name="highscore"/>'
                + '    <input type="hidden" name="highscoreId" value="'+newHsSeq+'"/>'
                + '</td>'
                + '<td>'+stname+'<input type="hidden" name="'+newHsSeq+'name" value="'+stname+'"/></td>'
                + '<td>'+inscore+'<input type="hidden" name="'+newHsSeq+'scores" value="'+inscore+'"/></td>'
                + '<td>'+schoolname+'<input type="hidden" name="'+newHsSeq+'from" value="'+schoolname+'"/></td>'
                + '<td>'+admittedTo+'<input type="hidden" name="'+newHsSeq+'to" value="'+admittedTo+'"/></td>'
            + '</tr>');
        $newHsRow.find('input[name=highscore]').click(function(e){
            validIsAllHsChk();
        });
        $highScoreTable.append($newHsRow);
        newSeq++;
        $highScoreTable.data('newSeq', newSeq);
        validIsAllHsChk();
        $("#addItemsModal input").val('');
        $("#addItemsModal").modal('hide');
    });
    $('#delHighscore').click(function(e){
        var $chkHS = $('input[name=highscore]:checked');
        if ($chkHS.length==0) {
            alert("请选择要删除的提分榜记录");
            return true;
        }
        $chkHS.each(function(){
            var $row = $(this).closest('tr');
            $row.remove();
        });
        validIsAllHsChk();
    });

    // 身份资质认证等
    $('[data-action=approve-cert]').click(function(e){
        var $editBox = $(this).closest('.img-edit');
        var $flagSpan = $editBox.find('.cert-verify-flag');
        $flagSpan.removeClass('False').addClass('True');
        $flagSpan.find('input').val('True');
    });
    var declineCert = function($editBox) {
        var $flagSpan = $editBox.find('.cert-verify-flag');
        $flagSpan.removeClass('True').addClass('False');
        $flagSpan.find('input').val('False');
    };
    $('[data-action=decline-cert]').click(function(e){
        var $editBox = $(this).closest('.img-edit');
        declineCert($editBox);
    });
    $('[data-action=delete-cert]').click(function(e){
        var $editBox = $(this).closest('.img-edit'), type = $editBox.attr('for');
        if (type=='otherCert') {
            var $editBoxList = $("#otherCertsList > .img-edit");
            if ($editBoxList.length>1) {
                $editBox.remove();
            } else {
                declineCert($editBox);
                clearImgEditBox($editBox);
                $editBox.find('input[name$=certName]').val('');
            }
            return false;
        }
        declineCert($editBox);
        clearImgEditBox($editBox);
        $editBox.find('input[name^=toDeleteCert]').val('1');
    });
    $('[data-action=add-more-cert]').click(function(){
        var $otherCertsList = $("#otherCertsList"), newSeq = $otherCertsList.data('newSeq');
        if (!newSeq) {
            newSeq = 1;
        }
        var $certEdit = $('.img-edit[for=otherCert]:last');
        var $newCertEdit = $certEdit.clone(true);
        declineCert($newCertEdit);
        clearImgEditBox($newCertEdit);
        var oldCertId = $certEdit.find('input[name=certOtherId]').val(), newCertId = 'newCert'+newSeq;
        $newCertEdit.find('input[name=certOtherId]').val(newCertId);
        $newCertEdit.find('input[name='+oldCertId+'certName]').attr('name', newCertId+'certName').val('');
        $newCertEdit.find('input[name='+oldCertId+'certOk]').attr('name', newCertId+'certOk');
        $newCertEdit.find('input[name='+oldCertId+'certImg]').attr('name', newCertId+'certImg');
        $certEdit.after($newCertEdit);
        newSeq++;
        $otherCertsList.data('newSeq', newSeq);
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
        var $achieveList = $("#achieveList"), newSeq = $achieveList.data('newSeq');
        if (!newSeq) {
            newSeq = 1;
        }
        var $imgEdit = $('.img-edit[for=achieve]:last');
        var $newImgEdit = $imgEdit.clone(true);
        clearImgEditBox($newImgEdit);
        var oldId = $imgEdit.find('input[name=achieveId]').val(), newId = 'new'+newSeq;
        $newImgEdit.find('input[name=achieveId]').val(newId);
        $newImgEdit.find('input[name='+oldId+'achieveName]').attr('name', newId+'achieveName').val('');
        $newImgEdit.find('input[name='+oldId+'achieveImg]').attr('name', newId+'achieveImg');
        $imgEdit.after($newImgEdit);
        newSeq++;
        $achieveList.data('newSeq', newSeq);
    });

    $('[data-action=delete-achieve]').click(function(e){
        var $editBox = $(this).closest('.img-edit')
        var $editBoxList = $("#achieveList > .img-edit");
        if ($editBoxList.length>1) {
            $editBox.remove();
        } else {
            $editBox.find('input[name$=achieveName]').val('');
            clearImgEditBox($editBox);
        }
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
