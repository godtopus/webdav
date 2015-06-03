<?xml version="1.0" encoding="UTF-8"?>
<!--
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
-->
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta charset="UTF-8"/>
		<title>SyncBerry</title>
		<link rel="stylesheet" href="../static/css/style-home-page.css"/>
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
		<script src="../static/js/authentication.js"></script>
	</head>
	<body>
		<div id="header"><img src="../static/icons/sb-logo-login.png"/></div>
		<div class="container">
			<ul id="nav"><li class="marked">Sign In</li><li>Sign Up</li></ul>
			<div id="sign_in">
				<form action="" method="post">
					<fieldset>
						<p><span><img src="../static/icons/user.png"/></span><input type="text" class="username" pattern=".{4,}" title="Must be at least 4 characters" placeholder="Username" maxlength="40" required="true"/></p>
						<p><span><img src="../static/icons/lock.png"/></span><input type="password" class="password" pattern=".{4,}" title="Must be at least 4 characters" placeholder="Password" maxlength="40" required="true"/></p>
						<p><input type="submit" id="submit_sign_in" value="Sign In"/></p>
					</fieldset>
				</form>
			</div>
			<div id="sign_up">
				<form action="" method="post">
					<fieldset>
						<p><span><img src="../static/icons/user.png"/></span><input type="text" class="username" pattern=".{4,}" title="Must be at least 4 characters" placeholder="Username" maxlength="40" required="true"/></p>
						<p><span><img src="../static/icons/lock.png"/></span><input type="password" class="password" id="password1" pattern=".{4,}" title="Must be at least 4 characters" placeholder="Password" maxlength="40" required="true"/></p>
						<p><span><img src="../static/icons/lock.png"/></span><input type="password" class="password" id="password2" pattern=".{4,}" title="Must be at least 4 characters" placeholder="Password" maxlength="40" required="true"/></p>
						<p><input type="submit" id="submit_sign_up" value="Sign Up"/></p>
					</fieldset>
				</form>
			</div>
		</div>
	</body>
</html>