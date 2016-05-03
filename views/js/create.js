$( document ).ready(function () {
	$('button#createBtn').click(function () {
		var email = $('#emailInput').val();
		var username = $('#unInput').val();
		var password1 = $('#passwordInput').val();
		var password2 = $('#confirmPasswordInput').val();

		if (email === '' || password1 === '' || username === '') {
			$('#issue').text('Please complete all fields!');
			return;
		}

		if (password1 !== password2) {
			$('#issue').text('Passwords do not match!');
			return;
		}

		var postData = {
			email: email,
			username: username,
			password: password1
		};

		$.post('/create', postData, function (data) {
			if (!data.success) {
				$('#issue').text(data.msg);
			} else {
				window.location = '/home';
			}
		});
	});
});