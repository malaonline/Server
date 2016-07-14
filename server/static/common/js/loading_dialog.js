/**
 * Created by liumengjun on 2016-04-11.
 * 注: 不用于微信端, 微信端自行处理
 */
function showLoading(msg) {
    var $loadingDialog = $('#mLoadingDialog');
    var $loadingText = $("#mLoadingText");
    $loadingText.html(msg ? msg : "");
    $loadingDialog.show();
}
function hideLoading() {
    $('#mLoadingDialog').hide();
}
var DEFAULT_ERR_MSG = '请求失败, 请稍后重试, 或联系管理员!';
function _malaAjax0(method, url, data, success, dataType, error, loadingMsg) {
    if ($.isFunction(data)) {
        loadingMsg = error;
        error = dataType;
        dataType = success;
        success = data;
        data = undefined;
    }
    if ($.isFunction(dataType)) {
        loadingMsg = error;
        error = dataType;
        dataType = undefined;
    }
    showLoading(loadingMsg);
    return $.ajax({
        url: url,
        type: method,
        dataType: dataType,
        data: data,
        success: function (result, textStatus, jqXHR) {
            if (typeof(success) === 'function') {
                success(result, textStatus, jqXHR);
            }
            hideLoading();
        },
        error: function (jqXHR, errorType, errorDesc) {
            if (typeof(error) === 'function') {
                error(jqXHR, errorType, errorDesc);
            }
            hideLoading();
        }
    });
}
function malaAjaxPost(url, data, success, dataType, error, loadingMsg) {
    return _malaAjax0('post', url, data, success, dataType, error, loadingMsg);
}
function malaAjaxGet(url, data, success, dataType, error, loadingMsg) {
    return _malaAjax0('get', url, data, success, dataType, error, loadingMsg);
}
