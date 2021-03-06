<!DOCTYPE html>
<html>
<head lang="de">
<title>Lizzy update repository</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.0/css/bootstrap.min.css">
</head>
<body class="container">
	<nav class="navbar navbar-expand-sm">
		<ul class="navbar-nav">
			<li class="nav-item"><a class="nav-link"
				href="https://www.intension.de">HOME</a></li>
		</ul>
	</nav>
	<h1>Lizzy eclipse plugin</h1>
	<br />
	<p>
		<a href="https://github.com/intensiongmbh/lizzy" data-toggle="tooltip"
			title="Lizzy on GitHub">Lizzy</a> is a free, open-source Eclipse
		plugin to generate test code from BusinessReadableDSL taken from
		ticket systems.<br />Below is a list of all available releases.
	</p>
<?php
$list = "<div class=\"list-group\">";
foreach (scandir('.') as $file) {
    if (is_dir($file) && (fnmatch("latest", $file) || fnmatch("release-*", $file))) {
        $list = $list . "<a class=\"list-group-item list-group-item-action\" href=\"$file\">$file</a>";
    }
}
echo $list . "</div>";
?>
</body>
</html>
