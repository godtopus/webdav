/*jslint browser: true*/
/*jslint white: true */
/*global $, jQuery*/

$(window).load(function () {
	"use strict";
	
	var clickedElementName;
	var files;
	
	$(document).on("click", function (e) {
		if (e.which === 1) {
			$("#right_click_actions").css("display", "none");
			$("#user_resource_actions").css("display", "none");
			$("#pdf_frame").attr("src", "");
			$("#pdf_frame").css({
				position	: "",
				height		: "",
				width		: "",
				top			: "",
				left		: "",
				padding		: "",
				border		: "",
				'box-shadow': ""
			});
		}
	});

	$(document).on("contextmenu", function (e) {
		$("#user_resource_actions").css("display", "none");
		e.preventDefault();
		$("#right_click_actions").css({
			display: "block",
			position: "absolute",
			top: e.pageY,
			left: e.pageX
		});
		return false;
	});
	
	$("#user_resources tbody").on("contextmenu", "tr", function (e) {
		$("#right_click_actions").css("display", "none");
		e.preventDefault();
		$("#user_resource_actions").css({
			display: "block",
			position: "absolute",
			top: e.pageY,
			left: e.pageX
		});
		clickedElementName = $(this).children("td").eq(1).text();
		return false;
	});
	
	$("#rename").on("click", function (e) {
		if (e.which === 1) {
			$("#cover").fadeTo("slow", 0.8);
			$("#rename_resource").fadeIn("slow");
			$("#user_resource_actions").hide();
			$("#rename_resource input[name=given_name]").val(clickedElementName);
			$("#rename_resource input[name=given_name]").select();
		}
	});
	
	$("#rename_resource input[name=commit]").on("click", function (e) {
		if (e.which === 1) {
			$("#cover").fadeTo("slow", 0).hide();
			$(this).parent().fadeOut("slow");
			$(this).parent().trigger("reset");
			$.moveOrRenameResource(clickedElementName, $("#rename_resource input[name=given_name]").val(), $.encodeFolderUrl(), "rename");
		}
	});
	
	$("#move_to").on("click", function (e) {
		if (e.which === 1) {
			$("#move_to_resource ul").empty();
			$("#user_resources tbody").children("tr.folder").each(function () {
				if ($(this).children("td").eq(1).text() != clickedElementName) {
					$("#move_to_resource ul").append("<li><img src='" + window.location.origin + "/static/icons/folder.png'/><p>" + $(this).children("td").eq(1).text() + "</p></li>");
				}
			});
				
			$("#cover").fadeTo("slow", 0.8);
			$("#move_to_resource").fadeIn("slow");
			$("#user_resource_actions").hide();
		}
	});
	$("#move_to_resource ul").on("click", "li", function (e) {
		if (e.which === 1) {
			$("#move_to_resource ul li").removeClass("marked");
			$(this).addClass("marked");
		}
	});
	$("#move_to_resource input[name=commit]").on("click", function (e) {
		if (e.which === 1) {
			$("#cover").fadeTo("slow", 0).hide();
			$(this).parent().fadeOut("slow");
			$.moveOrRenameResource(clickedElementName, clickedElementName, $.encodeFolderUrl() + encodeURIComponent($("#move_to_resource ul li.marked p").text()) + "/", "move");
		}
	});
	
	$("#copy_resource").on("click", function (e) {
		if (e.which === 1) {
			$.createCopy(clickedElementName);
		}
	});
	
	/*$("#copy_to").on("click", function (e) {
		if (e.which === 1) {
			$("#copy_to_resource ul").empty();
			$("#user_resources tbody").children("tr.folder").each(function () {
				if ($(this).children("td").eq(1).text() != clickedElementName) {
					$("#copy_to_resource ul").append("<li><img src='" + window.location.origin + "/static/icons/folder.png'/><p>" + $(this).children("td").eq(1).text() + "</p></li>");
				}
			});
				
			$("#cover").fadeTo("slow", 0.8);
			$("#copy_to_resource").fadeIn("slow");
			$("#user_resource_actions").hide();
		}
	});
	
	$("#copy_to_resource ul").on("click", "li", function (e) {
		if (e.which === 1) {
			$("#copy_to_resource ul li").removeClass("marked");
			$(this).addClass("marked");
		}
	});
	
	$("#copy_to_resource input[name=commit]").on("click", function (e) {
		if (e.which === 1) {
			$("#cover").fadeTo("slow", 0).hide();
			$(this).parent().fadeOut("slow");
			$.copyResource(clickedElementName, $.encodeFolderUrl() + encodeURIComponent($("#copy_to_resource ul li.marked p").text()) + "/");
		}
	});*/
	
	$("#download_file").on("click", function (e) {
		e.preventDefault();
		if (e.which === 1) {
			$("#user_resource_actions").hide();
			$("#down a").attr("href", $.encodeFolderUrl() + encodeURIComponent(clickedElementName));
			$("#down a").attr("download", clickedElementName);
			$("#down a")[0].click();
			$("#down a").attr("href", "");
			$("#down a").attr("download", "");
		}
	});
	
	$("#delete_resource").on("click", function (e) {
		e.preventDefault();
		if (e.which === 1) {
			$("#user_resource_actions").hide();
			$.deleteResource(clickedElementName);
		}
	});
	
	$("#new_folder").on("click", function (e) {
		e.preventDefault();
		if (e.which === 1) {
			$("#cover").fadeTo("slow", 0.8);
			$("#name_folder").fadeIn("slow");
			$("#right_click_actions").hide();
			$("#name_folder input[name=given_name]").select();
		}
	});
	
	$("#name_folder input[name=create]").on("click", function (e) {
		if (e.which === 1) {
			$("#cover").fadeTo("slow", 0).hide();
			$(this).parent().fadeOut("slow");
			$(this).parent().trigger("reset");
			$.createFolder($("#name_folder input[name=given_name]").val());
		}
	});
	
	$("#new_file").on("click", function (e) {
		e.preventDefault();
		if (e.which === 1) {
			$("#cover").fadeTo("slow", 0.8);
			$("#name_file").fadeIn("slow");
			$("#right_click_actions").hide();
			$("#name_folder input[name=given_name]").select();
		}
	});
	
	$("#name_file input[name=create]").on("click", function (e) {
		console.log("creating file...");
		if (e.which === 1) {
			$("#cover").fadeTo("slow", 0).hide();
			$(this).parent().fadeOut("slow");
			$(this).parent().trigger("reset");
			$.createFile($("#name_file input[name=given_name]").val(), "", "text/plain");
		}
	});
	
	$("input[name=cancel]").on("click", function () {
        $("#cover").fadeTo("slow", 0).hide();
        $(this).parent().fadeOut("slow");
        $(this).parent().trigger("reset");
    });
	
	$("#upload").on("click", function (e) {
		e.preventDefault();
		if (e.which === 1) {
			$("#upload_file input[name=upload]").trigger("click");
			$("#right_click_actions").hide();
		}
	});
	
	$("#upload_file input[name=upload]").on("change", function (e) {
		files = this.files;
		$("#ajax_loader").css('display', 'block');
		
		for (var i = 0; i < files.length; i++) {
			$.uploadFile(files[i].name, files[i], files[i].type, files[i].size);
		}
		
		$("#ajax_loader").css('display', '');
	});
});