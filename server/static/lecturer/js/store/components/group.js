/**
 * Created by Elors on 2017/1/3.
 * Exercise-Group component
 */

define(['Exercise'], function () {
  $(function () {

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
    };

    var emptyModel = function () {
      return {
        title: '',
        options: ['', '', '', ''],
        solution: null,
        desc: ''
      }
    }

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
          var model = emptyModel()
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

  });
});