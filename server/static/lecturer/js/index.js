/**
 * Created by erdi on 03/03/2017.
 */

$(function() {
  var defaultErrMsg = '请求失败, 请稍后重试, 或联系管理员!';

  $("[data-action=start]").click(function() {
    var params = {
      'action': 'start',
      'question_group': $("#question-group").val(),
      'live_course_timeslot': $("#live-course-timeslot").val()
    };
    malaAjaxPost(location.pathname, params, function(result) {
      if (result) {
        if (result.ok) {
          $("#question-group").enable(false);
          $("[data-action=start]").hide();
          $("[data-action=stop]").show();
        } else {
          alert(result.msg);
        }
        return;
      }
      alert(defaultErrMsg);
    }, 'json', function() {
      alert(defaultErrMsg);
    });
  });

  $("[data-action=stop]").click(function() {
    var params = {
      'action': 'stop',
      'question_group': $("#question-group").val(),
      'live_course_timeslot': $("#live-course-timeslot").val()
    };
    malaAjaxPost(location.pathname, params, function(result) {
      if (result) {
        if (result.ok) {
          $("#question-group").enable(true);
          $("[data-action=stop]").hide();
          $("[data-action=start]").show();
        } else {
          alert(result.msg);
        }
        return;
      }
      alert(defaultErrMsg);
    }, 'json', function() {
      alert(defaultErrMsg);
    });
  });
});
