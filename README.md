
# react-native-ringtones-library

## Getting started

`$ npm install https://github.com/zivost/react-native-ringtones-library.git --save`

### Mostly automatic installation

`$ react-native link react-native-ringtones-library`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-ringtones-library` and add `RNRingtonesLibrary.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNRingtonesLibrary.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNRingtonesLibraryPackage;` to the imports at the top of the file
  - Add `new RNRingtonesLibraryPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-ringtones-library'
  	project(':react-native-ringtones-library').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-ringtones-library/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-ringtones-library')
  	```

#### Windows
[Read it! :D](https://github.com/ReactWindows/react-native)

1. In Visual Studio add the `RNRingtonesLibrary.sln` in `node_modules/react-native-ringtones-library/windows/RNRingtonesLibrary.sln` folder to their solution, reference from their app.
2. Open up your `MainPage.cs` app
  - Add `using Ringtones.Library.RNRingtonesLibrary;` to the usings at the top of the file
  - Add `new RNRingtonesLibraryPackage()` to the `List<IReactPackage>` returned by the `Packages` method


## Usage
```javascript
import RNRingtonesLibrary from 'react-native-ringtones-library';

# react-native-ringtones-library

## iOS Foreword

Setting ringtones programatically is not available in iOS unfortunately. iTunes has it's own ringtone store available and there is no public API for setting ringtones. This library is for Android only.

## Getting started

With npm: 

`$ npm install react-native-ringtones-library --save`

Or with yarn: 

`$ yarn add react-native-ringtones-library`

### Mostly automatic installation

`$ react-native link react-native-ringtones-library`

### Manual installation

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNRingtonesLibraryPackage;` to the imports at the top of the file
  - Add `new RNRingtonesLibraryPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-ringtones-library'
  	project(':react-native-ringtones-library').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-ringtones-library/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-ringtones-library')
  	```


## Usage
```javascript
import RingtoneLibrary from 'react-native-ringtones-library';
```

### `RingtoneLibrary.getRingtones()`

Returns a list of `Ringtones`

### `RingtoneLibrary.getRingtones(RINGTONE_TYPE)`

Returns a list of `Ringtones` of the given type:

- `RingtoneLibrary.TYPE_ALL`
- `RingtoneLibrary.TYPE_RINGTONE`
- `RingtoneLibrary.TYPE_NOTIFICATION`
- `RingtoneLibrary.TYPE_ALARM`

### `RingtoneLibrary.setRingtone(settings)`

Sets the system ringtone to the given ringtone. Settings options given below:

| Param        | Type          | Description                                                                       |
|--------------|---------------|-----------------------------------------------------------------------------------|
| uri          | String        | The full file path to the ringtone on the file system.                            |
| title        | String        | The title of the ringtone. Will appear in the picker with this title.             |
| artist       | String        | The artist of the ringtone.                                                       |
| size         | Integer       | The size of the ringtone file.                                                    |
| mimeType     | String        | The mime type of the ringtone, for example: `audio/mp3`                           |
| duration     | Integer       | The duration of the ringtone in seconds.                                          |
| ringtoneType | RINGTONE_TYPE | The ringtone type: `TYPE_ALL`, `TYPE_RINGTONE`, `TYPE_NOTIFICATION`, `TYPE_ALARM` |


  