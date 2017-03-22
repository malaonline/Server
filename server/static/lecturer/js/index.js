/**
 * Created by erdi on 03/03/2017.
 */

$(function() {
  var defaultErrMsg = '请求失败, 请稍后重试, 或联系管理员!';
  // 基本元组：总数,正确数,选A数,选B数,选C数,选D数
  var META_ITEM = {'total': 0, 'right': 0, 'A': 0, 'B': 0, 'C': 0, 'D': 0};
  var group2questions = {};  // <题组, 题目>数据存储

  /* 根据题组ID获取题组题目 */
  var reqQuestionsOfGroup = function(gid, sync) {
    if (!group2questions[gid]) {
      $.ajax({
        url: '/lecturer/api/exercise/store?action=group&gid=' + gid,
        async: !sync,
        dataType: "json",
        success: function(json) {
          if (json && json.ok) {
            group2questions[gid] = json.data;
          }
        }
      });
    }
    return group2questions[gid];
  };

  // 根据 session 激活状态更新 UI
  var refreshUI = function() {
    var session = $("#active-session").val();
    if (session) {
      $("#question-group").enable(false);
      $("[data-action=start]").hide();
      $("[data-action=stop]").show();
    }
    else {
      $("#question-group").enable(true);
      $("[data-action=stop]").hide();
      $("[data-action=start]").show();
    }
  };

  var _update_stat_item = function(item, row) {
    item['total'] += 1;
    if (row.seq) item[row.seq.toUpperCase()] += 1;
    if (row.ok) item['right'] += 1;
    return item;
  };

  var calc_questions_stat = function(submits) {
    var stat_question = {}, stat_school = {};  // <qid: <META_ITEM>>
    for (var i in submits) {
      var row = submits[i];
      var obj = stat_question[row.qid] || $.extend({}, META_ITEM);
      stat_question[row.qid] = _update_stat_item(obj, row);
      var sc_obj = stat_school[row.sc_id] || $.extend({}, META_ITEM);
      stat_school[row.sc_id] = _update_stat_item(sc_obj, row);
    }
    console.log(stat_question);
    console.log(stat_school);
  };

  // 获取答题结果的心跳
  var getSessionResults = function(repeat) {
    var delayRefresh = function(repeat) {
      if (repeat) {
        setTimeout(function() {
          getSessionResults(true)
        }, delay);
      }
    };
    var delay = 1000;
    var sessionId = $("#active-session").val();
    if (sessionId) {
      var params = {
        'action': 'exercise_submits',
        'sid': sessionId
      };
      $.ajax({
        'type': "GET",
        'url': '/lecturer/api/exercise/store',
        'data': params,
        'success': function(json) {
          if (json && json.ok) {
            console.log(json);
            calc_questions_stat(json.data);
          }
          delayRefresh(repeat);
        },
        'error': function() {
          delayRefresh(repeat);
        }
      });
    } else {
      delayRefresh(repeat);
    }
  };

  // 开始答题
  $("[data-action=start]").click(function() {
    var params = {
      'action': 'start',
      'question_group': $("#question-group").val(),
      'live_course_timeslot': $("#live-course-timeslot").val()
    };
    reqQuestionsOfGroup(params['question_group']);
    malaAjaxPost(location.pathname, params, function(result) {
      if (result) {
        if (result.ok) {
          $("#active-session").val(result.sid);
          refreshUI();
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

  // 结束答题
  $("[data-action=stop]").click(function() {
    var params = {
      'action': 'stop',
      'question_group': $("#question-group").val(),
      'live_course_timeslot': $("#live-course-timeslot").val()
    };
    reqQuestionsOfGroup(params['question_group']);
    malaAjaxPost(location.pathname, params, function(result) {
      if (result) {
        if (result.ok) {
          getSessionResults(false);
          $("#active-session").val("");
          refreshUI();
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

  refreshUI();
  getSessionResults(true);
});
