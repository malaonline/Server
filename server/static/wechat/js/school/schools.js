/**
 * Created by liumengjun on 2016-04-13.
 */

wx.error(function (res) {
});

function refreshDistance(lat, lng) {
    schoolDistanceNodeList = $("[data-school-latitude]");
    schoolDistanceNodeList.each(function () {
        schoolLatitude = parseFloat($(this)[0].dataset.schoolLatitude);
        schoolLongitude = parseFloat($(this)[0].dataset.schoolLongitude);
        var distance = CoolWPDistance(lat, lng, schoolLatitude, schoolLongitude);
        $(this).text((distance && !isNaN(distance)) ? (distance + 'km') : '');
    })
}

wx.ready(function (res) {
    wx.getLocation({
        type: 'wgs84', // 默认为wgs84的gps坐标，如果要返回直接给openLocation用的火星坐标，可传入'gcj02'
        success: function (res) {
            refreshDistance(res.latitude, res.longitude);
        },
        fail: function (res) {
        }
    });
});

$(function () {
    var adjustImgSize = function (img_ele) {
        var w = img_ele.width, h = img_ele.height;
        if (!img_ele.src || !w || !h || isNaN(w) || isNaN(h)) return;
        var $img = $(img_ele), $imgBox = $img.closest('div'), out_width = $imgBox.width(), out_height = $imgBox.height();
        if (h < out_height) {
            $img.css('height', out_height + 'px');
            var new_width = (w * out_height / h);
            $img.css('max-width', new_width + 'px');
            if (new_width > out_width) {
                var left = (w - new_width) / 2;
                $img.css('width', new_width + 'px');
                $img.css('margin-left', left + 'px');
            }
        } else {
            var top = (out_height - h) / 2;
            $img.css('margin-top', top + 'px');
        }
    };
    $('.cover-retro-fixer').each(function (e) {
        var $div = $(this), coversrc = $div.data('coversrc');
        $div.find('img')[0].src = coversrc;
    });
    $('.cover-retro-fixer img').bind('load', function (e) {
        adjustImgSize(this);
    });
    $('.cover-retro-fixer img').each(function (e) {
        adjustImgSize(this);
    });
});
