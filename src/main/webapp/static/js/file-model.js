$(document).ready(function() {
	
	/*function Page() {
		var self = this;
		self.page = ko.observable();
		
		this.pageText = koi
	}
	
	function File() {
		var self = this;
		
		//self.page = ko.observable();
		self.page = ko.observableArray();
	};
	
	ko.applyBindings(new Page());*/
	
	//var tid = setInterval(mycode, 500);
	/*function mycode() {
		$.ajax({
			type: "POST",
			url: "http://localhost:8080/templates/edit_handler.php",
			data: { type: "get", user: window.location.hash.substring(1) },
			dataType: "json",
			success: function (resp) {
				alert();
				//$("#file_content tr").eq(resp.page).val(resp.text);
				mycode();
			}
		});
	}*/
	
	$("#file_content").on("keypress", ".page", function (e) {
		console.log("sending...");
		$.ajax({
			type: "POST",
			url: "http://localhost:8080/templates/edit_handler.php",
			data: { type: "post", user : window.location.hash.substring(1), page: $(this).index() + 1, text:  $(this).val() },
			dataType: "json",
			success: function () {
			}
		});
	});
	
	//mycode();
});