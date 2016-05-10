$( document ).ready(function () {
	$('button#searchbtn').click(function () {
		var query = $('#searchbar').val();
		if (query === '') {
			return;
		}
		var jsonData = {
			query: query
		};
		$.post('/search', jsonData, function (data) {
			if (data.success) {
				var s = new sigma(
					{ 
				    graph: data.tree,
				    container: 'searchresults',
				    settings: {
				        defaultNodeColor: '#2980b9'
				  	}
					}
				);
			}
		});
	});
});