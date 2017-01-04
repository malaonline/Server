/**
 * Created by Elors on 2017/1/3.
 * Group-List component
 */

define(function () {

  Vue.component('store-group-list', {
    template: '\
      <div class="row store-row sidebar-pane">\
        <div id="group-operat"><el-button type="success" icon="plus" @click="onCreateGroup">新建题组</el-button></div>\
        <el-tree :data="data" :props="defaultProps" @node-click="handleNodeClick" id="tree-content"></el-tree>\
      </div>\
    ',
    data: function () {
      return {
        data: [],
        defaultProps: {
          children: 'list',
          label: 'title'
        }
      }
    },
    mounted () {
      this.load_list();
    },
    methods: {
      load_list () {
        let group_list = this;
        $.ajax({
          async: false,
          dataType: "json",
          url: '/lecturer/api/exercise/store?action=group_list',
          success: function (json) {
            if (json && json.ok) {
              for (let g of json.data) {
                group_list.data.push(g);
              }
            }
          }
        });
      },
      refresh_list () {
        this.data = [];
        this.load_list();
      },
      handleNodeClick (data) {
        this.$emit('selected', data);
      },
      onCreateGroup () {
        this.$emit('onCreate');
      }
    }
  });

});