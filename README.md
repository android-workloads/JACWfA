# Java* ACW for Android*

Java* Aplication Component Workload for Android* - the client Java workload powered by MTTest framework

#### It targets multiple kernels widely used on mobile systems like Android:
* ++ ai          (artificial intelligence scenarios by libGdx AI library)
* ++ compression (compression scenarios)
* ++ dmath       (decimal math algorithms)
* ++ fmath       (float math algorithms)
* ++ html        (html parser by jsoup library)
* ++ image       (image processing by BoofCV library)
* ++ jni         (jni stress scenarios)
* ++ lphysics    (physics engine scenarios by jBullet)
* ++ pdf         (pdf tests)
* ++ sort        (various sort algorithms)
* ++ xphysics    (physics engine scenarios by jBox2D)
* ++ suite ALL [use -s option] masterset:all:     [ai compression dmath fmath html image jni lphysics pdf sort xphysics]

Java* ACW for Android* is based on xml configurations, to see micros (tests) in every kernel please go to:

Project_dir/cfg/

### How to run Java* ACW for Android*: 

#### User mode 

##### GUI

- download and run JavaACW-<Version>.apk, use GUI to navigate

    install: adb install -r ./JavaACW-<Release>.apk (uninstall old version first if error happened: adb uninstall com.intel.mttest.android )

    run:

        * use GUI to startup application
        * or run from command line: adb shell "am start -S -n com.intel.mttest.android/com.intel.mttest.android.StarterActivity -e autostart true -e -s masterset:all". See Developer mode for details

##### cmd

    * Go to bin folder

    * extract native libraries and input files using 'jar -xf JavaACW-<Release>.apk lib/<platform>' and 'jar -xf JavaACW-<Release>.apk assets'
        Where <platform> is one of these variants: Lin-x86, Lin-x86_64, Win-x86, Win-x86_64

    * Run tests using: java -Djava.library.path=lib/<platform> -jar JavaACW-<Release>.apk -s masterset:all


#### Developer mode 

* Goto the project folder

* Setup Java* ACW for Android environment

 * Configure mttest.properties file to be able to build and run, see hints in the property file

 * Perform the next command to setup proxy (if necessary), needed to download external dependencies by ant:

    - export ANT_OPTS="-Dhttp.proxyHost=<your.host.addr> -Dhttp.proxyPort=<your.port>"

 * Use Android SDK Manager to download and install Android SDK with 'Android Support Library' and 'Android Support Repository'

* Setup ANDROID_SERIAL to your device (e.g. expoert ANDROID_SERIAL=<your device serial>)

* Build

    ./mttest.sh build -m [Java|Host|Device|Product] (to make build run faster you may want to give Java more memory, eg "export _JAVA_OPTIONS: -Xmx2g" before the build)
    NOTE: How build product APK with your own prebuilt libraries (for example, DLLs)
        1. Before use './mttest.sh build' go to ./src/main/jni
        2. Build the contents into *.dll yorself using apropriate compiler
        3. Create a folder if not exists with name 'prebuilt' and put dlls there
        4. After all you can use './mttest.sh build -m Product' and they will be automatically picked up

* Use mttest.sh - this is major script to use Java* ACW for Android*.

   Run './mttest.sh' for mttest.sh manual. MTTest supports several modes:

    - Java (run on JVM)

    - Device (run on Android device)

        a. command line mode (by dalvikvm)

        b. apk mode (as a standard Android application run by application manager)

    - Host (run on Linux by ART host)

* Run from command line

    Parameters:

    All modes support unified parameters:

    -s [test1,group2,test3] 

        Allows to pick testing set. Example '-s masterset:all:html,masterset:all:ai:com.intel.JACW.ai.scArrive'.

        {masterset:all} by default. It links on ./cfg/testsets/ALL/all.xml

    -c [config file name]

        Defines duration of testing cycle.  Example [-c medium.xml].

        {medium.xml} by default. It links on ./cfg/configs/medium.xml

    -t [integer]; 

        Defines number of threads. Example [-t 2].

        {-t 1} by default.

  * Run modes:

    - mode Java

        Command perform 4 runs on [32 / 64 bit] JVM with [-client / -server] options.

        ./mttest.sh run -m Java (run the whole workload)

        How to specify test. 

            - pass '-s' key, e.g. ./mttest.sh run -m Java -s masterset:all:html

            - it's possible to use multiply test definition, -s masterset:all:html,masterset:all:ai:com.intel.JACW.ai.scArrive

    - mode Host

        ./mtthet.sh run -m Host

    - mode Device 

        Install apk and run on the device

        ./mttest.sh run -m Device

            Direct VM invocation without application interface.

        ./mttest.sh run-android -m Device

            Start application with 'autostart' argument.

* Look for resutls in ./results/date_stamp/

#### Shrinking to single dex

There was an option added to mttest.properties 
```bash
use_proguard=true
```
It is used to generate single dex apk to avoid ART compiler overhead. 


