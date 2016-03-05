$(document).ready(function() {
			$('#fullpage').fullpage({
				anchors: ['firstPage', 'secondPage', '3rdPage','4thPage','footer'],
				sectionsColor: ['#1acbe9', '#17cc83', '#f5c56f','#1acbe9','#2f3440'],
				responsiveHeight: 600,
                navigation: true,
				navigationPosition: 'right',
				//navigationTooltips: ['First', 'Second', 'Third']
                //Accessibility
                keyboardScrolling: true,
			});
		});