# Automatic_FFMPEG
It processes all .mp4 files with FFMPEG recursively from where the program was executed and stores them in the directory "renderizados" which in turn is created in the same path where the program was executed.

**Warning: The program overwrite the name of the video by a name that is assigned by its last modified date in descending order**

# Installation
[![GitHub All Releases](https://img.shields.io/github/downloads/AntonioRG00/Automatic_FFMPEG/total?color=blue)](https://github.com/AntonioRG00/Automatic_FFMPEG/releases)   **<---- Releases!**

First you have to download and install FFMPEG, i have an automatic build but if you don't trust just watch a tutorial on youtube (The version tested of FFMPEG for this program is the 4.2)
[FFMPEG Automatic Installer](http://www.mediafire.com/file/lx5dgdmxn0gcb9c/ffmpeg-4.2-setup.exe/file).

After installing FFMPEG, download one of my releases and move the .jar to your videos folder, double click it and that is all! it will start working.

# Default Config

> `ffmpeg  -i CONVERT.mp4 -vf scale=3840:2160:flags=neighbor -c:v h264_nvenc -profile high -preset slow -rc vbr_2pass -qmin 17 -qmax 22 -2pass 1 -c:a:0 copy -b:a 384k CONVERTED.mp4`
