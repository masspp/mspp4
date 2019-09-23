#!/bin/sh
#
# BSD 3-Clause License
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
# * Redistributions of source code must retain the above copyright notice, this
#   list of conditions and the following disclaimer.
#
# * Redistributions in binary form must reproduce the above copyright notice,
#   this list of conditions and the following disclaimer in the documentation
#   and/or other materials provided with the distribution.
#
# * Neither the name of the copyright holder nor the names of its
#   contributors may be used to endorse or promote products derived from
#   this software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
# DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
# FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
# DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
# SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
# CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
# OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
# OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#
#
# @author Mass++ Users Group (https://www.mspp.ninja/)
# @author Masayo Kimura
# @since 2019
#
# Copyright (c) Mon Sep 23 19:52:10 JST 2019 Masayo Kimura
# All rights reserved.
#

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



