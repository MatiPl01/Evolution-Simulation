This project was made for the Object-Oriented Programming course at the AGH UST in 2021/2022.

## Description
This project is the implementation of a simple simulator based on Darwin's Theory. Animals are moving on the map, eating grass, and reproducing. Each move requires some energy, so animals need to eat to survive. Also, breeding consumes part of the parent's energy. An animal dies after having run out of energy.

Each animal has its genome. A genome is a combination of genomes inherited from an animal's parents. Genome is built of numbers from 0 to 7. Each number is correlated with the animal's preference to move in a specific direction. The more repetition of a digit, the more likely it is for the animal to move in the direction represented by this digit.

## Features
- Adjusting the speed of the simulation,
- Highlighting animals with dominant genomes,
- Showing dominant genomes list and animals that have such genomes,
- Tracking the specific animal (the number of its direct children and indirect descendants),
- Checking which animals are placed on the selected field,
- Showing simulation preview window,
- Showing statistics chart,
- Saving statistics to a file
- Displaying simulation on two maps at the same time:
  - Folding map - a map on which, after crossing the border, the animal will be on the other side of the map,
  - Fenced map - a map surrounded by a fence which makes it impossible for animals to be on the other side of the map,
- Magic strategy - a simulation mode in which the number of animals will be doubled after it has dropped to the specific value. The number of such respawns is limited.
- A possibility to adjust many settings before starting the simulation.

## Core technology stack
- Java 16,
- Gradle,
- JavaFX

## Settings
![Settings](/docs/settings.gif)

## Resizing window parts
![Resizing window parts](/docs/resizing.gif)

## Preview
![Preview](/docs/preview.gif)

## Saving statistics to a file
![Saving statistics to a file](/docs/saving-file.gif)

## Using only one map
![Using only one map](/docs/one-map.gif)
