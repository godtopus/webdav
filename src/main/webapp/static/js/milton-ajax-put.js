/*jslint browser: true*/
/*jslint white: true */
/*global $, jQuery*/

$(window).load(function () {
	"use strict";
	
	var currentDate, jsonObj, jsonStr, modifiedDate, processedData = "";
	
	function savePopUp(name) {
		$("#pop_up p").text("Sparade " + name);
		$("#pop_up").fadeIn(3000);
        $("#pop_up").fadeOut(3000);
		$("#pop_up p").trigger("reset");
	}
	
	function createPopUp(name) {
		$("#pop_up p").text("Skapade " + name);
		$("#pop_up").fadeIn(3000);
        $("#pop_up").fadeOut(3000);
		$("#pop_up p").trigger("reset");
	}

	$.saveFile = function(fileData) {
		fileData.children().children().each(function () {
			processedData = processedData + $(this).val();
		});
		
		//processedData = processedData.replace("\n", "\r\n");
		processedData = encodeURI(processedData);
		
		$.ajax({
			type: "PUT",
			url: $.encodeFolderUrl(),
			contentType: "text/plain",
			data: processedData,
			dataType: "json",
			beforeSend: function() {
				processedData = "";
				$("#ajax_loader").css('display', 'block');
			},
			success: function(resp) {
				//savePopUp(window.location.hash.split("/").pop());
			},
			error: function(jqXHR, textStatus, errorThrown) {
				alert("Sorry, an error occured saving " + name + ". Please check your internet connection");
			},
			complete: function() {
				$("#ajax_loader").css('display', '');
			},
		});
	}
	
	$.createFile = function(fileName, fileData, fileType) {
		$.ajax({
			type: "PUT",
			url: $.encodeFolderUrl() + fileName,
			contentType: fileType,
			data: fileData,
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
				
				$.displayResource(fileName, fileType, fileData.length, 0);
				
				//createPopUp(fileName);
			},
			error: function(jqXHR, textStatus, errorThrown) {
				alert("Sorry, an error occured saving " + textStatus + ". Please check your internet connection");
			},
			complete: function() {
				$("#ajax_loader").css('display', '');
			},
		});
	}
	
	$.uploadFile = function(fileName, fileData, fileType, contentLength) {
		$.ajax({
			type: "PUT",
			url: $.encodeFolderUrl() + fileName,
			contentType: fileType,
			processData: false,
			data: fileData,
			success: function() {
				$.displayResource(fileName, fileType, contentLength, 0);
			},
			error: function(jqXHR, textStatus, errorThrown) {
				alert("Sorry, an error occured saving " + textStatus + ". Please check your internet connection");
			},
		});
	}
});