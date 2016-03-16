function refreshDistance(lat,lng) {
    schoolDistanceNodeList = $("[data-school-latitude]");
    console.log(schoolDistanceNodeList)
    schoolDistanceNodeList.each(function() {
        schoolLatitude = parseFloat($(this)[0].dataset.schoolLatitude);
        schoolLongitude = parseFloat($(this)[0].dataset.schoolLongitude);
        $(this).text('<'+CoolWPDistance(lat,lng,schoolLatitude,schoolLongitude)+'km')
    })
}

//测试用:大望路附近坐标:39.913608,116.482342