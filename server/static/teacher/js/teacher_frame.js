/**
 * Created by liumengjun on 2016-04-11.
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
