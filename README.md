# LApi 
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/lni-dev/lapi.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/lni-dev/lapi/context:java)
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/lni-dev/lapi/CodeQL)
![Maven Central](https://img.shields.io/maven-central/v/io.github.lni-dev/lapi?color=%2300dd00)
![Lines of code](https://img.shields.io/tokei/lines/github/lni-dev/lapi)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/lni-dev/lapi)
<br>LApi is a Discord API written in Java

## Installation
In order to install it, you can either build it yourself or use gradle and implement it into your Project:<br><br>
In your `build.gradle` add `mavenCentral()` to the repositories if you have not done so already and add `io.github.lni-dev:lapi:[version]` to the dependencies. Replace `[version]` with the version you want to install.
 <br><br>![Maven Central](https://img.shields.io/maven-central/v/io.github.lni-dev/lapi?label=current%20newest%20version%3A%20)
```groovy
repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor 'io.github.lni-dev:lapi-annotation-processor:1.0.0'
    implementation 'io.github.lni-dev:lapi:[version]'
}
```

<br>An example `build.gradle` could look like this:
```groovy
plugins {
    id 'java'
}

group 'com.example'
version 'your.version'

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor 'io.github.lni-dev:lapi-annotation-processor:1.0.0'
    implementation 'io.github.lni-dev:lapi:1.0.3'
}
```
(Tested on gradle 7.5.1)

<br>If you want to make an executable .jar file at some point in time, I recommend
the gradle plugins `application` and `shadow`. An example `build.gradle` with these plugins could
look like this:
```groovy
plugins {
    id 'java'
    id 'application'
    id 'com.github.johnrengelman.shadow' version '7.1.2'
}

group 'com.example'
version '1.0.0'
mainClassName = 'com.example.exampleProjectName.Main'

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor 'io.github.lni-dev:lapi-annotation-processor:1.0.0'
    implementation 'io.github.lni-dev:lapi:1.0.3'
}
```
This will then add a gradle task called shadowJar, which will build an executable jar for you.
## Getting Started
First you will need to create a Discord bot and copy it's `TOKEN`.<br>
Then you can create a Config:
```java
Config config = ConfigBuilder.getDefault("TOKEN", true).build();
```
And create a LApi instance:
```java
Config config = ConfigBuilder.getDefault("TOKEN", true).build();
LApi lApi = LApi.newInstance(config);
```
Or a lot simpler:
```java
LApi lApi = ConfigBuilder.getDefault("TOKEN", true).buildLApi();
```
`TOKEN` must be replaced with your bot token. The second boolean parameter specifies, whether
you want the privileged intents enabled. read more [here](https://discord.com/developers/docs/topics/gateway#privileged-intents).
If you pass `true`, you have to enabled them for your application's bot [here](https://discord.com/developers/applications).
<br><br>
Now you can register an EventListener:
```java
lApi.getEventTransmitter().addListener(new EventListener() {
    @Override
    public void onMessageCreate(@NotNull LApi lApi, @NotNull MessageCreateEvent event) {
        //code
    }

    @Override
    public void onMessageUpdate(@NotNull LApi lApi, @NotNull MessageUpdateEvent event) {
        //code
    }

    @Override
    public void onMessageDelete(@NotNull LApi lApi, @NotNull MessageDeleteEvent event) {
        //code
    }
});
```

Inside your listener, you can overwrite all events, you want to listen to.<br>
Here a small example on how to respond to "Hi":
```java
lApi.getEventTransmitter().addListener(new EventListener() {
    @Override
    public void onMessageCreate(@NotNull LApi lApi, @NotNull MessageCreateEvent event) {
        System.out.println("Message: " + event.getMessage().getContent());

        if(!event.getMessage().getAuthor().isBot() && event.getMessage().getContent().equals("Hi")){
            lApi.getRequestFactory().createMessage(event.getChannelId(), "Hi").queue();
        }
    }
});
```
Note the check, if the message was sent by a bot. This check is important, so that your bot does not respond to itself.
