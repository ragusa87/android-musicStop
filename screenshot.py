# Imports the monkeyrunner modules used by this program
from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice

# Connects to the current device, returning a MonkeyDevice object
device = MonkeyRunner.waitForConnection()

#device.installPackage('google-play/MusicStop-2.0-release.apk')
#package = 'com.blackcrowsteam.musicstop'
#activity = 'com.blackcrowsteam.musicstop.MainActivity'
#runComponent = package + '/' + activity
#device.startActivity(component=runComponent)

MonkeyRunner.sleep(2)

result = device.takeSnapshot()
result.writeToFile('google-play/main.png','png')
