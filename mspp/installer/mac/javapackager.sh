#!/bin/sh
# To build MacOS installer,  execute following commands: 
#   1. export JAVA_HOME=`/usr/libexec/java_home -v 1.8`
#   2. mvn clean install -Dlicense.skip=true
#   3. sh ${mspp_source_top}/mspp/installer/mac/javapackager.sh

app_version=4.0.0
script_dir=`dirname $0`


if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ] && [ -x "$JAVA_HOME/bin/javap" ] && [ -x "$JAVA_HOME/bin/javapackager" ]; then
    _javap="$JAVA_HOME/bin/javap"
    _packager="$JAVA_HOME/bin/javapackager"
else
    echo 'set JAVA_HOME'
fi



jdk_version_OK=''
if [ "$_javap" ]; then
    version=`$_javap  -version`
    num_version=`echo $version   | sed -e 's/^\(.*\)\.\(.*\)\..*/\1\2/g'`
    echo 'Java version: ' $version
    if [ $num_version -eq 18 ] ; then
	jdk_version_OK='OK'
    fi
fi


if [ -n "$jdk_version_OK" ]; then
    echo 'Starting to create installer for MacOSX'
    (cd  $script_dir/../../target ; $_packager -deploy -native dmg -outdir mac_installer -outfile Mass++  -srcdir . -srcfiles mspp-4.0.0_alpha.jar -appclass org.springframework.boot.loader.JarLauncher -name Mass++ -title Mass++  -BjvmOptions=-Xmx2g -Bicon=../src/main/resources/images/mspp4.icns -BappVersion=$app_version -Bruntime=$JAVA_HOME ) 
else 
    echo 'Sorry, use JDK 1.8'
fi



