/**
 * Created by Elors on 2017/1/3.
 * Exercise-Group component
 */

define(['Exercise'], function () {
  $(function () {

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
          <el-form-item class="item-submit" v-show="shouldShow">\
            <el-button type="success" icon="plus" @click="onInsert">新增习题</el-button>\
            <el-button type="primary" icon="edit" @click="onSave">保存题组</el-button>\
          </el-form-item>\
        </el-form>\
      </div>\
    ',
      data: function () {
        return {
          form: {},
          shouldShow: false
        }
      },
      methods: {
        loadGroup (data) {
          let group = this;
          $.ajax({
            async: false,
            dataType: "json",
            url: '/lecturer/api/exercise/store?action=group&gid=' + data.id,
            success: function (json) {
              if (json && json.ok) {
                group.form = group.handleData(json);
                group.shouldShow = true;
              }
            }
          });
        },
        onInsert () {
          let model = this.defaultExercise();
          this.form.exercises.push(model);
        },
        onSave () {
          console.log(this.form.exercises)
        },
        handleData (json) {
          let form = {};
          form.id = json.data.id;
          form.title = json.data.title;
          form.desc = json.data.desc;
          for (question of json.data.questions) {
            let exercise = {};
            exercise.id = question.id;
            exercise.title = question.title;
            exercise.analyse = question.analyse;
            exercise.options = question.options;
            for (option of question.options) {
              if (question.solution === option.id) {
                exercise.solution = option.text;
              }
            }
            let exercises = [];
            exercises.push(exercise);
            form.exercises = exercises;
          }
          return form
        },
        defaultExercise () {
          return {
            id: null,
            title: '',
            solution: null,
            analyse: '',
            options: ['', '', '', '']
          }
        }
      }
    });

  });
});