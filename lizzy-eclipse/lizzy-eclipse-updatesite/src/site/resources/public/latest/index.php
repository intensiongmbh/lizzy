<!DOCTYPE html>
<html>
<head lang="de">
<title>Lizzy update site</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.0/css/bootstrap.min.css">
</head>
<body class="container">
	<nav class="navbar navbar-expand-sm">
		<ul class="navbar-nav">
			<li class="nav-item"><a class="nav-link" href="..">OVERVIEW</a></li>
		</ul>
	</nav>
	<h1>You're close!</h1>
	<div>
		This URL is an Eclipse <b>software repository</b>; you must use it in
		Eclipse (<a
			href="http://help.eclipse.org/photon/index.jsp?topic=/org.eclipse.platform.doc.user/tasks/tasks-127.htm">see
			how</a>).
<?php
if ($fh = fopen('release-number.txt', 'r')) {
    $datestring = trim(fgets($fh));
    $date = DateTime::createFromFormat('Ymdhi', $datestring)->format('jS F Y');
    echo "<br/>This version was released on " . $date . ".";
    fclose($fh);
}
?>
	</div>
	<img class="img-fluid" src="../eclipse-software-install.png"
		alt="image not found">
</body>
</html>
