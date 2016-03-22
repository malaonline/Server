var init = function() {
    var center = new qq.maps.LatLng(lat,lng);
    console.log(lat,lng)
    console.log(center.getLat())
    var map = new qq.maps.Map(document.getElementById('container'),{
        center: center,
        zoom: 13
    });
    //创建marker
    var marker = new qq.maps.Marker({
        position: center,
        map: map,
        zIndex:900
    });

    //创建缩放控制
    var scaleControl = new qq.maps.ScaleControl({
        align: qq.maps.ALIGN.BOTTOM_LEFT,
        margin: qq.maps.Size(85, 15),
        map: map
    });

    //创建文本标签
    labeltext= '<h4>'+schoolname+'</h4><p>'+schooladd+'</p>';
    var cssC = {
        padding: "14px 10px 12px 10px",
        border: "2px solid #bababa",
        'border-radius':"8px",
        width:'120px',
        background:"rgba(255, 255, 255, 0.96)"
    };

    var label = new qq.maps.Label({
        position: center,
        map: map,
        content: labeltext,
        //Label样式。
        style: cssC,
        offset: new qq.maps.Size(-70,-110),
        zIndex:1000
    });
};

window.onload = init();