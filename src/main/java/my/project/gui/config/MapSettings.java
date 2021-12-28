package my.project.gui.config;

import my.project.simulation.enums.MapStrategy;

public record MapSettings (
    int width,
    int height,
    double jungleRatio,
    int startEnergy,
    int moveEnergy,
    int bushEnergy,
    int grassEnergy,
    int animalsCount,
    int magicRespawnsCount,
    int magicRespawnAnimals,
    MapStrategy mapStrategy) {
}
