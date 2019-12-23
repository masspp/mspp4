#define JarFile     "mspp-4.0.0_alpha.jar"

[Setup]
AppName=Mass++ 4
AppVersion=4.0.0 alpha
DefaultDirName={commonpf}\mspp4
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
Name: "{group}\Mass++ 4"; Filename: "{app}\mspp4.bat"; WorkingDir: "{app}"