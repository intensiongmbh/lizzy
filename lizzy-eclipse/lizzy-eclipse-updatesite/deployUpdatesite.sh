cd lizzy-eclipse/lizzy-eclipse-updatesite/target/
# unzip project in target folder:
unzip 'lizzy-eclipse-updatesite-*' -d latest/
unzip latest/artifacts.jar -d latest/
unzip latest/content.jar -d latest/
rm latest/artifacts.jar
rm latest/content.jar
# get previous version:
# login to intension server host:
ncftp -u $1 -p $2 wp12720414.server-he.de <<END_SCRIPT
get latest/release-number.txt
quit
END_SCRIPT
mv release-number.txt latest/
prev=$(cat latest/release-number.txt)
echo "Previous release number: $prev"
date=$(date +%Y%m%d.%H%M%S)
echo "New release number: $date"
# write new release number to file:
echo $date > latest/release-number.txt
if [ $3 = true ]
then
	echo "Deploying bugfix"
	# upload to intension server (overwrite):
	ncftp -u $1 -p $2 wp12720414.server-he.de <<END_SCRIPT
	delete latest/features/*
	delete latest/plugins/*
	mput -r latest/
	quit
END_SCRIPT
else
	echo "Deploying release"
	# upload to intension server (additive):
	ncftp -u $1 -p $2 wp12720414.server-he.de <<END_SCRIPT
	mv latest/ release-$prev/
	mput -r latest/
	quit
END_SCRIPT
fi
echo done