/**
 * Created by liumengjun on 2/23/16.
 */
$(function () {
    $('[data-toggle="tooltip"]').tooltip({'html':true});

    $('.hint .close-icon').click(function(){
        $(this).closest('.hint').hide();
    });
});
