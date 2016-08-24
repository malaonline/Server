/**
 * Created by liumengjun on 1/15/16.
 */
$(function(){
    $("select[name=province]").change(function(e){
        var pro_id = $(this).val(), $city_sel = $("select[name=city]"), $dist_sel = $("select[name=district]");
        $city_sel.find('option:gt(0)').remove();
        $dist_sel.find('option:gt(0)').remove();
        if (!pro_id) return;
        malaAjaxGet('/api/v1/regions', {'action': 'sub-regions', 'sid': pro_id}, function(json){
            if (json && json.results) {
                for (var i in json.results) {
                    var reg = json.results[i];
                    $city_sel.append('<option value="'+reg.id+'">'+reg.name+'</option>');
                }
            }
        });
    });
    $("select[name=city]").change(function(e){
        var city_id = $(this).val(), $dist_sel = $("select[name=district]");
        $dist_sel.find('option:gt(0)').remove();
        if (!city_id) return;
        malaAjaxGet('/api/v1/regions', {'action': 'sub-regions', 'sid': city_id}, function(json){
            if (json && json.results) {
                for (var i in json.results) {
                    var reg = json.results[i];
                    $dist_sel.append('<option value="'+reg.id+'">'+reg.name+'</option>');
                }
            }
        });
    });
    $("select[name=region]").change(function(e){
        var region_id = $(this).val(), $schools_sel = $("select[name=schools]");
        $schools_sel.find('option').remove();
        if (!region_id) return;
        malaAjaxGet('/api/v1/schools', {'region': region_id}, function(json){
            if (json && json.results) {
                for (var i in json.results) {
                    var sch = json.results[i];
                    $schools_sel.append('<option value="'+sch.id+'">'+sch.name+'</option>');
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
        malaAjaxGet('/staff/teachers/action/', {'action': 'get-subject-grades-range', 'sid': subjectId}, function(json){
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

    // 预览图片
    $('.img-edit img').click(function(e){
        var src = $(this).attr('src');
        var $modal = $("#imgModal");
        $modal.find('img').attr('src', src);
        $modal.modal();
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
            alert("必须填写所有内容, 并确保辅导提分为整数！");
            return false;
        }
        var $highScoreTable = $('#highScoreTable'), newSeq = $highScoreTable.data('newSeq');
        if (!newSeq) {
            newSeq = 1;
        }

        if(schoolname.length > 6){
          schoolname = schoolname.substring(0, 6);
        }
        if(admittedTo.length > 6){
          admittedTo = admittedTo.substring(0, 6);
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
    var hasImg = function($editBox) {
        var hasImg = !!$editBox.find('.img-box img').attr('src');
        var hasPreImg = !!$editBox.find('.img-preview-box img').attr('src');
        return hasImg || hasPreImg;
    };
    $('[data-action=approve-cert]').click(function(e){
        var $editBox = $(this).closest('.img-edit'), type = $editBox.attr('for');
        if (type=='idHeld') {
            if (!hasImg($editBox)) {
                alert("没有身份证手持照");
                return;
            }
            var $idFrontEditBox = $('.img-edit[for=idFront]');
            if (!hasImg($idFrontEditBox)) {
                alert("没有身份证正面照片");
                return;
            }
        } else if (type=='otherCert') {
            var certName = $.trim($editBox.find('input[name$=certName]').val());
            if (!certName) {
                alert("证书标题不能为空");
                return;
            }
        }
        if (!hasImg($editBox)) {
            alert("没有证书照片");
            return;
        }
        var $flagSpan = $editBox.find('.cert-verify-flag');
        $flagSpan.find('input').val('True');
        $editBox.removeClass('cert-fail').addClass('cert-ok');
    });
    var declineCert = function($editBox, fail) {
        var $flagSpan = $editBox.find('.cert-verify-flag');
        if (fail) {
            $flagSpan.find('input').val('Fail');
            $editBox.addClass('cert-fail');
        } else {
            $flagSpan.find('input').val('False');
            $editBox.removeClass('cert-fail');
        }
        $editBox.removeClass('cert-ok');
    };
    $('[data-action=decline-cert]').click(function(e){
        var $editBox = $(this).closest('.img-edit');
        declineCert($editBox, true);
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

    // 初始化音视频
    $('.media-edit').each(function(){
        var $editBox = $(this), type = $editBox.attr('for');
        var $uploadBox = $editBox.find("."+type+"-upload-box");
        var $view = $uploadBox.find(type+".view");
        var src = $uploadBox.attr('src');
        $view.attr('src', src);
        $view[0].load();
        if (type=='audio') {
            $view.css('display', 'inline-block');
        } else {
            $view.show();
        }
    });

    // audio file changed event
    $('.media-edit input[type=file]').change(function(e){
        var ele = e.target, $ele = $(ele), $editBox = $ele.closest(".media-edit");
        $editBox.find('.help-block').hide();
        var type = $editBox.attr('for'), file = ele.files[0];
        var $uploadBox = $editBox.find("."+type+"-upload-box");
        var $previewBox = $uploadBox.find(type+".preview");
        var mediaUrl = getObjectURL(file);
        $previewBox.attr("src", mediaUrl);
        if (type=='audio') {
            $previewBox.css('display', 'inline-block');
        } else {
            $previewBox.show();
        }
        var audio = $previewBox[0];
        //audio.play();
        function g(){isNaN(audio.duration) ? requestAnimationFrame(g):console.info("该歌曲的总时间为："+audio.duration+"秒")}
        requestAnimationFrame(g);
        return true;
    });

    $('[data-action=delete-media]').click(function(e){
        var $editBox = $(this).closest(".media-edit"), type = $editBox.attr('for');
        $editBox.find(type+".preview")[0].pause();
        $editBox.find(type+".preview").hide();
        $editBox.find(type+".view")[0].pause();
        $editBox.find(type+".view").attr('src', '');
        $editBox.find('input[name^=toDelete]').val('1');
        $editBox.find('input[type=file]').val('');
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
            $editBox.find('input[name=achieveId]').val('');
            $editBox.find('input[name$=achieveName]').val('');
            clearImgEditBox($editBox);
        }
    });

    var defaultErrMsg = '请求失败,请稍后重试,或联系管理员!';
    $('[data-action=submit]').click(function(e){
        var $this = $(this);
        $this.addClass('disabled');
        $teacherEditForm = $("#teacherEditForm");
        showLoading();
        $teacherEditForm.ajaxSubmit({
            dataType: 'json',
            success: function(result){
                if (result) {
                    if (result.ok) {
                        alert("保存成功");
                        location.href = listPageUrl;
                    } else {
                        alert(result.msg);
                        $this.removeClass('disabled');
                    }
                    return;
                }
                alert(defaultErrMsg);
                $this.removeClass('disabled');
                hideLoading();
            },
            error: function(jqXHR, errorType, errorDesc){
                var errMsg = errorDesc?('['+errorDesc+'] '):'';
                alert(errMsg+defaultErrMsg);
                $this.removeClass('disabled');
                hideLoading();
            }
        });
    });

    $('#admittedTo, #schoolname').bind("blur", function(){
      var vl = this.value;
      if(vl.length > 6){
        this.value = vl.substring(0, 6);
      }
    });
});
