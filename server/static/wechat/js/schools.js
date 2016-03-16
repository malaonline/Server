function refreshDistance(lat,lng) {
    schoolDistanceNodeList = $("[data-school-latitude]");
    console.log(schoolDistanceNodeList)
    schoolDistanceNodeList.each(function() {
        console.log(
            $(this)[0].dataset.schoolLatitude,
            $(this)[0].dataset.schoolLongitude
        )
    })
}

