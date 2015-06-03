/*jslint browser: true*/
/*jslint white: true */
/*global $, jQuery*/

$(window).load(function () {
	"use strict";
	
	$.currentUserHome = window.location.hash.split("=").pop();
	history.pushState(null, null, "/my-syncberry/folders/");
	
	var i, currentDate, jsonObj, jsonStr;
	
	$.loadDir = function(folderName, pushState) {
		$("#user_resources tbody").empty();
		
		$.ajax({
			type: "GET",
			url: $.encodeFolderUrl() + folderName + "/_DAV/PROPFIND?fields=name,getcontenttype,getcontentlength,getlastmodified",
			dataType: "json",
			beforeSend: function() {
				$("#ajax_loader").css('display', 'block');
			},
			success: function(resp) {
				if (pushState) {
					history.pushState(resp[0].name, null, resp[0].name + "/");
				} else {
					//history.pushState(resp[0].name, null, "");
				}
				currentDate = new Date().getTime();
				
				for (i = 1; i < resp.length; i++) {
					jsonObj = JSON.stringify(resp[i].getlastmodified);
					jsonStr = JSON.parse(jsonObj);
				
					$.displayResource(resp[i].name, resp[i].getcontenttype, resp[i].getcontentlength, currentDate - jsonStr.time);
				}
			},
			error: function(jqXHR, textStatus, errorThrown) {
				alert("Sorry, an error occured loading " + folderName + ". Please check your internet connection");
			},
			complete: function() {
				$("#ajax_loader").css('display', '');
			},
		});
	}
	
	
	$("#user_resources tbody").on("click", ".folder td:nth-child(2)", function (e) {
		if (e.which === 1) {
			$.loadDir($(this).text(), true);
		}
	});
	
	$("#user_resources tbody").on("click", ".file td:nth-child(2)", function (e) {
		if (e.which === 1) {
			if ($(this).text().split(".").pop() == "pdf") {
				$("#pdf_frame").attr("src", $.encodeFolderUrl() + $(this).text());
				$("#pdf_frame").css({
					position	: "fixed",
					height		: "78.5%",
					width		: "74%",
					top			: "8.6%",
					left		: "10%",
					padding		: "3%",
					border		: "1px solid black",
					background	: "#FFFFFF",
					'box-shadow': "0 0 5px #999"
				});
			} else if (($(this).text().split(".").pop() == "jpg") || ($(this).text().split(".").pop() == "jpeg") || ($(this).text().split(".").pop() == "png")) {
				console.log("image...");
				$("#image a").attr("href", $.encodeFolderUrl() + encodeURIComponent($(this).text().split("/").pop()));
				$("#image a")[0].click();
				$("#image a").attr("href", "");
			} else {
				window.open(window.location.origin + "/templates/editFilePage.xhtml" + "#path=" + $.encodeFolderUrl() + $(this).text());
			}
		}
		
		return false;
	});
	
	$.loadDir($.currentUserHome, true);
});