# !/bin/sh


case "$1" in
 debug)
	 ant debug
	 cp bin/MusicStop-debug.apk google-play/MusicStop-debug.apk 
  ;;
 release)
 	ant release
	cp bin/MusicStop-release.apk google-play/MusicStop-release.apk
  ;;
 *)
  echo "Usage : `basename $0` release/debug" 
esac