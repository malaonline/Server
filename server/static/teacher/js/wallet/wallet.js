/**
 * Created by liumengjun on 2/20/16.
 */

$(function(){
    paginationInit(function(e) {
        var $this = $(this), $li = $this.closest('li');
        if ($li.hasClass('disabled') || $li.hasClass('active')) return;
        var page_to = $this.data('pageto');
        $.getJSON(histories_url, {'page': page_to} ,function(data){
            if (data && data.list && data.pager) {
                var $historyTable = $(".history table");
                $historyTable.find('tr:gt(0)').remove();
                for (var i=0; i<data.list.length; i++) {
                    var hist = data.list[i];
                    $historyTable.append('<tr class="item">'
                        + '<td class="time">' + hist.submit_time + '</td>'
                        + '<td class="amount ' + (hist.positive?'income':'outgoing') + '">' + hist.amount + '</td>'
                        + '<td class="comment">' + (hist.comment?hist.comment:(hist.positive?'收入':'支出')) + '</td>'
                        + '</tr>');
                }
                paginationUpdate(data.pager);
            }
        });
    });
});
