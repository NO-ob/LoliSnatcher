#!/bin/sh
### Check we have all programs ###
for I in wget unzip java awk; do
  if ! command -v "$I" 1>/dev/null; then
    echo "Need '$I' command :(" >&2; exit 1
  fi
done
### Check Java version ###
JAVA_VER=$(java -version 2>&1| awk -F '"' '/version/ {print $2}')
if [ "$(expr "$JAVA_VER" \< "11.0")" = 0 ]; then
  echo "Need at least Java 11 :(" >&2; exit 1
fi
LSDIR="$HOME/.loliSnatcher/"
mkdir "$LSDIR"
### Get JavaFX if it is not already in program dir ###
if [ ! -d "/home/$USER/.loliSnatcher/javafx-sdk-11.0.2" ]; then
	wget https://download2.gluonhq.com/openjfx/11.0.2/openjfx-11.0.2_linux-x64_bin-sdk.zip
	unzip openjfx-11.0.2_linux-x64_bin-sdk.zip -d "$LSDIR"
	rm openjfx-11.0.2_linux-x64_bin-sdk.zip
fi
PATH_TO_FX="$LSDIR"javafx-sdk-11.0.2/lib
### Get Program ###
rm "$LSDIR"LoliSnatcher.jar
wget https://github.com/NO-ob/LoliSnatcher/releases/download/v1.1/LoliSnatcher.jar -P "$LSDIR"
### Get Icon ###
if [ ! -f "$LSDIR/icon.png" ]; then
	wget https://github.com/NO-ob/LoliSnatcher/releases/download/v1.0/icon.png -P "$LSDIR"
fi
### Create desktop file ###
cat << EOF > ~/.local/share/applications/loliSnatcher.desktop
[Desktop Entry]
Name=Loli Snatcher
StartupWMClass=com.no.loliSnatcher.Main
Exec=env java -jar --module-path "$PATH_TO_FX" --add-modules javafx.controls,javafx.fxml,javafx.swing "$LSDIR/LoliSnatcher.jar"
Icon=$LSDIR/icon.png
Type=Application
Categories=Grpahics
EOF
