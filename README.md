# HomeOffice - Home Assistant

Application for notification of household members about the status of working from home. It has an automatic process that, based on working hours and the registry, sets the appropriate status to Home Assistant depending on whether you are available, working, having a meeting with or without a camera

![](https://github.com/Patresss/HomeOffice---Home-Assistant/blob/master/readme-resources/application.png)

## Download - 1.0.0 version
* Installer exe (windows) - [download exe](https://github.com/Patresss/HomeOffice---Home-Assistant/releases/download/1.0.0/HomeOffice-1.0.0.exe)
* Executable jar (bin/) - [download zip](https://github.com/Patresss/HomeOffice---Home-Assistant/releases/download/1.0.0/HomeOffice-1.0.0.zip)

## Installation

1. Install the application (but please do not select the system path like `C:\Program Files` otherwise you will have to run the application as administrator)
it will create the `config/settings.yaml` file
2. Set up the `config/settings.yaml` file:
    * `homeAssistant.token` from Long-Lived Access Tokens
    * `homeAssistant.homeAssistantUrl`  for example: http://my-home-assistant-url:8123
3. Run the application again
4. Add the input_select to Home Assistant
```yaml
input_select:
   work_status:
   name: Work status
   icon: mdi:briefcase
   options:
    - Available
    - Working
    - Meeting - microphone
    - Meeting - camera
    - Turn off
```
a) You can rename "work_status" but you have to override `homeAssistant.entityName` in the `config/settings.yaml` file
b) You can options but you have to override `homeAssistant.*OptionName` in the `config/settings.yaml` file
5. Now you can change your work status using buttons
6. I would recommend adding this app to startup 

## Options
* AVAILABLE - change input_select.work_status to Available 
* WORKING - change input_select.work_status to Working
* MEETING (MICROPHONE ONLY) - change input_select.work_status to Meeting - microphone
* MEETING (WITH WEBCAM) - change input_select.work_status to Meeting - camera
* AUTOMATION - change input_select.work_status according to Automatic mode
* TURN OFF - change input_select.work_status to Turn off

## Automatic mode
* AVAILABLE - outside working hours
* WORKING - during working hours
* MEETING (MICROPHONE ONLY) - when a microphone in use is detected
* MEETING (WITH WEBCAM) - when a webcam in use is detected

## Settings
### Home Assistant
```yaml
homeAssistant:
  token: <Long-Lived Access Tokens>
  homeAssistantUrl:  <url like http://127.0.0.1:8123 or http://homeassistant-my-dns:8123>
  entityName: <input_select entity name>
  availableOptionName: <Available option name>
  workingOptionName: <Working option name>
  meetingMicrophoneOptionName: <Meeting - microphone option name>
  meetingCameraOptionName: <Meeting - camera option name>
  turnOffOptionName: <Turn off option name>```
```
### window
```yaml
window:
  pinned: <pinned - true if you want the application to always be displayed on the screen> # Example: true
  enablePreviousPosition: <pinned - true if you want the application to be in the same position after restart> # Example: true
  positionX: <Position X - X position on the screen> # Example: 200
  positionY: <Position Y - Y position on the screen> # Example: 200
```
### workingTime
```yaml
workingTime:
  days: <working days of the week> # Example: - "MONDAY" \n - "TUESDAY"
  start: <start - working hour> # Example: "09:00"
  end: <end - working hour> # Example: "17:00"
```

### Other
```yaml
currentMode: <current mode>
automationFrequencySeconds: <refreshing period when automation mode>
```

## Built With

* [JFoenix](https://github.com/jfoenixadmin/JFoenix)
* [commons-lang](https://github.com/apache/commons-lang)
* [jnativehook](https://github.com/kwhat/jnativehook)
* [log4j](https://logging.apache.org/log4j/2.x/)
* [slf4j](http://www.slf4j.org/)
* [FontAwesomeFx](https://www.jensd.de/wordpress/?tag=fontawesomefx)
* [Commons IO](http://commons.apache.org/proper/commons-io/)
* [Jackson](https://github.com/FasterXML/jackson-module-kotlin)

## License

This project is licensed under the Apache License 2.0 
