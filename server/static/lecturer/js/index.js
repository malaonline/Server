/**
 * Created by erdi on 03/03/2017.
 */

$(function() {
  var defaultErrMsg = '请求失败, 请稍后重试, 或联系管理员!';
  // 基本元组：总数,正确数,选A数,选B数,选C数,选D数
  var META_ITEM = { 'total': 0, 'right': 0, 'A': 0, 'B': 0, 'C': 0, 'D': 0 };
  var group2questions = {},
    questions, question; // <题组, 题目>数据存储,题组所有题目,单个题目
  var back_data, question_data = [],
    questions_data = {},
    school_data = [],
    index = 0; // 返回数据,问题数据,数组数据,校区数据,题目序号

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

  // 问题数据格式化
  var questionDataFormat = function(data) {
      var stat_data = [];
      for (var i in data) {
        stat_data[stat_data.length] = [i, data[i]];
      }
      question_data = stat_data;
      return question_data;
    }
    // 题组数据格式化
  var questionsDataFormat = function(data) {
      var right = 0,
        total = 0;
      for (var i in data) {
        right += data[i].right;
        total += data[i].total;
      }
      questions_data.right = right;
      questions_data.total = total;
      return questions_data;
    }
    // 校区数据格式化
  var schoolDataFormat = function(data) {
    var stat_data = [],
      accuracy, name;
    var j = 0,
      l = back_data.length;
    for (var i in data) {
      accuracy = data[i].right / data[i].total;
      for (; j < l; j++) {
        if (i == back_data[j].sc_id) {
          name = back_data[j].sc_name;
          stat_data[stat_data.length] = [name, data[i], accuracy]
          break;
        }
      }
    }
    school_data = stat_data;
    return school_data;
  }

  //绘制问题饼图
  var drawPie = function(data, elem, radius, text) {
    if (data[0].value == 0 & data[1].value == 0) {
      var accuracy = 0;
    } else {
      var accuracy = (data[0].value / (data[0].value + data[1].value) * 100).toFixed(2);
    }
    var pie = echarts.init(elem);

    var option = {
      title: {
        text: text,
        subtext: '正确率: ' + accuracy + '%',
        subtextStyle: {
          color: '#000',
          fontSize: 14
        }
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
        radius: radius,
        label: {
          normal: {
            show: true,
            position: 'center',
            formatter: '{b}:{c}人',
            textStyle: {
              fontSize: 14,
              color: '#000'
            }
          },
          emphasis: {
            show: true,
            formatter: '正确率:{d}%',
            textStyle: {
              fontSize: 14,
              fontWeight: 700,
              color: 'red'
            }
          }
        },
        labelLine: {
          normal: {
            show: true
          }
        },
        data: data
      }],
      color: ['#00bcff', '#ff9900']
    };

    pie.setOption(option);
    window.addEventListener('resize', function() {
      pie.resize();
    });
  }

  // 绘制问题柱状图
  var drawBar = function(data, elem, subtext) {
    var bar = echarts.init(elem);

    var config = {
      title: {
        subtext: subtext,
        subtextStyle: {
          color: '#000',
          fontSize: 14
        }
      },
      color: ['#FF8C69'],
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
        barWidth: '50%',
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
    window.addEventListener('resize', function() {
      bar.resize();
    });
  }

  // 动态显示问题
  var showQuestion = function(data, index) {
    var data = data[index];
    question = (index + 1) + '. ' + data.title;
    $('.exercise').text(question);
    var i = 0,
      l = data.options.length;
    for (; i < l; i++) {
      $('.option span:eq(' + i + ')').text(data.options[i].text);
      if (data.options[i].id == data.solution) {
        $('.option li:eq(' + i + ')').css('color', '#00bcff');
      }
    }
  }

  // 根据题目id渲染图表
  var drawChartById = function(question_data, id) {
    var i = 0,
      l = question_data.length;
    for (; i < l; i++) {
      if (questions[index].id == question_data[i][0]) {
        drawQuestionChart(question_data[i][1]);
      }
    }
  }

  // 上一题
  $('.previous').click(function() {
    if (back_data) {
      index--;
      $('.next button').removeAttr('disabled');
      if (index == 0) {
        $('.previous button').prop('disabled', true);
      }
      $('.option li').css('color', '#000');
      showQuestion(questions, index);
      drawChartById(question_data, questions[index].id);
    }
  })

  // 下一题
  $('.next').click(function() {
    if (back_data) {
      index++;
      $('.previous button').removeAttr('disabled');
      if (index == question_data.length - 1) {
        $('.next button').prop('disabled', true);
      }
      $('.option li').css('color', '#000');
      showQuestion(questions, index);
      drawChartById(question_data, questions[index].id);
    }
  })

  // 绘制问题结果图表(接收格式化后的数组中的数据对象)
  var drawQuestionChart = function(data) {
      var right_count = data.right || 0,
        wrong_count = data.total - data.right || 0;
      var A_count = data.A || 0,
        B_count = data.B || 0,
        C_count = data.C || 0,
        D_count = data.D || 0;
      var elem1 = $('.pie')[0],
        elem2 = $('.bar')[0];
      var radius = ['30%', '50%'];
      var text = '参与人数: ' + data.total + '人';
      var subtext = '各选项人数';
      var pie_arr = [{
        value: right_count,
        name: '正确'
      }, {
        value: wrong_count,
        name: '错误'
      }];
      drawPie(pie_arr, elem1, radius, text);
      var bar_arr = [A_count, B_count, C_count, D_count];
      drawBar(bar_arr, elem2, subtext);
    }
    // 绘制题组图表
  var drawQuestionsChart = function(data) {
      var right_count = data.right || 0,
        wrong_count = data.total - data.right || 0;
      var elem = $('.questions')[0];
      var radius = ['50%', '75%'];
      var text = '参与人数: ' + data.total + '人';
      var pie_arr = [{
        value: right_count,
        name: '正确'
      }, {
        value: wrong_count,
        name: '错误'
      }];
      drawPie(pie_arr, elem, radius, text);
    }
    // 绘制校区结果图表(接收格式化后的数据数组)
  var drawSchoolChart = function(data) {
    console.log(data[0])
    var i = 0,
      l = data.length;
    var right_count = 0,
      wrong_count = 0;
    var A_count = 0,
      B_count = 0,
      C_count = 0,
      D_count = 0;
    var pie_arr, bar_arr;
    var elem1, elem2, radius = '50%',
      text = '',
      subtext = '';
    var $row, $col;
    $('.accuracy').html('');
    for (; i < l; i++) {
      text = data[i][0];
      right_count = data[i][1].right;
      wrong_count = data[i][1].total - data[i][1].right;
      A_count = data[i][1].A;
      B_count = data[i][1].B;
      C_count = data[i][1].C;
      D_count = data[i][1].D;
      // 动态生成校区图表容器
      $col = $('<div class="col-xs-6">' +
        '<div class="row">' +
        '<div class="pie' + i + ' col-xs-6"></div>' +
        '<div class="bar' + i + ' col-xs-6"></div>' +
        '</div>' +
        '</div>');
      if (i % 2 === 0) {
        $row = $('<div class="row">' +
          '</div>');
      }
      $row.append($col);
      $('.accuracy').append($row);
      elem1 = $('.pie' + i)[0];
      elem2 = $('.bar' + i)[0];

      pie_arr = [{
        value: right_count,
        name: '正确'
      }, {
        value: wrong_count,
        name: '错误'
      }];
      drawPie(pie_arr, elem1, radius, text);
      bar_arr = [A_count, B_count, C_count, D_count];
      drawBar(bar_arr, elem2, subtext);
    }
  }

  // 获取答题结果的心跳
  var getSessionResults = function(repeat) {
    var delayRefresh = function(repeat) {
      if (repeat) {
        setTimeout(function() {
          getSessionResults(true)
        }, delay);
      }
    };
    var delay = 3000;
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
          if (json && json.ok && json.data.length != 0) {
            console.log(json);
            if (!_.isEqual(back_data, json.data)) {
              back_data = json.data;
              var stat_data = calc_questions_stat(json.data);
              var stat_question = stat_data[0];
              var stat_school = stat_data[1];
              $('.question').show();
              console.log(questions);
              drawChartById(questionDataFormat(stat_question), questions[index].id);
              drawQuestionsChart(questionsDataFormat(stat_question));
              if ($('.btn-group button:eq(1)').attr('class') == 'btn btn-default') {
                $('.question .row:eq(1)').hide();
              }
              $('.school').show();
              drawSchoolChart(schoolDataFormat(stat_school));
            }
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
    questions = reqQuestionsOfGroup(params['question_group'], true).questions;
    console.log(questions);
    showQuestion(questions, index);
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

  // 题目题组图表切换
  $('.question .btn-group button:eq(0)').click(function() {
    $(this).removeClass('btn-default').addClass('btn-info');
    $('.question .btn-group button:eq(1)').removeClass('btn-info').addClass('btn-default');
    $('.question .row:eq(0)').show();
    $('.question .row:eq(1)').hide();
    drawChartById(question_data, questions[index].id);
  })
  $('.question .btn-group button:eq(1)').click(function() {
    $(this).removeClass('btn-default').addClass('btn-info');
    $('.question .btn-group button:eq(0)').removeClass('btn-info').addClass('btn-default');
    $('.question .row:eq(0)').hide();
    $('.question .row:eq(1)').show();
    drawQuestionsChart(questions_data);
  })

  // 控制校区排列顺序
  $('.sort').click(function() {
    if ($(this).text() === '正序') {
      school_data.sort(function(a, b) {
        var value1 = a[2];
        var value2 = b[2];
        return value2 - value1;
      })
      $('.accuracy').html('');
      drawSchoolChart(school_data);
      $(this).removeClass('btn-default').addClass('btn-info').text('倒序');
    } else {
      school_data.sort(function(a, b) {
        var value1 = a[2];
        var value2 = b[2];
        return value1 - value2;
      })
      $('.accuracy').html('');
      drawSchoolChart(school_data);
      $(this).removeClass('btn-info').addClass('btn-default').text('正序');
    }
  })
});
