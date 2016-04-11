var pagedefaultErrMsg = '请求失败,请稍后重试,或联系管理员!';
$(function(){
  $('#teachingAgeAdd').click(function(e){
    $('#teachingAge').html(parseInt($('#teachingAge').html()) + 1);
  });
  $('#teachingAgeSub').click(function(e){
    var nv = parseInt($('#teachingAge').html()) - 1;
    nv = nv < 0 ? 0 : nv;
    $('#teachingAge').html(nv);
  });
  $('[name="tag-item"]').click(function(e){
    if(!$(e.target).hasClass('item-selected') && !checkFlag()){
      return false;
    }
    $(e.target).toggleClass('item-selected');
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
  $('#saveBtn').click(function(){
    if(!checkFlag(true) || !checkMinFlag()){
      return false;
    }
    var birthday_y = $('#birthday_y').val();
    var birthday_m = $('#birthday_m').val();
    var birthday_d = $('#birthday_d').val();
    var teachingAge = $('#teachingAge').html();
    var graduate_school = $('#graduate_school').val();
    var introduce = $('#introduce').val();
    introduce = $.trim(introduce);
    if (!introduce || introduce.length<10) {
        alert('自我介绍不能少于10个字！');
        return false;
    }

    var params = {
      'birthday_y': birthday_y,
      'birthday_m': birthday_m,
      'birthday_d': birthday_d,
      'teachingAge': teachingAge,
      'graduate_school': graduate_school,
      'introduce': introduce,
      'selectedTags': JSON.stringify(getSelectedTags()),
      'subclass': $('#subclass_input').html()
    };

    if(birthday_y <= 0 || birthday_m <= 0 || birthday_d <= 0){
      alert("请选择出生日期！");
      return false;
    }

    $.post("/teacher/basic_doc/", params, function(result){
        if(result){
          if(result.ok){
            alert("保存成功");
            location.href=nextPage;
          }else{
            alert(result.msg);
          }
        }else{
          alert(pagedefaultErrMsg);
        }
    }, 'json').fail(function(){
      $('#complaintModal').modal('hide');
      alert(pagedefaultErrMsg);
    });
  });

  //视图数据定义区
  window.primary_list = [
      // button的id名,button名称,是否被选中,是否可以显示
      ["primary_one", "小学一年级", false, true],
      ["primary_two", "小学二年级", false, true],
      ["primary_three", "小学三年级", false, true],
      ["primary_four", "小学四年级", false, true],
      ["primary_five", "小学五年级", false, true],
      ["primary_six", "小学六年级", false, true]];
  window.junior_list = [
      ["junior_one", "初一", false, true],
      ["junior_two", "初二", false, true],
      ["junior_three", "初三", false, true]];
  window.high_list = [
      ["high_one", "高一", false, true],
      ["high_two", "高二", false, true],
      ["high_three", "高三", false, true]
  ];
  var set_grade_select_by_html = function(grade_select_in_html, grade_list){
      for(i = 0; i < grade_select_in_html.length; i++){
          grade_list[i][2] = grade_select_in_html[i];
      }
  };
  if(window.grade_select_by_html != undefined){
      set_grade_select_by_html(window.grade_select_by_html[0], window.primary_list);
      set_grade_select_by_html(window.grade_select_by_html[1], window.junior_list);
      set_grade_select_by_html(window.grade_select_by_html[2], window.high_list);
  }

  //过滤看看哪些科目有哪些年级[名称, [小学], [初中], [高中]]
  window.subclass_mask_list = [
      ["数学", [[1,1,1,1,1,1], [1,1,1], [1,1,1]]],
      ["英语", [[1,1,1,1,1,1], [1,1,1], [1,1,1]]],
      ["语文", [[1,1,1,1,1,1], [1,1,1], [1,1,1]]],
      ["物理", [[0,0,0,0,0,0], [1,1,1], [1,1,1]]],
      ["化学", [[0,0,0,0,0,0], [1,1,1], [1,1,1]]],
      ["地理", [[0,0,0,0,0,0], [1,1,1], [1,1,1]]],
      ["历史", [[0,0,0,0,0,0], [1,1,1], [1,1,1]]],
      ["政治", [[0,0,0,0,0,0], [1,1,1], [1,1,1]]],
      ["生物", [[0,0,0,0,0,0], [1,1,1], [1,1,1]]]
  ];
  window.grand_mask = [];
  if($("#subclass_input").html() == ""){
      window.need_select_subclass = true;
  }else{
      window.need_select_subclass = false;
  }

  map_grand_button_mask(get_grand_mask($("#subclass_input").html()));
  render_grand_button_list();

  $(function(){
    $.ms_DatePicker({
      YearSelector: ".sel_year",
      MonthSelector: ".sel_month",
      DaySelector: ".sel_day",
      minYear: 1960
    });
  });

});

//获得已经选择的年级
function selected_grand(){
    var select_button = [];
    var _selected_grand = function(grand_list){
        _.each(grand_list, function(grand_item){
            if(grand_item[2] == true){
                select_button.push(grand_item[1]);
            }
        });
    };
    _selected_grand(window.primary_list);
    _selected_grand(window.junior_list);
    _selected_grand(window.high_list);
    return select_button;
}
//渲染年级按钮列表
function render_grand_button_list(){
    var grand_array = selected_grand();
    if(grand_array != undefined){
        var button_template = $("#grand_button_group").html();
        var grand_button_group = _.template(button_template, {variable: 'datas'})(grand_array);
        $("#grand_input").html(grand_button_group);
        if(window.need_select_subclass == false){
            enable_add_grand();
        }
    }
}

//Array添加一个方法
Array.prototype.remove = function() {
    var what, a = arguments, L = a.length, ax;
    while (L && this.length) {
        what = a[--L];
        while ((ax = this.indexOf(what)) !== -1) {
            this.splice(ax, 1);
        }
    }
    return this;
};

//允许添加授课年级
function enable_add_grand(){
    $("#add_grand").prop("disabled", false);
}

//提供渲染id,渲染模版id和渲染data
function render_with_template_and_data(id, template_id, data){
    var template = $("#"+template_id).html();
    var content = _.template(template, {variable: "datas"})(data);
    $("#"+id).html(content);
}
//根据需求说明得到年级掩模
function get_grand_mask(grand_name){
    //默认为空
    var grand_mask = [];
    _.each(window.subclass_mask_list, function(one_subclass){
        if(grand_name == one_subclass[0]){
            grand_mask = one_subclass[1];
        }
    });
    return grand_mask;
}
//映射年级按钮
function map_grand_button_mask(grand_mask){
    var primary_mask = grand_mask[0];
    var junior_mask = grand_mask[1];
    var high_mask = grand_mask[2];
    var _set_mask = function(mask, button_list){
        for(i = 0; i < mask.length; i++){
            if (mask[i] == 0){
                button_list[i][3] = false;
            }
            if(mask[i] == 1){
                button_list[i][3] = true;
            }
        }
    };
    _set_mask(primary_mask, window.primary_list);
    _set_mask(junior_mask, window.junior_list);
    _set_mask(high_mask, window.high_list);
}
//获取所选tag
function getSelectedTags(){
  var ret = [];
  _.each($('[name=tag-item]'), function(item){
    if($(item).hasClass('item-selected')){
      ret[ret.length] = $(item).attr('data-id');
    }
  });
  return ret;
}
function checkFlag(isSubmit){
  if((isSubmit && getSelectedTags().length>3) || (!isSubmit && getSelectedTags().length>=3)){
    alert('风格标签不能超过3个！');
    return false;
  }
  return true;
}
function checkMinFlag(){
  if(getSelectedTags().length == 0){
    alert('风格标签至少选择一个！');
    return false;
  }
  return true;
}
