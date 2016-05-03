$( document ).ready(function () {
	var color = $('#getcolor').text();
	if (color === '1') {
		$('#circle1').addClass('selected');
	} else if (color === '2') {
		$('#circle2').addClass('selected');
	} else if (color === '3') {
		$('#circle3').addClass('selected');
	} else if (color === '4') {
		$('#circle4').addClass('selected');
	} else if (color === '5') {
		$('#circle5').addClass('selected');
	} else if (color === '6') {
		$('#circle6').addClass('selected');
	}
	$('#cc1').click(function () {
		changecolor(1, function(s) {
			if (!s) return;
			$('.bigcircle').removeClass('selected');
			$('#circle1').addClass('selected');
		});
	});
	$('#cc2').click(function () {
		changecolor(2, function(s) {
			if (!s) return;
			$('.bigcircle').removeClass('selected');
			$('#circle2').addClass('selected');
		});
	});
	$('#cc3').click(function () {
		changecolor(3, function(s) {
			if (!s) return;
			$('.bigcircle').removeClass('selected');
			$('#circle3').addClass('selected');
		});
	});
	$('#cc4').click(function () {
		changecolor(4, function(s) {
			if (!s) return;
			$('.bigcircle').removeClass('selected');
			$('#circle4').addClass('selected');
		});
	});
	$('#cc5').click(function () {
		changecolor(5, function(s) {
			if (!s) return;
			$('.bigcircle').removeClass('selected');
			$('#circle5').addClass('selected');
		});
	});
	$('#cc6').click(function () {
		changecolor(6, function(s) {
			if (!s) return;
			$('.bigcircle').removeClass('selected');
			$('#circle6').addClass('selected');
		});
	});
	var changecolor = function (color, callback) {
		$.post('/changeColor', { color : color }, function (data) {
			callback(data.success);
		});
	};
});