<?php
	$text = "";
	
	if (isset($_SERVER['REQUEST_METHOD']) && $_SERVER['REQUEST_METHOD'] === 'POST') {
		$text = $_POST["text"];
		echo json_encode ("success");
	}
?>