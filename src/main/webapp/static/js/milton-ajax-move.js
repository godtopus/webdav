/*jslint browser: true*/
/*jslint white: true */
/*global $, jQuery*/

$(window).load(function () {
	"use strict";

	$.moveOrRenameResource = function (oldName, newName, newParentUrl, mode) {
		console.log(oldName + " " + newName + " " + newParentUrl);
		$.ajax({
			type: "MOVE",
			url: $.encodeFolderUrl() + encodeURIComponent(oldName),
			beforeSend: function(xmlhttp) {
				$("#ajax_loader").css('display', 'block');
				xmlhttp.setRequestHeader("Destination", newParentUrl + encodeURIComponent(newName));
			},
			success: function() {
				$("#user_resources tbody").children("tr").each(function () {
					if ($(this).children("td").eq(1).text() == oldName) {
						if (mode == "move") {
							$(this).remove();
						} else if (mode == "rename") {
							$(this).children("td").eq(1).text(newName);
						}
						return false;
					}
					
					if ($(this).children("td").eq(1).text() == newName && mode == "move") {
						$(this).children("td").eq(2).text("Less than 1 minute ago");
					}
				});
			},
			error: function(jqXHR, textStatus, errorThrown) {
				alert("Sorry, an error occured renaming " + oldName + ". Please check your internet connection");
			},
			complete: function() {
				$("#ajax_loader").css('display', '');
			},
		});
	}
});