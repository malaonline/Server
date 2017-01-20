/**
 * Created by erdi on 20/01/2017.
 */

$(function() {
  $("#status-select").change(function() {
    if ($(this).val() == 'all') {
      location.search = "?show_all=true";
    } else {
      location.search = "";
    }
  })
});
