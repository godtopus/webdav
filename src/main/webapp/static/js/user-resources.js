/*jslint browser: true*/
/*jslint white: true */
/*global $, jQuery*/

$(window).load(function () {
	"use strict";
	
	var actions = "<td><div class='actionmenu'><img class='share' src='" + window.location.origin + "/static/icons/share.png'></img><img class='delete' src='" + window.location.origin + "/static/icons/trash.png'></img></div></td>";
	var folder = "<td><img src='" + window.location.origin + "/static/icons/folder.png'></img></td>";
	var file = "<td><img src='" + window.location.origin + "/static/icons/file.png'></img></td>";
	var inserted = false;
	
	function insertFile(name, newChild) {
		$("#user_resources tbody").children(".file").each(function () {
			if ($(this).children("td").eq(1).text() > name) {
				$(this).before(newChild);
				inserted = true;
				return false;
			}
		});
			
		if (!inserted) {
			$("#user_resources tbody").append(newChild);
		}
	}
	
	$.displayResource = function(name, contentType, contentLength, modifiedDate) {
		inserted = false;
		modifiedDate = (modifiedDate < 60000 ? "Less than 1 minute ago" : (modifiedDate < 3600000 ? Math.ceil(modifiedDate / 60000) + " minutes ago" : (modifiedDate < 86400000 ? Math.ceil(modifiedDate / 3600000) + " hours ago" : (modifiedDate < 604800000 ? Math.ceil(modifiedDate / 86400000) + " days ago" : "undefined"))));
		
		if (contentType == null) {
			$("#user_resources tbody").children(".folder").each(function () {
				if ($(this).children("td").eq(1).text() > name) {
					$(this).before("<tr class='folder'>" + folder + "<td>" + name + "</td><td>" + modifiedDate + "</td><td>" + "-" + "</td>" + actions + "</tr>");
					inserted = true;
					return false;
				}
			});
			
			if (!inserted) {
				if (!$("#user_resources tbody .folder").length) {
					$("#user_resources tbody").prepend("<tr class='folder'>" + folder + "<td>" + name + "</td><td>" + modifiedDate + "</td><td>" + "-" + "</td>" + actions + "</tr>");
				} else {
					$("#user_resources tbody tr:nth-child(" + $('#user_resources tbody .folder').length + ")").after("<tr class='folder'>" + folder + "<td>" + name + "</td><td>" + modifiedDate + "</td><td>" + "-" + "</td>" + actions + "</tr>");
				}
			}
		} else {
			contentLength = (contentLength < 1024 ? contentLength + " byte" : (contentLength < 1048576 ? Math.ceil(contentLength / 1024) + " kB" : (contentLength < 1073741824 ? Math.ceil(contentLength / 1048576) + " MB" : "Undefined")));
			if (contentType == "text/plain") {
				insertFile(name, "<tr class='file'>" + file + "<td>" + name + "</td><td>" + modifiedDate + "</td><td>" + contentLength + "</td>" + actions + "</tr>");
			} else {
				insertFile(name, "<tr class='file'>" + file.replace("file", contentType.split("/").pop()) + "<td>" + name + "</td><td>" + modifiedDate + "</td><td>" + contentLength + "</td>" + actions + "</tr>");
			}
		}
	}
	
	$.displayResourceCopy = function(name, element) {
		inserted = false;
		
		if ($(element).attr("class") == "folder") {
			$("#user_resources tbody").children(".folder").each(function () {
				if ($(this).children("td").eq(1).text() > name) {
					$(this).before("<tr class='folder'>" + folder + "<td>" + name + "</td><td>" + "Less than 1 minute ago" + "</td><td>" + "-" + "</td>" + actions + "</tr>");
					inserted = true;
					return false;
				}
			});
			
			if (!inserted) {
				if (!$("#user_resources tbody .folder").length) {
					$("#user_resources tbody").prepend("<tr class='folder'>" + folder + "<td>" + name + "</td><td>" + "Less than 1 minute ago" + "</td><td>" + "-" + "</td>" + actions + "</tr>");
				} else {
					$("#user_resources tbody tr:nth-child(" + $('#user_resources tbody .folder').length + ")").after("<tr class='folder'>" + folder + "<td>" + name + "</td><td>" + "Less than 1 minute ago" + "</td><td>" + "-" + "</td>" + actions + "</tr>");
				}
			}
		} else {
			insertFile(name, "<tr class='file'><td>" + $(element).children("td").eq(0).html() + "</td><td>" + name + "</td><td>" + "Less than 1 minute ago" + "</td><td>" + $(element).children("td").eq(3).text() + "</td>" + actions + "</tr>");
		}
	}
});