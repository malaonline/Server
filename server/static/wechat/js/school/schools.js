/**
 * Created by liumengjun on 2016-04-13.
 */

wx.error(function (res) {
    console.log(res);
});

function refreshDistance(lat, lng) {
    schoolDistanceNodeList = $("[data-school-latitude]");
    //console.log(schoolDistanceNodeList)
    schoolDistanceNodeList.each(function () {
        schoolLatitude = parseFloat($(this)[0].dataset.schoolLatitude);
        schoolLongitude = parseFloat($(this)[0].dataset.schoolLongitude);
        console.log(schoolLatitude + ',' + schoolLongitude);
        var distance = CoolWPDistance(lat, lng, schoolLatitude, schoolLongitude);
        $(this).text((distance && !isNaN(distance)) ? ('<' + distance + 'km') : '距离未知');
    })
}

wx.ready(function (res) {
    wx.getLocation({
        type: 'wgs84', // 默认为wgs84的gps坐标，如果要返回直接给openLocation用的火星坐标，可传入'gcj02'
        success: function (res) {
            console.log(res);
            refreshDistance(res.latitude, res.longitude);
        },
        fail: function (res) {
            console.log(res);
        }
    });
});

$(function () {
    var COVER_IMG_HEIGHT = 182;
    var adjustImgSize = function (img_ele) {
        var w = img_ele.width, h = img_ele.height;
        if (!w && !h || isNaN(w) || isNaN(h)) return;
        var $img = $(img_ele), out_width = $img.closest('div').width();
        console.log(w + ' x ' + h + ' in (' + w + 'x' + out_width + ')');
        if (h < COVER_IMG_HEIGHT) {
            $img.css('height', COVER_IMG_HEIGHT + 'px');
            var new_width = (w * COVER_IMG_HEIGHT / h);
            $img.css('max-width', new_width + 'px');
            if (new_width > out_width) {
                var left = (w - new_width) / 2;
                $img.css('margin-left', left + 'px');
            }
        } else {
            var top = (COVER_IMG_HEIGHT - h) / 2;
            $img.css('margin-top', top + 'px');
        }
    };
    $('.cover-retro-fixer img').each(function (e) {
        adjustImgSize(this);
    });
    $('.cover-retro-fixer img').bind('load', function (e) {
        adjustImgSize(this);
    });
});
