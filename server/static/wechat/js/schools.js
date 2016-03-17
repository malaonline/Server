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

/*=== 默认为 standalone ===*/
$(function(){
  /*=== 默认为 standalone ===*/
  var myPhotoBrowserStandalone = $.photoBrowser({
      photos : [
          '//img.alicdn.com/tps/i3/TB1kt4wHVXXXXb_XVXX0HY8HXXX-1024-1024.jpeg',
          '//img.alicdn.com/tps/i1/TB1SKhUHVXXXXb7XXXX0HY8HXXX-1024-1024.jpeg',
          '//img.alicdn.com/tps/i4/TB1AdxNHVXXXXasXpXX0HY8HXXX-1024-1024.jpeg',
      ]
  });
  //点击时打开图片浏览器
  $(document).on('click','.pb-standalone',function () {
    myPhotoBrowserStandalone.open();
  });

  /*=== Popup ===*/
  var myPhotoBrowserPopup = $.photoBrowser({
      photos : [
          '//img.alicdn.com/tps/i3/TB1kt4wHVXXXXb_XVXX0HY8HXXX-1024-1024.jpeg',
          '//img.alicdn.com/tps/i1/TB1SKhUHVXXXXb7XXXX0HY8HXXX-1024-1024.jpeg',
          '//img.alicdn.com/tps/i4/TB1AdxNHVXXXXasXpXX0HY8HXXX-1024-1024.jpeg',
      ],
      type: 'popup'
  });
  $(document).on('click','.pb-popup',function () {
    myPhotoBrowserPopup.open();
  });

  /*=== 有标题 ===*/
  var myPhotoBrowserCaptions = $.photoBrowser({
      photos : [
          {
              url: '//img.alicdn.com/tps/i3/TB1kt4wHVXXXXb_XVXX0HY8HXXX-1024-1024.jpeg',
              caption: 'Caption 1 Text'
          },
          {
              url: '//img.alicdn.com/tps/i1/TB1SKhUHVXXXXb7XXXX0HY8HXXX-1024-1024.jpeg',
              caption: 'Second Caption Text'
          },
          // 这个没有标题
          {
              url: '//img.alicdn.com/tps/i4/TB1AdxNHVXXXXasXpXX0HY8HXXX-1024-1024.jpeg',
          },
      ],
      theme: 'dark',
      type: 'standalone'
  });
  $(document).on('click','.pb-standalone-captions',function () {
    myPhotoBrowserCaptions.open();
  });

  /*=== 有视频 ===*/
  var myPhotoBrowserVideo = $.photoBrowser({
      photos : [
          {
              html: '<iframe src="//www.youtube.com/embed/lmc21V-zBq0?list=PLpj0FBQgLGEr3mtZ5BTwtmSwF1dkPrPRM" frameborder="0" allowfullscreen></iframe>',
              caption: 'Woodkid - Run Boy Run (Official HD Video)'
          },
          {
              url: 'http://lorempixel.com/1024/1024/sports/2/',
              caption: 'Second Caption Text'
          },
          {
              url: 'http://lorempixel.com/1024/1024/sports/3/',
          },
      ],
      theme: 'dark',
      type: 'standalone'
  });
  $(document).on('click','.pb-standalone-video',function () {
    myPhotoBrowserVideo.open();
  });
})