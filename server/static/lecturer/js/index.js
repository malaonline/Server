/**
 * Created by erdi on 03/03/2017.
 */

$(function() {
  var defaultErrMsg = '请求失败, 请稍后重试, 或联系管理员!';
  // 基本元组：总数,正确数,选A数,选B数,选C数,选D数
  var META_ITEM = { 'total': 0, 'right': 0, 'A': 0, 'B': 0, 'C': 0, 'D': 0 };
  var group2questions = {}; // <题组, 题目>数据存储

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
    } else {
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
    var stat_question = {},
      stat_school = {}; // <qid: <META_ITEM>>
    for (var i in submits) {
      var row = submits[i];
      var obj = stat_question[row.qid] || $.extend({}, META_ITEM);
      stat_question[row.qid] = _update_stat_item(obj, row);
      var sc_obj = stat_school[row.sc_id] || $.extend({}, META_ITEM);
      stat_school[row.sc_id] = _update_stat_item(sc_obj, row);
    }
    console.log(stat_question);
    console.log(stat_school);
    var stat_data = [stat_question, stat_school];
    return stat_data;
  };

  // 绘制柱状图
  var drawBar = function(data) {
    var sum = 0,
      i = 0,
      l = data.length;
    for (; i < l; i++) {
      sum += data[i]
    }
    var bar = echarts.init($('.bar')[0]);

    var config = {
      title: {
        text: '参与人数: ' + sum + '人'
      },
      color: ['#0094ff'],
      tooltip: {
        trigger: 'axis',
        formatter: '{c}人',
        axisPointer: {
          type: 'shadow'
        }
      },
      xAxis: [{
        type: 'category',
        data: ['A', 'B', 'C', 'D'],
        axisTick: {
          alignWithLabel: true
        }
      }],
      yAxis: {
        axisLine: {
          show: false
        },
        axisTick: {
          show: false
        },
        axisLabel: {
          show: false
        },
        splitLine: {
          show: false
        }
      },
      series: [{
        name: '选项统计',
        type: 'bar',
        data: data,
        label: {
          normal: {
            show: true,
            position: 'outside',
            formatter: '{c}人'
          }
        }
      }]
    };

    bar.setOption(config);
  }

  //绘制饼图
  var drawPie = function(data) {
      if (data[0].value == 0 & data[1].value == 0) {
        var accuracy = 0
      } else {
        var accuracy = data[0].value / (data[0].value + data[1].value) * 100
      }
      var pie = echarts.init($('.pie')[0]);

      var option = {
        title: {
          text: '正确率: ' + accuracy + '%'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a}<br/>{b}:{c}人({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: '10%',
          bottom: '10%',
          data: ['正确', '错误']
        },
        series: [{
          name: '正确率统计',
          type: 'pie',
          radius: ['30%', '50%'],
          label: {
            normal: {
              show: true,
              position: 'outside',
              formatter: '{b}:{c}人'
            },
            emphasis: {
              show: true,
              formatter: '正确率:{d}%',
              textStyle: {
                fontSize: '14',
                fontWeight: 700
              }
            }
          },
          labelLine: {
            normal: {
              show: true
            }
          },
          data: data
        }]
      };

      pie.setOption(option);
    }
    // 初始化显示图表
  drawPie([{
    value: 0,
    name: '正确'
  }, {
    value: 0,
    name: '错误'
  }]);
  drawBar([0, 0, 0, 0]);

  // 绘制问题结果图表
  var drawQuestionChart = function(data) {
    for (var i in data) {
      var stat_data = [];
      stat_data[stat_data.length] = data[i];
    }
    var right_count = stat_data[0].right || 0;
    var wrong_count = stat_data[0].total - stat_data[0].right || 0;
    var A_count = stat_data[0].A || 0;
    var B_count = stat_data[0].B || 0;
    var C_count = stat_data[0].C || 0;
    var D_count = stat_data[0].D || 0;
    var pie_arr = [{
      value: right_count,
      name: '正确'
    }, {
      value: wrong_count,
      name: '错误'
    }];
    drawPie(pie_arr);
    var bar_arr = [A_count, B_count, C_count, D_count];
    drawBar(bar_arr);
  }

  // 绘制校区结果图表

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
            var stat_question = calc_questions_stat(json.data)[0];
            var stat_school = calc_questions_stat(json.data)[1];
            drawQuestionChart(stat_question);
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

  // 控制校区排列顺序
  $('.sort').click(function() {
    if ($(this).text() === '正序') {
      $(this).removeClass('btn-default').addClass('btn-primary').text('倒序')
    } else {
      $(this).removeClass('btn-primary').addClass('btn-default').text('正序')
    }
  })
});
