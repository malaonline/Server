/**
 * Created by Elors on 2017/1/3.
 * Exercise component
 */


define(function () {
  $(function () {

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
                <el-radio v-bind:label="option.text" class="radio-item-radio"></el-radio>\
                <input class="el-radio__label" v-model="exercise.options[index]"/>\
              </div>\
            </el-radio-group>\
          </el-form-item>\
          <el-form-item v-bind:label="analyzeStr">\
            <el-input type="textarea" v-model="exercise.analyse"></el-input>\
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

  });
});