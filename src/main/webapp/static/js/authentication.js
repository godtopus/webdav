/*jslint browser: true*/
/*jslint white: true */
/*global $, jQuery*/

$(window).load(function () {
	"use strict";
	
	$("#submit_sign_in").on("click", function(e) {
		e.preventDefault();
		$.ajax({
			type: "POST",
			url: window.location.origin + "/users/_DAV/POST",
			data: { sign_in: "true", username: $("#sign_in .username").val(), password:  $("#sign_in .password").val() },
			dataType: "json",
			success: function(respUrl) {
				if (respUrl.data != null) {
					location.replace(respUrl.data);
				} else {
					$("#sign_in form").trigger("reset");
					$("#sign_in .username").addClass("invalid");
					$("#sign_in .password").addClass("invalid");
				}
			},
			error: function(jqXHR, textStatus, errorThrown) {
				alert('There was a problem signing in');
			}
		});
	});
	
	$("#submit_sign_up").on("click", function(e) {
		e.preventDefault();
		$.ajax({
			type: "POST",
			url: window.location.origin + "/users/_DAV/POST",
			data: { sign_up: "true", username: $("#sign_up .username").val(), password:  $("#sign_up .password").val() },
			dataType: "json",
			success: function(respUrl) {
				if (respUrl.data != null) {
					location.replace(respUrl.data);
				} else {
					$("#sign_up form").trigger("reset");
					$("#sign_up .username").addClass("invalid");
					$("#sign_up #password1").addClass("invalid");
					$("#sign_up #password2").addClass("invalid");
				}
			},
			error: function(jqXHR, textStatus, errorThrown) {
				alert('There was a problem signing up');
			}
		});
	});
	
	$("#nav").on("click", "li:nth-child(1):not(.marked)", function () {
		$("#sign_up").trigger("reset");
		$(this).next().removeClass("marked");
		$(this).addClass("marked");
		$("#sign_up").css('display', 'none');
		$("#sign_in").css('display', 'block');
    });
	
	$("#nav").on("click", "li:nth-child(2):not(.marked)", function () {
		$("#sign_up").trigger("reset");
		$(this).prev().removeClass("marked");
		$(this).addClass("marked");
		$("#sign_in").css('display', 'none');
		$("#sign_up").css('display', 'block');
    });
});