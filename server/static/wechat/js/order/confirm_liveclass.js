/**
 * Created by erdi on 25/04/2017.
 */

$(function() {
  var liveClassId = $('#liveClassId').val();
  var isPaying = false;
  var $payBtn = $('#confirmBtn');

  var beginPaying = function() {
    isPaying = true;
  };
  var stopPaying = function() {
    hideLoading();
  };

  $payBtn.click(function(e) {
    e.preventDefault();

    if (isPaying) {
      return;
    }
    beginPaying();
    var params = {
      'action': 'confirm',
      'live_class': liveClassId,
      'coupon': ''
    };
    var defaultErrMsg = '请求失败, 请稍后重试或联系客户人员!';
    $.ajax({
      'type': "POST",
      'url': order_pay_url,
      'data': params,
      'success': function(result) {
        if (result) {
          if (result.ok) {
            var data = result.data;
            var prepay_id = data.prepay_id;
            var order_id = data.order_id;
            if (data.TESTING) {
              // in TESTING
              var verify_params = {
                'action': 'verify',
                'prepay_id': prepay_id,
                'order_id': order_id
              };
              $.ajax({
                'type': "POST",
                'url': location.pathname,
                'data': verify_params,
                'success': function(verify_ret) {
                  if (verify_ret && verify_ret.ok) {
                    alert('支付成功');
                  } else {
                    alert(verify_ret && verify_ret.msg || defaultErrMsg);
                  }
                  stopPaying();
                },
                'dataType': 'json',
                'error': function() {
                  alert('获取支付结果失败');
                  stopPaying();
                }
              });
              return;
            }
            wx.chooseWXPay({
              timestamp: data.timeStamp, // 支付签名时间戳，注意微信jssdk中的所有使用timestamp字段均为小写。但最新版的支付后台生成签名使用的timeStamp字段名需大写其中的S字符
              nonceStr: data.nonceStr, // 支付签名随机串，不长于 32 位
              package: data.package, // 统一支付接口返回的prepay_id参数值，提交格式如：prepay_id=***）
              signType: data.signType, // 签名方式，默认为'SHA1'，使用新版支付需传入'MD5'
              paySign: data.paySign, // 支付签名
              success: function(res) {
                var verify_params = {
                  'action': 'verify',
                  'prepay_id': prepay_id,
                  'order_id': order_id
                };
                $.ajax({
                  'type': "POST",
                  'url': location.pathname,
                  'data': verify_params,
                  'success': function(verify_ret) {
                    if (verify_ret && verify_ret.ok) {
                      alert('恭喜您，购课成功！');
                      wx.closeWindow();
                      return;
                    } else {
                      alert(verify_ret && verify_ret.msg || defaultErrMsg);
                    }
                    stopPaying();
                  },
                  'dataType': 'json',
                  'error': function() {
                    alert('获取支付结果失败');
                    stopPaying();
                  }
                });
              },
              fail: function(res) {
                $.ajax({ // 取消订单
                  'type': "DELETE",
                  'url': data.orders_api_url,
                  'success': function() {
                    stopPaying();
                  },
                  'error': function() {
                    stopPaying();
                  }
                });
              },
              //complete: function(){
              //    stopPaying();
              //},
              cancel: function() {
                $.ajax({ // 取消订单
                  'type': "DELETE",
                  'url': data.orders_api_url,
                  'success': function() {
                    stopPaying();
                  },
                  'error': function() {
                    stopPaying();
                  }
                });
              }
            });
          } else {
            // 未登录，跳转到登录页面
            if (result.code == 401) {
              var state = "LIVECLASS_" + liveClassId;
              var href = 'https://open.weixin.qq.com/connect/oauth2/authorize?appid=' + wx_appid
                  + '&redirect_uri=' + encodeURI(checkPhoneURI)
                  + '&response_type=code'
                  + '&scope=snsapi_base'
                  + '&state=' + state
                  + '&connect_redirect=1#wechat_redirect';
              location.href = href;
              return;
            }
            alert(result.msg);
            stopPaying();
          }
        } else {
          alert(defaultErrMsg);
          stopPaying();
        }
      },
      'dataType': 'json',
      'error': function() {
        alert(defaultErrMsg);
        stopPaying();
      }
    });
    e.stopPropagation();
  });
});
