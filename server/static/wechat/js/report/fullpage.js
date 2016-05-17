$(function() {
    $('a.next-section').bind('click tap', function() {
        fullpage.moveSectionDown();
    });
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
    recordHistory: false
});
