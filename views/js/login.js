$( document ).ready(function () {
	$('button#loginBtn').click(function () {
		var username = $('#emailInput').val();
		var password = $('#passwordInput').val();

		if (username === '' || password === '') {
			$('#issue').text('Please enter username and password to login!');
			return;
		}

		var postData = {
			username: username,
			password: password
		};

		$.post('/login', postData, function (data) {
			if (!data.success) {
				$('#issue').text(data.msg);
			} else {
				window.location = '/home';
			}
		});
	});
});