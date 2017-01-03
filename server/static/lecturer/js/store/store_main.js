/**
 * Created by Elors on 2017/1/3.
 */

require.config({
  paths: {
    'Vue': '../../../common/js/vue-2.1.8.min',
    'StoreContainer': './components/store_container',
    'GroupList': './components/group_list',
    'ExerciseGroup': './components/group',
    'Exercise': './components/exercise',
  }
});

require(['Vue', 'StoreContainer'], function () {
  console.log('requireJS')
});