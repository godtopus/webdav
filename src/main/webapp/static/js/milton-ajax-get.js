/*jslint browser: true*/
/*jslint white: true */
/*global $, jQuery*/

$(window).load(function () {
	"use strict";
	
	history.replaceState(null, null, window.location.hash.split("=").pop());
	window.location.href.replace("/ROOT/", "/my-syncberry/files/");
	
	function encodeFileUrl() {
		return window.location.href.replace("/my-syncberry/files/", "/ROOT/");
	}
	
	function loadFile() {
		$.ajax({
			type: "GET",
			url: encodeFileUrl(),
			dataType: "text",
			beforeSend: function() {
				$("#ajax_loader").css('display', 'block');
			},
			success: function (contents) {
				$(document).attr("title", decodeURI(window.location.href.split("/").pop()));
				$(".page:first-child").val(decodeURI(contents));
				$(".page:first-child")[0].selectionStart = 0;
				$(".page:first-child")[0].selectionEnd = 0;
				$(".page:first-child").focus();
			},
			complete: function () {
				$("#ajax_loader").css('display', '');
			},
		});
	}
	
	loadFile();
});