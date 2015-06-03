/*jslint browser: true*/
/*jslint white: true */
/*global $, jQuery*/

$(window).load(function () {
	"use strict";
	
	$.createCopy = function (name) {
		$.ajax({
			type: "COPY",
			url: $.encodeFolderUrl() + encodeURIComponent(name),
			beforeSend: function(xmlhttp) {
				$("#ajax_loader").css('display', 'block');
				xmlhttp.setRequestHeader("Destination", $.encodeFolderUrl() + encodeURIComponent("copy of " + name));
			},
			success: function() {
				$("#user_resources tbody").children("tr").each(function () {
					if ($(this).children("td").eq(1).text() == name) {
						$.displayResourceCopy("copy of " + name, $(this));
					}
				});
			},
			error: function(jqXHR, textStatus, errorThrown) {
				alert("Sorry, an error occured copying " + name + ". Please check your internet connection");
			},
			complete: function() {
				$("#ajax_loader").css('display', '');
			},
		});
	}
	
	/*$.copyResource = function (name, newParentUrl) {
		$.ajax({
			type: "MOVE",
			url: $.encodeFolderUrl() + encodeURIComponent(name),
			beforeSend: function(xmlhttp) {
				$("#ajax_loader").css('display', 'block');
				xmlhttp.setRequestHeader("Destination", newParentUrl + encodeURIComponent(name));
			},
			success: function() {
				$("#user_resources tbody").children("tr").each(function () {
					if ($(this).children("td").eq(1).text() == name) {
						$.displayResourceCopy(name, $(this));
					}
				});
			},
			error: function(jqXHR, textStatus, errorThrown) {
				alert("Sorry, an error occured copying " + name + ". Please check your internet connection");
			},
			complete: function() {
				$("#ajax_loader").css('display', '');
			},
		});
	}*/
});