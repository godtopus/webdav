/*jslint browser: true*/
/*global $, jQuery*/

$(document).ready(function () {
    "use strict";
	
	$("#user_resources tbody").on("mouseenter", "tr", function () {
		$(this).css('background-color', '#E5EDED');
		$(this).children().last().children().css('display', 'inline');
	});
	$("#user_resources tbody").on("mouseleave", "tr", function () {
		$(this).css('background-color', '');
		$(this).children().last().children().css('display', '');
	});
});