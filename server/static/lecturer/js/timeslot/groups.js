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
        },
        methods: {
            doAdd: function(e){
                let $group = $(e.target).closest('.group-item');
                let gid = $group.attr('gid');
                let isAdded = _.find(selected_groups.groupList, function(g){
                    return g.id == gid;
                });
                if (isAdded) {
                    return;
                }
                render_selected_groups([gid]);
            }
        }
    });

    let selected_groups = new Vue({
        delimiters: ["[[", "]]"],
        el: '#selected-groups',
        data: {
            groupList: []
        },
        methods: {
            doDelete: function(e){
                let $group = $(e.target).closest('.selected-group');
                let gid = $group.attr('gid');
                let idx = _.findIndex(selected_groups.groupList, function(g){
                    return g.id == gid;
                });
                if (idx < 0) {
                    return;
                }
                selected_groups.groupList.splice(idx, 1);
            }
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

    function render_selected_groups(group_ids, clear){
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
            if (caches['g' + gid]) {
                selected_groups.groupList.push(caches['g' + gid]);
            }
        }
    }

    // init origin selected group
    render_selected_groups(old_group_ids);

    $('#save-btn').click(function(e){
        let selected_ids = _.map(selected_groups.groupList, function(g){
            return g.id;
        });
        let diffIn = _.difference(old_group_ids, selected_ids);
        let diffOut = _.difference(selected_ids, old_group_ids);
        console.log(diffIn);
        console.log(diffOut)
    });
});