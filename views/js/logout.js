$( document ).ready(function () {
	$('#logoutBtn').click(function () {
		$.post('/logout', {}, function (data) {
			if (!data.success) {
				$('#issue').text(data.msg);
			} else {
				window.location = '/';
			}
		});
	});
});