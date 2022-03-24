# LApi
LApi is a Discord API written in Java

## Installation
TODO

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
