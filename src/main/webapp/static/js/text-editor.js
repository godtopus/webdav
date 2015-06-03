/*jslint browser: true*/
/*jslint white: true */
/*global $, jQuery*/

$(window).load(function () {
	"use strict";
	
	$(window).on("keydown", function(event) {
		if (event.ctrlKey || event.metaKey) {
			if (String.fromCharCode(event.which).toLowerCase() == "s") {
				console.log("saving...");
				event.preventDefault();
				$.saveFile($("#file_content"));
			}
		}
	});
	
	$("#file_content").on("keydown", ".wrapper .page", function (e) {
		var temp = $(this).val().split("\n");
		
		if ((temp.length == $(this).attr('rows')) && e.which === 13) {
			e.preventDefault();
			$(this).parent().after("<div class='wrapper'><textarea class='page' rows='47' cols='79' spellcheck='false'></textarea></div>");
			$(this).parent().next().children()[0].selectionStart = 0;
			$(this).parent().next().children()[0].selectionEnd = 0;
			$(this).parent().next().children().focus();
			$(window).scrollTop($(window).scrollTop() - 600);
		} else if ((temp.length == $(this).attr('rows')) && (temp.pop().length == $(this).attr('cols')) && e.which !== 8) {
			e.preventDefault();
			$(this).parent().after("<div class='wrapper'><textarea class='page' rows='47' cols='79' spellcheck='false'></textarea></div>");
			if (e.ctrlKey || e.metaKey) {
				$(this).parent().next().children().val(String.fromCharCode(e.which));
			} else {
				$(this).parent().next().children().val(String.fromCharCode(e.which).toLowerCase());
			}
			$(this).parent().next().children()[0].selectionStart = 1;
			$(this).parent().next().children()[0].selectionEnd = 1;
			$(this).parent().next().children().focus();
			$(window).scrollTop($(window).scrollTop() - 600);
		} else if ($(this).val().trim().length === 0 && $(this).parent().index() !== 0 && e.which === 8) {
			e.preventDefault();
			$(this).parent().prev().children().focus();
			$(this).parent().remove();
		}
		//alert($(document).height() - $(window).height() - $(window).scrollTop());
	});
});