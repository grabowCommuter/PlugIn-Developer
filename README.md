
##How to create a PlugIn for ACom?


To create a PlugIn, you have to perform three steps:

 **1. Develop the PlugIn:** You have to define the behavior of the PlugIn, that means define which script should be executed, which url
    should be loaded, etc. Our PlugIn-Developer-App will help you. (The
    app can be downloaded here).
    
 **2. Test the PlugIn:** Describe the behavior the the PlugIn in a JSON-Object and test the JSON-Object in our PlugIn-Tester (github:
    …).
    
 **3. Publish your PlugIn** (if you want to) on github.

---------

###Part 1: Develop the PlugIn

 PlugIn is nothing else than a „webview“. If you are not familiar with this concept, you should look at the [android documentation](http://developer.android.com/guide/webapps/webview.html).

A „webview“ can be considered as an internal web-browser and for loading a website you can simply use the following method: `webview.loadUrl(...)`.

If you want to execute scripts, you should use the `loadDataWithBaseUrl(...)` - [method](http://developer.android.com/reference/android/webkit/WebView.html#loadDataWithBaseURL%28java.lang.String,%20java.lang.String,%20java.lang.String,%20java.lang.String,%20java.lang.String).

Sometimes you want to execute scripts after the webpage has been loaded. Then you have to call the `loadDataWithBaseUrl(...)`-method in the `onPageFinished()`-Method.

If you want to use the latitude, longitude and zoom parameters form the ACom master map, you can reference these parameters with the strings `#lat#` , `#lng#` and `#zoom#` in your script or url. 

These strings are afterwards automatically replaced by the concrete values of the master map. E.g. if the master map shows a map at (lat=49.123, lng=8.234) with zoomlevel zoom=11 and you specify the url by the string `https://www.google.de/maps/@#lat#,#lng#,#zoom#z`, the string will become to `https://www.google.de/maps/@49.123,8.234,11z`. (To simulate this behavior in our PlugIn-Developer-App, call our replace()-method, e.g. url = replace(url); ).

If you load a webpage - e.g. with `loadUrl()` - and the webpage contains links to other pages, you have to specify whether your webview should handle  the links or if the browser should be launched. This can be done with the `shouldOverrideUrlLoading()`-method. E.g. if you want the web view to handle all the links, you can specify this with: 
> if url.startsWith(„http“) { … return true } else  { … return false }

----------
As the web view will run as part of our main app ACom, you have the possibility to specify additional behavior:

 - enable / disable ZoomControls for the webView. (Maybe, you don’t want the zoomControl widget to appear.)
 - enable / disable location ability. (Maybe, you want to disable GPS location.)
 - enable / disable backButton. (Maybe, you have several webpages and you want to navigate inside the web view through the backButton, so enable backButton.)
 - enable / disable screen-lock if you rotate the device. (Maybe, you want to avoid config-changes by screen rotation, so enable screen-lock.)
 - enable / disable reload-feature (Maybe, you clean-and-reload doesn’t work properly, so you might want to disable this option menu item.) 

Set these behavior with the method 

    setOptions(WebView wv, boolean zoomCtrl, boolean geoLocEnable, boolean backButton, boolean lockScreenRot, boolean reload)

---------

To elaborate these functionalities, we provide you with the PlugIn-Developer-App, which is just a simple app consisting of one activity, with which you can call the methods mentioned above. 
Just download or clone the app from this repro and jump into the method `loadWebView()`. Inside this method, you may call the following methods:
 - `setOptions()` to specify additional behavior. 
 - `replace()` to set the lat, log and zoom values in your url or script.
 - `loadUrl()` to start the web view with the url
 - `loadDataWithBaseUrl()` to execute a javascript
 - `shouldOverrideUrlLoading()` to specify the link behavior
 - `loadUrl()` in the `onPageFinished()`-method

-----
If your done and the app behaves as you want you should note the arguments of the invoked methods, e.g.:
>	for setOptions:  `zoomCtrl : false, geoLocEnable : true, backButton : false, lockScreenRot : false, reload : true`

>	for loadUrl: `url : 
https://www.google.de/maps/@#lat#,#lng#,#zoom#z/data=!3m1!1e3?hl=en`

----
Now, you are ready for **[Part 2: Test the PlugIn](https://github.com/grabowCommuter/PlugIn-Tester)**. 

-----

----

> Written with [StackEdit](https://stackedit.io/).