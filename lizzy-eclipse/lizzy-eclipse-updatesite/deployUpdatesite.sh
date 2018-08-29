cd lizzy-eclipse/lizzy-eclipse-updatesite/target/
# unzip project in target folder:
unzip 'lizzy-eclipse-updatesite-*' -d latest/
unzip latest/artifacts.jar -d latest/
unzip latest/content.jar -d latest/
rm latest/artifacts.jar
rm latest/content.jar
# login to intension server host:
ncftp -u $1 -p $2 wp12720414.server-he.de <<END_SCRIPT
# delete old jars:
delete latest/features/*
delete latest/plugins/*
# upload folder to web:
mput -r latest/
quit
END_SCRIPT
echo done