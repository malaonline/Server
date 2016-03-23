/**
 * Created by liumengjun on 3/23/16.
 */
$(function(){
    // 初始化"测评建档服务"内容
    var $evaluateItemsBody = $('#evaluateItemsBody');
    var buf = [];
    for (var i in evaluateItems) {
        var obj = evaluateItems[i];
        buf.push('<div class="evaluate-item">');
        buf.push('<div class="evaluate-title">');
        buf.push(obj.title);
        buf.push('</div>');
        buf.push('<div class="evaluate-photo">');
        buf.push('<img src="'+obj.photo+'"/>');
        buf.push('</div>');
        buf.push('<div class="evaluate-note">');
        buf.push(obj.desc);
        buf.push('</div>');
        buf.push('</div>');
    }
    $evaluateItemsBody.append(buf.join(''));
});
