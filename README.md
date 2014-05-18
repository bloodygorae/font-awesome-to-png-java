font-awesome-to-png-java
========================

java program to make png icon from ttf font files.

This program is JAVA version to extract the awesome
[Font Awesome] (http://fortawesome.github.com/Font-Awesome/) icons as PNG icons with each character.

### Usage

    java com/ingecep/font/FontConverter [--help] [--color=COLOR]
    		[--font=FONT] [--list=LIST] [--iconsize=SIZE] [--fontsize=SIZE]
		
	necessary artuments
	  --font=FONT     Font file to use (default: fontawesome-webfont.ttf)
	  --list=LIST     Icon set image name and character code list file (iconset.txt)
	  --output=FOLDER	Icon save folder to store png icon images
	
	optional arguments
    --color=COLOR 	HTML RGB Color code starting from #
    --iconsize=SIZE	PNG icon size

To use the program, you need the Font Awesome TTF file, which is available in
[Font Awesome Github repository] (https://github.com/FortAwesome/Font-Awesome).
