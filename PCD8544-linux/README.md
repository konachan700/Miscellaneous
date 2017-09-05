### PCD8544-linux
PCD8544 Nokia 5110/3310 simple display driver for linux. Pure C, good for small linux devices. Ported from Arduino driver, but not support images now (it not needed for me :3).

./ - PCD8544 driver. Do "make" for building the driver app. For install copy the binary to /usr/bin/display-daemon and copy systemd service "./display.service" to your systemd directory.

./example/ - demo sample.

For cross-compile edit the Makefile.
