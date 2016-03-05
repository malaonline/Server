$(document).ready(function() {
			$('#fullpage').fullpage({
				anchors: ['firstPage', 'secondPage', '3rdPage','4thPage','5thPage','footer'],
				sectionsColor: ['#f34f59', '#36d3ae', '#ffdf76','#6ccbeb','#f34f59','#2f3440'],
				responsiveHeight: 600,
                navigation: true,
				navigationPosition: 'right',
				//navigationTooltips: ['First', 'Second', 'Third']
                //Accessibility
                keyboardScrolling: true,
			});
		});