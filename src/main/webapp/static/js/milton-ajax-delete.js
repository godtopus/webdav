/*jslint browser: true*/
/*jslint white: true */
/*global $, jQuery*/

$(window).load(function () {
	"use strict";
	
	function deletePopUp(name) {
		$("#pop_up p").text("Raderade " + name);
		$("#pop_up").fadeIn(3000);
        $("#pop_up").fadeOut(3000);
		$("#pop_up p").trigger("reset");
	}

	$.deleteResource = function (name) {
		$.ajax({
			type: "DELETE",
			url: $.encodeFolderUrl() + name,
			dataType: "json",
			beforeSend: function() {
				$("#ajax_loader").css('display', 'block');
			},
			success: function() {
				$("#user_resources tbody").children("tr").each(function () {
					if ($(this).children("td").eq(1).text() == name) {
						$(this).remove();
						return false;
					}
				});
				
				//deletePopUp(name);
			},
			error: function(jqXHR, textStatus, errorThrown) {
				alert("Sorry, an error occured deleting " + name + ". Please check your internet connection");
			},
			complete: function() {
				$("#ajax_loader").css('display', '');
			},
		});
	}
	
	$("#user_resources tbody").on("click", "tr td .actionmenu .delete", function (e) {
		if (e.which === 1) {
			$.deleteResource($(this).parents("tr").children("td").eq(1).text());
		}
	});
});