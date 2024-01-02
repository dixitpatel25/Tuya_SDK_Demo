
#  **Tuya SDK Demo App**
 
# This is smaple demo app of tuya SDK

## **Features**

* MVVM Setup
* HILT
* Coroutines
* Navigation
* Interceptors for Authenticated Flows.
* Handling validations and loading state.
* Complete Signup/Login Flow
* Complete add home and show home Flow
* integrated Tuya's QR code scan and  smart camera paring
* also integrated Tuya's Ipc sdk view camera screen 
* in there i was integrated mute/unmute,change videoClarity,speak functionality,record and snapshot functionality

## **Setup**

## **Added required gradle**
* dagger:hilt --> Require this library for dependency injection.
* viewmodel(MVVM) --> Require this library for setup mvvm architecture
* livedata(MVVM) --> Require this library for displaying the latest data.
* coroutines --> Require this library for manage threads
* navigation --> Require this library for manage screen change
* SpinKit --> Require this library for show progress bar
* thingsmart --> Require this library for login,sign up,add home,device config
* thingsmart-ipcsdk --> Require this library for preview camera screen
* fastjson --> Require this library for thingsmart SDK 
* fastjson --> Require this library for thingsmart SDK 
* okhttp3 --> Require this library for thingsmart SDK 
* soloader --> Require this library for thingsmart SDK 
* sdp --> Require this library for Ui management Like if you run this apk tablet you can same screen preview like phone
* ssp --> Require this library for Ui-Text size mange same of above
* CountrySelectionDialog --> Require this library for country pick
* gson --> Require this library for convert json to string
* yipianfengye -->  Require this library for generate qrcode

# **For run this project in your android studio**
* first of open android studio in your pc then click open box and select directiories where you save this project and open that
* once you successfully buid this project after that you have generate sha-256 
* for generate sha-256 you have to click right side menu item **gradle** then click Execute Gradle Task icon after 
* that type there in serach signingreport and enter after that in logs you can see your sha-256 copy
* that and paste it to dashbord of tuya sdk under Demo app Get key section in certificate section
* after run this app in your mobile or emulator and you can check functionality

# **Test APP**

# **For Register**
1) First of you have register in app.For register you have to provide email first then click send verification otp 
after that you can receive an otp in your email.once you received otp then enter that otp to our app under verify code 
and provide password also.after that click on sign up button once register successfully you can redirect to home page.

# **For Login**
1) For login you have to provide email and password.once enter email and password then click login button.
once you get login success msg then you redirect home screen.

# **For Home Screen**
1) after register success you will redirect home page.first time you arrived on home page there you can show add home button
2) when you clicked add home button you can redirect to add home screen.in add home screen you have to provide 
home name there after click on add home button.once successfully home create you redirect to home page there 
you can see your home.
3) after that you can see add device button in home screen once you click there 
you can redirect to add device screen.there you have to provide wifi name and password.then click save button.
you can see an qrcode on screen you have to scan this qr code using your camera once qr code successfully scan.
you show toast device config successfully then you redirect home screen and show there your device.
4) you can see in home screen list of paring devices and also you can see there device status is online or offline.
5) once you click on any online device you can redirect to camera view screen you can see there your camera live screen.
6) i was added there some functionality like mute,unmute,speak,take snapshot,video recoding and clarity change

// Challenge face 
--> as of my experience i didn't face any challenge in this coding

// Download apk using below link test this app
 --> https://we.tl/t-p35g5B7EBb 

