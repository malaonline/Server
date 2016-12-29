/**
 * Created by liumengjun on 2016-12-28.
 */

$(function(){
    let caches = {};
    let all_groups = new Vue({
        delimiters: ["[[", "]]"],
        el: '#all-groups',
        data: {
            groupList: []
        }
    });

    let selected_groups = new Vue({
        delimiters: ["[[", "]]"],
        el: '#selected-groups',
        data: {
            groupList: []
        }
    });

    // init all group list
    $.getJSON('/lecturer/api/exercise/store?action=group_list', function(json){
        if (json && json.ok) {
            for (let g of json.data) {
                all_groups.groupList.push(g);
            }
        }
    });

    function render_selected_groups(group_ids){
        for (let gid of group_ids) {
            if (!caches['g' + gid]) {
                $.ajax({
                    async: false,
                    dataType: "json",
                    url: '/lecturer/api/exercise/store?action=group&gid=' + gid,
                    success: function(json){
                        if (json && json.ok) {
                            caches['g' + gid] = json.data;
                        }
                    }
                });
            }
            selected_groups.groupList.push(caches['g' + gid])
        }
    }

    // init origin selected group
    render_selected_groups(old_group_ids);


});