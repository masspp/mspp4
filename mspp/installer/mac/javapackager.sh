app_version=4.0.0
script_dir=`dirname $0`


if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ] && [ -x "$JAVA_HOME/bin/javap" ] && [ -x "$JAVA_HOME/bin/javapackager" ]; then
    _java="$JAVA_HOME/bin/java"
    _javap="$JAVA_HOME/bin/javap"
    _packager="$JAVA_HOME/bin/javapackager"
fi


start_OK=''

if [ "$_javap" ]; then
    version=`$_javap  -version`
    num_version=`echo $version   | sed -e 's/^\(.*\)\.\(.*\)\..*/\1\2/g'`
    echo 'Java version: ' $version
    if [ $num_version -eq 18 ] ; then
	start_OK='OK'
    fi
fi

if [ -n $start_OK ]; then
    echo 'Starting to package mac installer'
    (cd  $script_dir/../../target ; $_packager -deploy -native dmg -outdir mac_installer -outfile Mass++  -srcdir . -srcfiles mspp-4.0.0_alpha.jar -appclass org.springframework.boot.loader.JarLauncher -name Mass++ -title Mass++  -BjvmOptions=-Xmx2g -Bicon=../src/main/resources/images/mspp4.icns -BappVersion=$app_version -Bruntime=$JAVA_HOME ) 

fi



