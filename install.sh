#!/bin/sh

LSDIR="$HOME/.loliSnatcher/"
mkdir "$LSDIR"
### Check for config Directory ###
if [ ! -d "$LSDIR/config" ]; then
	mkdir "$LSDIR/config"
fi
### Get Icon ###
if [ ! -f "$LSDIR/icon.png" ]; then
	wget https://github.com/NO-ob/LoliSnatcher/releases/download/v1.0/icon.png -P "$LSDIR"
fi
### Get Binary ###
wget https://github.com/NO-ob/LoliSnatcher/releases/download/1.4/lolisnatcher -P "$LSDIR"
chmod u+x "$LSDIR/lolisnatcher"
### Get booru configs files ###
wget https://github.com/NO-ob/LoliSnatcher/releases/download/1.2/booru.zip
unzip booru.zip -d "$LSDIR"/config
rm booru.zip
### Get Theme ###
wget https://github.com/NO-ob/LoliSnatcher/releases/download/1.4/theme.zip
unzip theme.zip -d "$LSDIR/config"
rm theme.zip
### Create desktop file ###
cat << EOF > ~/.local/share/applications/loliSnatcher.desktop
[Desktop Entry]
Name=Loli Snatcher
StartupWMClass=loliSnatcher.Main
Exec="$LSDIR/lolisnatcher"
Icon=$LSDIR/icon.png
Type=Application
Categories=Graphics
EOF