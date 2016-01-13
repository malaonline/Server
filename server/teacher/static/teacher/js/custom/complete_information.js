/**
 * Created by caoyawen on 16/1/7.
 */
$(
    function(){
        console.log("TW-2-1");
        $("#add_grand").click(function(eventObject){
            console.log("click add_grand");
            //render_grand_select_button_group();
            eventObject.preventDefault();
        });

        //视图数据定义区
        window.primary_list = [
            // button的id名,button名称,是否被选中,是否可以显示
            ["primary_one", "小学一年级", false, false],
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
        window.need_select_subclass = true;

        //视图绘制区
        render_with_template_and_data("subclass_down_list",
        "subclass_template", ['数学', '英语', '语文',
            '物理', '化学', '地理',
            '历史', '政治', '生物']);
        render_with_template_and_data("city_down_list", "city_template",
        ["洛阳", "其它"]);
        render_grand_button_list();
        render_grand_select_button_group();
    }
);

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
    if (grand_array != undefined){
        var button_template = $("#grand_button_group").html();
        //console.log("button_template is :" + button_template);
        var grand_button_group = _.template(button_template, {variable: 'datas'})(grand_array);
        $("#grand_input").html(grand_button_group);
        render_grander_warning();
        if(window.need_select_subclass == false){
            enable_add_grand();
        }
    }
}

//渲染年级警告
function render_grander_warning(){
    var grand_array = selected_grand();

    if (window.need_select_subclass == true){
        set_grand_warning("select-subclass");
    }else{
        if (grand_array.length == 0){
            set_grand_warning("select-grand");
        }else{
            set_grand_warning("OK");
        }
    }
}

//添加一个年级
function add_button(args){
    set_grand_tag(args, true);
}

//删除一个年级按钮
function remove_button(args){
    //console.log("eventObject => " + eventObject);
    console.log("args => " + args);
    //window.button_list.remove(args);
    set_grand_tag(args, false);
    //render_grand_button_list();
}

//设置年级是否被选择
function set_grand_tag(grand_name, select){
    //注意这里用了闭包来传递参数
    var _remove_grand = function(grand_list){
        _.each(grand_list, function(item){
            if (item[1] == grand_name){
                item[2] = select;
            }
        });
    };
    _remove_grand(window.primary_list);
    _remove_grand(window.junior_list);
    _remove_grand(window.high_list);
    //渲染
    render_grand_button_list();
    render_grand_select_button_group();
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

//渲染年级选择面板
function render_grand_select_button_group(){
    console.log("render_grand_select_button_group");
    var select_button_template = $("#grand_select_button").html();

    var primary_contnet = _.template(select_button_template, {variable: 'datas'})(window.primary_list);
    var junior_contnet = _.template(select_button_template, {variable: 'datas'})(window.junior_list);
    var high_contnet = _.template(select_button_template, {variable: 'datas'})(window.high_list);
    $("#primary_select").html(primary_contnet);
    $("#junior_select").html(junior_contnet);
    $("#high_select").html(high_contnet);
}

//通用设置输入选项
function set_value(id, value){
    $("#"+id).val(value);
}

//授课科目输入选项
function set_subclass_value(id, value){
    $("#"+id).val(value);
    enable_add_grand();
    window.need_select_subclass = false;
    render_grander_warning();
    window.grand_mask = get_grand_mask(value);

    //根据选择重新渲染授课年级系统
    //先修改数据
    clear_selected_grand();
    map_grand_button_mask(window.grand_mask);
    //再渲染设置
    render_grand_button_list();
    render_grand_select_button_group();
}

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

//设置警告的状态,包括 "select-subclass", "select-grand", "OK"
function set_grand_warning(status){
    var select_subclass = $("#need-subclass");
    var select_grand = $("#select-grand");
    var attr = "attrHidden";
    if (status == "select-subclass"){
        select_subclass.attr(attr, false);
        select_grand.attr(attr, true);
        console.log(status);
        return;
    }
    if (status == "select-grand"){
        select_subclass.attr(attr, true);
        select_grand.attr(attr, false);
        console.log(status);
        return;
    }
    if (status == "OK"){
        select_subclass.attr(attr, true);
        select_grand.attr(attr, true);
        console.log(status);
        return;
    }
    throw "unknow status " + status;
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

//清除选择的所有授课年级
function clear_selected_grand(){
    var _clear_one_stage = function(one_stage){
        _.each(one_stage, function(one_subclass){
            one_subclass[2] = false;
        });
    };
    _clear_one_stage(window.primary_list);
    _clear_one_stage(window.junior_list);
    _clear_one_stage(window.high_list);
}
