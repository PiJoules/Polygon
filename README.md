TestApp
=======

This repository contains the source code of the app we are creating for our EPED 2 project, called Polygon. All java source code is in src/com/test. The xml used for GUI formatting is in res/layout.

Instructions
------------

To play this game, simply open the app and tap the play button. You play as the black circle that spawns in the center. You can move it by tilting your phone. Green squares will spawn in each corner. These are your enemies. You can eat the smaller ones to get bigger. If a bigger square touches you, you lose. The bigger you get, the higher your score!

You can also change the settings for the filter applied to the accelerometer readings. From the main menu, tap settings. There you can switch between no filter, an exponential moving average filter, and a simple moving average filter. You can change the alpha value used for the EMA filter and the number of periods to use for the SMA filter.

Your best scores will be saved on your phone with the names you enter at the end of the game. To clear these scores, tap Clear Local Scores button.

Code
----

* Test2.java

  * This file contains the core code controlling the game. It includes the loop that draws the playing screen and moves the objects on the screen according to the users input.

* Player.java

  * This file defines the Player object that represents the oval that the user plays as.

* Polygon.java

  * This file defines the Polygon object that represents the squares that are generated. The user must avoid larger squares and eats smaller squares to grow bigger.

* Accelerometer.java

  * This file defines the Acclerometer object. It applies filtering according to user defined settings to the accelerometer readings.

* FileManager.java

  * This file contains functions to create, save, and read contents from files saved on the phone by this app.

* ScoreManager.java

  * This file is used to keep track of the highest scores acheived in the game. It saves the highest scores to the phone.

* MainMenu.java

  * This file outlines the main screen (Represented by Android Activity objects). It creates several buttons that allow the user to play the game, change settings, or clear high scores.

* Settings.java

  * This file allows users to change filtering settings and clear high scores.

* FilterControl.java

  * This file outlines the interface that allows the user to change the accelerometer settings. It also creates plots using AndroidPlot (see more under dependencies)

* TimeSeriesActivity.java

  * This file controls the plots of the filtered and unfiltered acclerometer readings.

* res/

  * This folder contains all xml formatting for the Activity screens as well as images used in the app. res/layout defines the layout of gui elements on each screen. 

Dependencies
------------

* AndroidSDK - http://developer.android.com/sdk/index.html

  * The Android SDK contains all the core libraries needed to begin programming an Android app in java

* AndroidPlot - http://androidplot.com/

  * AndroidPlot is used for visualization of accelerometer data. It includes utilities to easily create graphs and rolling plots. The compiled libraries are contained in libs/androidplot-core-0.6.1.jar
