# LApi [![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/lni-dev/lapi.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/lni-dev/lapi/context:java)
LApi is a Discord API written in Java

## Installation
In order to install it, you can either build it yourself or use gradle and implement it into your Project:<br><br>
In your `build.gradle` add `mavenCentral()` to the repositories if you have not done so already and add `io.github.lni-dev:lapi:[version]` to the dependencies.
 replace `[version]` with the version you want to install. The newest version is currently `1.0.0`.
```gradle
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.lni-dev:lapi:[version]'
}
```

<br>An example `build.gradle` could look like this:
```gradle
plugins {
    id 'java'
}

group 'com.example'
version 'your.version'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.lni-dev:lapi:1.0.0'
}
```
(Tested on gradle 7.2)

<br>If you want to make an executable .jar file at some point in time, I recommend
the gradle plugins `application` and `shadow`. An example `build.gradle` with these plugins could
look like this:
```gradle
plugins {
    id 'java'
    id 'application'
    id 'com.github.johnrengelman.shadow' version '7.1.2'
}

group 'com.example'
version '1.0'
mainClassName = 'com.example.exampleProjectName.Main'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.lni-dev:lapi:1.0.0'
}
```
This will then add a gradle task called shadowJar, which will build an executable jar for you.
## Getting Started
First you will need to create a Discord bot and copy it's `TOKEN`.<br>
Then you can create a Config:
```java
Config config = ConfigBuilder.getDefault("TOKEN").build();
```
Then you can create a LApi instance:
```java
Config config = ConfigBuilder.getDefault("TOKEN").build();
LApi lApi = LApi.newInstance(config);
```
Or a lot simpler:
```java
LApi lApi = ConfigBuilder.getDefault("TOKEN").buildLapi();
```
Note: `TOKEN` must be replaced with your bot token.<br>
Now you can register an EventListener:
```java
lApi.getEventTransmitter().addListener(new EventListener() {
            @Override
            public void onGuildRoleCreate(GuildRoleCreateEvent event) {
                System.out.println("A Role was created.");
            }

            @Override
            public void onMessageCreate(MessageCreateEvent event) {
                System.out.println("Message: " + event.getMessage().getContent());
            }
        });
```

Inside your listener, you can overwrite all events, you want to listen to.<br>
Here a small example on how to respond to "Hi":
```java
lApi.getEventTransmitter().addListener(new EventListener() {
            @Override
            public void onMessageCreate(MessageCreateEvent event) {
                System.out.println("Message: " + event.getMessage().getContent());
                
                if(!event.getMessage().getAuthor().isBot()
                    && event.getMessage().getContent().equals("Hi")){
                    lApi.createMessage(event.getChannelId(), "Hi").queue();
                }
            }
        });
```
Note the check, if the message was sent by a bot. This check is important, so that your bot does not respond to itself.
