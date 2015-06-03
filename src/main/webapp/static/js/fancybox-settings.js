/*jslint browser: true*/
/*jslint white: true */
/*global $, jQuery*/

$(window).load(function () {
	"use strict";

	$("#fancybox-image").fancybox({
		type        : 'image',
		maxHeight	: '90%',
		openEffect  : 'none',
		closeEffect : 'none'
	});
});