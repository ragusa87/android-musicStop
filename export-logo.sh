#!/bin/bash
convert -verbose +antialias -fill white  -resize 300x300 -flatten -density 1200 app/src/main/assets/ic_launcher.svg  ./google-play/logo.png 
