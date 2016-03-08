$(document).ready(function() {
			$('#fullpage').fullpage({
				anchors: ['firstPage', 'secondPage', '3rdPage','4thPage','footer'],
				sectionsColor: ['#48cbec', '#42cc7f', '#f1c566','#1acbe9','#2f3440'],
				responsiveHeight: 600,
                navigation: true,
				navigationPosition: 'right',
				//navigationTooltips: ['First', 'Second', 'Third']
                //Accessibility
                keyboardScrolling: true,
			});
		});

$(document).on('click', 'a.next-section', function(){
  $.fn.fullpage.moveSectionDown();
});