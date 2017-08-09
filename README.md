# OpenMemories: ADB
Turn on ADB on your camera.

## Installation
Install it here: [sony-pmca.appspot.com](https://sony-pmca.appspot.com/apps) (Only Internet Explorer and Safari are supported)

## Compatibility
This app should be compatible with all Sony cameras supporting PlayMemories Camera Apps (PMCA). See [here](https://github.com/ma1co/OpenMemories-Framework/blob/master/docs/Cameras.md) for a full list.

## A word of caution
This is not an official Sony application. All information has been found through reverse engineering. Even though everything worked fine for our developers, it could cause harm to your hardware. If you break your camera, you get to keep both pieces. **We won't take any responsibility.**

## Usage
After installation, you should find the app in the "Application List" on your camera.

* Use the menu button to exit the app
* The app writes a log to the SD card (*ADBLOG.TXT*)

### Developer
#### Wifi
Check the "Enable Wifi" checkbox to permanently connect the camera to your wifi access point. Make sure to disable wifi again before you turn off the camera, otherwise it might crash.

Don't forget to increase the "Power save start time" in the menu, or the camera may turn off automatically after a few minutes.

Once wifi is enabled, you can run and connect to the following daemons:

#### Telnet
To get a root shell on your camera, check the "Enable Telnet" checkbox. This starts a telnet daemon listening on port 23. Connect to it via wifi.

A few special commands you can run:

##### To run an Android shell
    android_console.sh

##### To dump the firmware to the sdcard
On Android 2:

    dd if=/dev/nflasha of=/android/mnt/sdcard/DUMP.DAT bs=1M

On Android 4:

    dd if=/dev/nflasha of=/android/storage/sdcard0/DUMP.DAT bs=1M

This operation will take some time (it copies about 500MB). Use [fwtool.py](https://github.com/ma1co/fwtool.py) to unpack the dump file.

#### ADB
To install and debug Android apps from your computer, check the "Enable ADB" checkbox. This starts an ADB daemon listening on port 5555. Connect to it via wifi:

    adb connect <your camera's ip>

## FAQ
### How does it all work?
[nex-hack](http://www.personal-view.com/faqs/sony-hack/hack-development) managed to decrypt firmware updates (see [fwtool.py](https://github.com/ma1co/fwtool.py) for a more recent unpacker). The [PMCA-RE](https://github.com/ma1co/Sony-PMCA-RE) project reverse engineered how apps are installed. This allows us to run custom code.

### I don't have Internet Explorer
Well, lucky you :) You can try the experimental [installer](https://github.com/ma1co/Sony-PMCA-RE#local-installer) (releases for Windows and OS X are provided).

### How can I remove the app?
You can uninstall it normally with the "Application Management" app. The modified settings are preserved even if you remove the app.

### I have an old camera that doesn't support apps
For now, you're out of luck. We can only run code on our cameras through the Android subsystem. For older cameras, you'd have to patch a firmware image with your settings. There is also a USB service mode (senser / adjust mode) which sadly we haven't managed to trigger (yet).
