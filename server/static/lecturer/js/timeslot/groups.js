/**
 * Created by liumengjun on 2016-12-28.
 */

$(function(){
    let all_groups = new Vue({
        delimiters: ["[[", "]]"],
        el: '#all-groups',
        data: {
            groupList: []
        }
    });

    $.getJSON('/lecturer/api/exercise/store?action=group_list', function(json){
        if (json && json.ok) {
            for (let g of json.data) {
                all_groups.groupList.push(g);
            }
        }
    });

});