/**
 * Created by liumengjun on 1/21/16.
 */
/**
 * 更新当前网页url的查询参数key=val
 * @param key
 * @param val
 */
var updateLocationByParam = function(key, val) {
    var key_val = key+'='+val;
    var old_search = location.search;
    if (!old_search) {
        location.search = "?" + key_val;
        return;
    }
    var key_index = old_search.indexOf(key+"=");
    if (key_index<0) { // 原来没有page参数
        location.search = old_search + '&' + key_val;
        return;
    }
    // 有旧的key参数
    var end_index = old_search.indexOf('&', key_index + key.length + 1); // 加"{key}="的长度
    if (end_index<0) { // 无后续参数
        location.search = old_search.substring(0, key_index) + key_val;
    } else { // 有后续参数
        location.search = old_search.substring(0, end_index) + key_val + old_search.substring(end_index);
    }
};

var paginationInit = function($ctx, fn) {
    if (!fn) { // 导航点击事件: 默认取pageto属性值,更新url查询参数,可以自定义
        fn = function(e){
            var $this = $(this), $li = $this.closest('li');
            if ($li.hasClass('disabled') || $li.hasClass('active')) return;
            var page_to = $this.data('pageto');
            updateLocationByParam('page', page_to);
        }
    }
    $('.pagination a', $ctx).click(fn);
};