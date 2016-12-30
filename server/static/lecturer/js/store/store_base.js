/**
 * Created by Elors on 2016/12/29.
 */

$(function () {

  // container component
  Vue.component('store-container', {
    template: '\
      <div class="row store-row">\
       <div class="col-sm-9 store-col">\
         <exercise-group class="store-content"/>\
       </div>\
       <div class="col-sm-3 store-col">\
         <store-group-list class="store-content"/>\
       </div>\
      </div>\
    '
  });

  // group component
  Vue.component('exercise-group', {
    template: '\
      <div class="row store-row content-pane">\
        <el-form ref="form" :model="form" label-width="0px" class="page-pane">\
          <el-form-item label-width="0px"  class="text item-input">\
            <el-input v-model="form.title" class="input" id="ex-title"></el-input>\
          </el-form-item>\
         <el-form-item label-width="0px"  class="text item-input">\
            <el-input v-model="form.desc" class="input" id="ex-desc"></el-input>\
         </el-form-item>\
          <transition-group name="list" tag="div">\
            <store-exercise\
              v-for="(exercise, index) in form.exercises"\
              v-bind:list="form.exercises"\
              v-bind:exercise="exercise"\
              v-bind:index="index"\
              :key="index+\'ex\'">\
            </store-exercise>\
          </transition-group>\
          <el-form-item class="item-submit">\
            <el-button type="success" icon="plus" @click="onInsert">新增习题</el-button>\
            <el-button type="primary" icon="edit" @click="onSave">保存题组</el-button>\
          </el-form-item>\
        </el-form>\
      </div>\
    ',
    data: function () {
      return {
        form: t_group
      }
    },
    methods: {
      onInsert () {
        var model = exerciseModel()
        this.form.exercises.push(model)
      },
      onSave () {
        console.log(this.form.exercises)
      },
      onCancel () {
        console.log('Cancel!')
      }
    }
  });

  // model
  function exerciseModel () {
    return {
      title: '',
      options: ['', '', '', ''],
      solution: null,
      desc: ''
    }
  }

  // test data
  let t_group = {
    title: '动词不定式',
    desc: '题组描述根据上句意思完成下句，使两句意思相近或相同，每空一词。',
    exercises: [
      {
        title: 'What`s the weather ______ today?',
        options: ['like', 'is', 'it is', 'there'],
        solution: 'like',
        analyze: '这是这道题的题目解析文字。'
      },
      {
        title: 'How`s the weather ______ today?',
        options: ['like', 'is', 'it is', '空'],
        solution: '空',
        analyze: '这是这道题的题目解析文字。'
      },
      {
        title: 'Would you please ____ it in English?',
        options: ['say', 'speak', 'tell', 'talk'],
        solution: 'say',
        analyze: '这是这道题的题目解析文字。'
      },
      {
        title: 'What`s the weather ______ today?',
        options: ['like', 'is', 'it is', 'there'],
        solution: 'like',
        analyze: '这是这道题的题目解析文字。'
      },
      {
        title: 'What`s the weather ______ today?',
        options: ['like', 'is', 'it is', 'there'],
        solution: 'like',
        analyze: '这是这道题的题目解析文字。'
      }
    ]
  }


  // exercise component
  Vue.component('store-exercise', {
    template: '\
      <div>\
        <el-form ref="form" label-width="15px" class="item-exercise">\
          <el-form-item v-bind:label="serialNum+\'.\'" prop="title" class="item-exercise-title">\
             <el-input v-model="exercise.title" class="title-item"></el-input>\
             <el-button type="danger" icon="close" @click="onRemove" size="mini" id="removeBtn"></el-button>\
          </el-form-item>\
          <el-form-item v-bind:label="optionStr">\
            <el-radio-group v-model="exercise.solution" class="radio-group">\
              <div v-for="(option, index) in exercise.options" class="radio-item">\
                <el-radio v-bind:label="option" class="radio-item-radio"></el-radio>\
                <input class="el-radio__label" v-model="exercise.options[index]"/>\
              </div>\
            </el-radio-group>\
          </el-form-item>\
          <el-form-item v-bind:label="analyzeStr">\
            <el-input type="textarea" v-model="exercise.analyze"></el-input>\
          </el-form-item>\
        </el-form>\
      </div>\
    ',
    props: ['list', 'exercise', 'index'],
    data: function () {
      return {
        serialNum: (this.index + 1),
        optionStr: '',
        analyzeStr: ''
      }
    },
    methods: {
      onRemove () {
        this.$confirm('此操作将永久删除习题( ' + this.serialNum + ' ), 是否继续?', '提示', {
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          let item = this.list.splice(this.index, 1)
          if (item != null) {
            this.$message({type: 'success', message: '删除成功!'})
          } else {
            this.$message({type: 'error', message: '删除失败!'})
          }
        })
      }
    }
  });



  // group_list component
  let t_group_list = []

  var load_group_list = function () {
    $.getJSON('/lecturer/api/exercise/store?action=group_list', function (json) {
      if (json && json.ok) {
        for (let g of json.data) {
          t_group_list.push(g);
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
        data: t_group_list,
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

  load_group_list()

  // render DOM
  let root = new Vue({
    el: '#store-root'
  });

});