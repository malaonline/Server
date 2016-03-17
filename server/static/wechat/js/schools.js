//console.log(photosdic)

//测试用:大望路附近坐标:39.913608,116.482342

/*
$(document).on('click','[data-schoolid]',function (evt) {
    //photosdic = JSON.parse(photosdic);
    console.log(photosdic)
    schoolId = parseInt(evt.currentTarget.dataset.schoolid);
    photoList = photosdic[schoolId] || [];
    console.log('school的编号:'+schoolId+', URL列表: '+photoList)
});
*/

$(function(){
    $(document).on('click','[data-schoolid]',function (evt) {
        //photosdic = JSON.parse(photosdic);
        console.log(photosdic)
        schoolId = parseInt(evt.currentTarget.dataset.schoolid);
        photoList = photosdic[schoolId] || [];
        console.log('school的编号:'+schoolId+', URL列表: '+photoList)


        var schoolPhotosPopup = $.photoBrowser({
            photos : photoList,
            type: 'popup'
        });

        schoolPhotosPopup.open()
    });
})
