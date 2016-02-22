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

var paginationInit = function(fn, $ctx) {
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

var paginationUpdate = function(pager, $ctx) {
    var $pagination = $('.pagination', $ctx);
    var bakClick = $._data($pagination.find('a')[0], 'events').click[0];
    $pagination.children().remove();
    $pagination.append('<li class="' + ((!pager.page || pager.page <= 1)?'disabled':'') + '">'
        + '<a href="javascript:void(0)" aria-label="First" data-pageto="1"><span aria-hidden="true">&laquo;</span></a>'
        + '</li>');
    $pagination.append('<li class="' + ((!pager.page || pager.page <= 1)?'disabled':'') + '">'
        + '<a href="javascript:void(0)" aria-label="Previous" data-pageto="' + (pager.page - 1) + '"><span aria-hidden="true">&lt;</span></a>'
        + '</li>');
    for(var i = 1; i <= pager.total_page; i++) {
        $pagination.append('<li class="' + (pager.page == i?'active':'') + '">'
            + '<a href="javascript:void(0)" data-pageto="' + i + '">' + i + '</a>'
            + '</li>');
    }
    $pagination.append('<li class="' + ((!pager.page || pager.page >= pager.total_page)?'disabled':'') + '">'
        + '<a href="javascript:void(0)" aria-label="Next" data-pageto="' + (pager.page + 1) + '"> <span aria-hidden="true">&gt;</span></a>'
        + '</li>');
    $pagination.append('<li class="' + ((!pager.page || pager.page >= pager.total_page)?'disabled':'') + '">'
        + '<a href="javascript:void(0)" aria-label="Last" data-pageto="' + pager.total_page + '"> <span aria-hidden="true">&raquo;</span></a>'
        + '</li>');
    $pagination.find('a').click(bakClick);
};