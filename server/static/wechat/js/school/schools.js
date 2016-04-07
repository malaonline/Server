//测试用:大望路附近坐标:116.482342,39.913608

$(function(){
    $(document).on('click','[data-schoolid]',function (evt) {
        schoolId = parseInt(evt.currentTarget.dataset.schoolid);
        photoList = photosdic[schoolId] || [];
        //console.log('school的编号:'+schoolId+', URL列表: '+photoList)

        var schoolPhotosPopup = $.photoBrowser({
            photos : photoList,
            type: 'popup',
            //theme: 'dark',
        });

        schoolPhotosPopup.open()
    });
});
