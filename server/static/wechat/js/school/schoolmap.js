var map = new AMap.Map('container',{
    zoom: 14,
    center: [lng,lat]
});

var marker = new AMap.Marker({
        position: [lng,lat],
        map:map
    });

var infowindow = new AMap.InfoWindow({
    content: '<h3>'+schoolname+'</h1><div>'+schooladd+'</div>',
    offset: new AMap.Pixel(0, -30),
    size:new AMap.Size(160,0)
})

infowindow.open(map,new AMap.LngLat(lng, lat));
