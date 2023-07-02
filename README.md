# TheManHunt

## About
This is a Mini-Game about Runners and Hunters. The goal of the Runners is to finish the game (see the credits).
The Hunters are supposed to stop / kill them before they finish the game.
The team sizes don't really matter, but keep in mind that being a Runner is probably harder.

## Features
- Works without any dependencies and minimal setup
- Runner tracking compass
- Compass works in Nether
- 2 teams and spectators
- Customizable GUI for team selection
- Players can select their own language independently
- Special Abilities like freeze vision and one hit killing
- Game can be returned after the server stopped and starts up again
- Automatic world resetting
- Administration commands
- Command alternative to GUIs
- Very customisable config
- Multiple game-modes (essentially setting presets)
- Easily add more languages for players to choose from

## Roadmap
The following features are to be added, with the top most entry being the highest priority.
- GUI for language selection
- Zombie game-mode (dead Runners become Hunters)
- A separate lobby / waiting world

## Installation
Download the compiled binary either from the release tab or from the [spigot.com](https://www.spigotmc.org/resources/themanhunt.105044/) page.
Add the Jar in your plugins folder and start / restart your server.

## Usage

### Commands

game:
- description: Starts the game if it isn't already running and if there are enough players in the Teams
- usage: `/game <start|stop>`
- permission: TheManHunt.GameManagement

team:
- description: To join a team, list members of a team, add or remove a player from a team
- usage: `/team <join|list|add|remove> [<Player Name>] <Team Name>`
- permission:
  - TheManHunt.GameManagement (join|list|add|remove)
  - TheManHunt.Player (join)

language:
- description: To select your language
- usage: `/language <language>`
- permission: TheManHunt.Player


### Config
The config options in the default config files should have explaining comments.
The game mode files are special config files that basically override the normal config.
Keep in mind that game mode files have to be in plugins/TheManHunt/game_modes to be usable.

## Credits
The base idea of this Mini-Game comes from the Youtuber Dream. <br>
Link to his YouTube channel: [Dream](https://www.youtube.com/c/dream)

## Contributing
Bugfixes are very welcome and will probably be added.<br>
If you are a more advanced programmer, I would also appreciate some general best practices ant tips that I didn't follow.
But please provide an explanation on why I should follow your tip inform of a Link or just an explanation in chat.
Just start a private conversation and send those to me at the
[Spigot](https://www.spigotmc.org/members/cubelegend.1007733/) forum. <br>
If you want to add a new feature please message me on [Spigot](https://www.spigotmc.org/members/cubelegend.1007733/)
first, so that you can be sure that I will accept your contribution. 
If I don't like your feature idea, there is of course always the option to fork this project. 

## Build
Clone this repository. <br>
Then compile and package it with Maven: `mvn clean compile package`
