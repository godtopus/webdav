/*jslint browser: true*/
/*jslint white: true */
/*global $, jQuery*/

$(window).load(function () {
	"use strict";
	
	var currentDate, jsonObj, jsonStr, modifiedDate;
	
	function createPopUp(name) {
		$("#pop_up p").text("Created folder " + name);
		$("#pop_up").fadeIn(3000);
        $("#pop_up").fadeOut(3000);
		$("#pop_up p").trigger("reset");
	}

	$.createFolder = function (folderName) {
		$.ajax({
			type: "MKCOL",
			url: $.encodeFolderUrl() + folderName, //+ "_DAV/MKCOL",
			//data: { name: folderName },
			//dataType: "json",
			beforeSend: function() {
				$("#ajax_loader").css('display', 'block');
			},
			success: function() {
				/*currentDate = new Date().getTime();
				jsonObj = JSON.stringify(resp);
				jsonStr = JSON.parse(jsonObj);
				
				modifiedDate = jsonStr.data.modifiedDate;
				modifiedDate = JSON.parse(JSON.stringify(modifiedDate));*/
				
				$.displayResource(folderName, null, null, 0);
				//createPopUp(folderName);
			},
			error: function(jqXHR, textStatus, errorThrown) {
				alert('There was a problem creating the folder ' + folderName);
			},
			complete: function() {
				$("#ajax_loader").css('display', '');
			},
		});
	}
});