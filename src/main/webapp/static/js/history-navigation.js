/*jslint browser: true*/
/*jslint white: true */
/*global $, jQuery*/

$(window).load(function () {
	"use strict";
	
	$.encodeFolderUrl = function () {
		return window.location.href.replace("/my-syncberry/folders/", "/ROOT/");
	}
	
	$(window).on("keydown", function (e) {
		if (e.which == 116) {
			e.preventDefault();
			console.log(window.location.href);
			$.loadDir("", false);
		}
	});
	
	$(window).on("popstate", function (e) {
		console.log(window.location.href);
		console.log(e.originalEvent.state);
		$.loadDir("", false);
	});
});