/**
 * Created by erdi on 03/03/2017.
 */

$(function() {
  $("[data-action=start]").click(function() {
    $("#question-group").enable(false);
    $(this).hide();
    $("[data-action=stop]").show();
  });
  $("[data-action=stop]").click(function() {
    $("#question-group").enable(true);
    $(this).hide();
    $("[data-action=start]").show();
  });
});
