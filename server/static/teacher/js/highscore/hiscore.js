$(function(){
  $('#checkboxSelectAll').change(function(e){
    var ck = false;
    if($('#checkboxSelectAll').is(':checked')){
      ck = true;
    }
    $('[name="checkboxSelect"]').each(function(){
      $(this).prop('checked', ck ? 'checked' : false);
    });
  });
});
