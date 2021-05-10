#define JarFile     "mspp-4.0.0_beta.jar"

[Setup]
AppName=Mass++ 4
AppVersion=4.0.0 beta
DefaultDirName={commonpf64}\mspp4
DefaultGroupName=Mass++ 4
Compression=lzma2
SolidCompression=yes
SourceDir=.
OutputBaseFilename=Mspp4_Setup

[Files]
Source: "jre8\*"; DestDir: "{app}\jre8"; Flags: recursesubdirs
Source: "..\..\target\{#JarFile}"; DestDir: "{app}"
Source: "mspp4.bat"; DestDir: "{app}"


[Icons]
Name: "{group}\Mass++ 4"; Filename: "{app}\jre8\bin\java.exe"; Parameters: "-Xms2048m -Xmx8192m -jar ""{app}\mspp-4.0.0_beta.jar"""; WorkingDir: "{userdocs}"
