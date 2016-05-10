$( document ).ready(function () {
	var isOwner = $('#isOwner').text();
	var isPub = $('#isPub').text();
	if (isOwner == '0') {
		$('#optionsRadios1').attr('disabled','disabled');
		$('#optionsRadios2').attr('disabled','disabled');
		$('#unInput').attr('disabled','disabled');
		$('#addbtn').attr('disabled','disabled');
		$('#updatebtn').attr('disabled','disabled');
	}
	if (isPub == '0') {
		$('#optionsRadios1').attr('checked', 'checked');
	} else {
		$('#optionsRadios2').attr('checked', 'checked');
	}

	$('button#updatebtn').click(function () {
		var radioVal = $('input[name=optionsRadios]:checked').val();
		var id = $('#dataid').text();
		var jsonData = {
			id: id,
			isPub: radioVal
		};
		$.post('/updatePub', jsonData, function (data) {
			if (data.success) {
				location.reload();
			}
		});
	});

	$('button#addbtn').click(function () {
		var viewer = $('#unInput').val();
		if (viewer === '') {
			$('#issue').text('Enter a username to add as viewer.');
			return;
		}
		var id = $('#dataid').text();
		var jsonData = {
			id: id,
			viewer: viewer
		};
		$.post('/addViewer', jsonData, function (data) {
			if (data.success) {
				location.reload();
			} else {
				$('#issue').text(data.msg);
				return;
			}
		});
	});
});