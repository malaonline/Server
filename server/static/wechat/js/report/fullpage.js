$(function() {

});

fullpage.initialize('#fullpage', {
    anchors: ['firstPage', 'secondPage', '3rdPage', '4thpage', '5thpage', '6thpage', 'lastPage'],
    //menu: '#menu',
    css3: true,
    //sectionsColor: ['#f34f59', '#36d3ae', '#ffdf76', '#6ccbeb', '#f34f59', '#2f3440'],
    //responsiveHeight: 600,
    navigation: false,
    slidesNavigation: false,
    //keyboardScrolling: true,
    recordHistory: false,
    onLeave: function(last, dest, direction) {
        console.log(dest-1);
        var $section = $("#section"+(dest-1));
        console.log($section.offset());
        console.log($section.position());
    }
});

$(document).on('click', 'a.next-section', function() {
    fullpage.moveSectionDown();
});
