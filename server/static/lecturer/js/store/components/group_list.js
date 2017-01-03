/**
 * Created by Elors on 2017/1/3.
 * Group-List component
 */

define(function () {
  $(function () {

    let g_group_list = [];

    var load_group_list = function () {
      $.getJSON('/lecturer/api/exercise/store?action=group_list', function (json) {
        if (json && json.ok) {
          for (let g of json.data) {
            g_group_list.push(g);
          }
        }
      });
    };

    Vue.component('store-group-list', {
      template: '\
      <div class="row store-row sidebar-pane">\
          <el-tree :data="data" :props="defaultProps" @node-click="handleNodeClick" id="tree-content"></el-tree>\
      </div>\
    ',
      data: function () {
        return {
          data: g_group_list,
          defaultProps: {
            children: 'list',
            label: 'title'
          }
        }
      },
      methods: {
        handleNodeClick () {
          load_group_list()
        }
      }
    });

    load_group_list();

  });
});