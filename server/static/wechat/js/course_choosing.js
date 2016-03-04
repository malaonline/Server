/**
 * Created by liumengjun on 3/5/16.
 */
$(function(){
    //alert("course choosing");

    $('.grade-box > .grade').click(function(e){
        var ele = e.target, $ele = $(ele);
        var val = $ele.data('gradeid');
        $(".grade-box > .grade").each(function(){
          var $this = $(this), v = $this.data('gradeid');
          if (v===val) {
            $this.addClass('chosen');
          } else {
            $this.removeClass('chosen');
          }
        });
        e.stopPropagation();
    });

    $('#showMoreSchoolsBtn').click(function(e){
        $(this).hide();
        $('#moreSchoolsContainer').show();
    });

    $(".school > .icons-area input[type=radio]").click(function(e){
        var ele = e.target, $ele = $(ele).closest(".icons-area");
        var val = $ele.find("input")[0].value;
        $(".school > .icons-area").each(function(){
          var $this = $(this), v = $this.find("input")[0].value;
          if (v===val) {
            $this.addClass('chosen');
          } else {
            $this.removeClass('chosen');
          }
        });
        e.stopPropagation();
    });
});
