/**
 * Created by Elors on 2017/1/3.
 * Group-List component
 */

define(function () {
  $(function () {

    Vue.component('store-group-list', {
      template: '\
      <div class="row store-row sidebar-pane">\
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
        console.info('list init');
        this.load_list();
        this.handleNodeClick(this.data[0]);
      },
      methods: {
        load_list () {
          let group_list = this;
          $.getJSON('/lecturer/api/exercise/store?action=group_list', function (json) {
            if (json && json.ok) {
              for (let g of json.data) {
                group_list.data.push(g);
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
        }
      }
    });

  });
});