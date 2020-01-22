mkdir ~/.loliSnatcher/
### Get JavaFX if it is not already in program dir ###
if [ ! -d /home/$USER/.loliSnatcher/javafx-sdk-11.0.2 ]; then
	wget https://download2.gluonhq.com/openjfx/11.0.2/openjfx-11.0.2_linux-x64_bin-sdk.zip
	unzip openjfx-11.0.2_linux-x64_bin-sdk.zip -d ~/.loliSnatcher/
	rm openjfx-11.0.2_linux-x64_bin-sdk.zip
fi
export PATH_TO_FX=~/.loliSnatcher/javafx-sdk-11.0.2/lib
### Get Program ###
rm ~/.loliSnatcher/LoliSnatcher.jar
wget https://github.com/NO-ob/LoliSnatcher/releases/download/v1.1/LoliSnatcher.jar -P ~/.loliSnatcher/
### Get Icon ###
if [ ! -f "/home/$USER/.loliSnatcher/icon.png" ]; then
	wget https://github.com/NO-ob/LoliSnatcher/releases/download/v1.0/icon.png -P ~/.loliSnatcher/
fi
### Create desktop file ###
rm ~/.local/share/applications/loliSnatcher.desktop
touch ~/.local/share/applications/loliSnatcher.desktop
echo "[Desktop Entry]" >> ~/.local/share/applications/loliSnatcher.desktop
echo "Name=Loli Snatcher" >> ~/.local/share/applications/loliSnatcher.desktop
echo "StartupWMClass=com.no.loliSnatcher.Main" >> ~/.local/share/applications/loliSnatcher.desktop
echo "Exec=env java -jar --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml,javafx.swing /home/$USER/.loliSnatcher/LoliSnatcher.jar" >> ~/.local/share/applications/loliSnatcher.desktop
echo "Icon=/home/$USER/.loliSnatcher/icon.png" >> ~/.local/share/applications/loliSnatcher.desktop
echo "Type=Application" >> ~/.local/share/applications/loliSnatcher.desktop
echo "Categories=Grpahics" >> ~/.local/share/applications/loliSnatcher.desktop
