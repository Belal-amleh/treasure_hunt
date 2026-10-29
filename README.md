# Treasure Hunt Game

A Java 17 implementation of the Systems Engineering Treasure Hunt project.

## Features
- 10 x 10 grid by default
- Player starts at zero-based position `(9, 0)` (bottom-left)
- 20 moves and 3 lives by default
- One visible treasure, 5 traps, and 5 enemies by default
- Traps cost 3 additional moves; enemies cost one life
- Invalid/out-of-bounds moves do not consume moves
- A hazard-free path to the treasure is reserved and requires at most 20 moves
- Reproducible grid generation with a seed
- Console and Swing interfaces share the same `Game` engine
- New game or exit after a game ends

## Requirements
- Java Development Kit (JDK) 17 or newer
- No third-party libraries required

## Compile
From this folder, run:

```bash
javac -d dist $(find src -name "*.java")
```

On Windows PowerShell, use:

```powershell
$files = Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName }
javac -d dist $files
```

## Run the Swing interface

```bash
java -cp dist treasurehunt.Main
```

## Run the console interfacex

```bash
java -cp dist treasurehunt.Main --console
```

Optional seed:

```bash
java -cp dist treasurehunt.Main --console --seed 12345
```

For Swing:

```bash
java -cp dist treasurehunt.Main --seed 12345
```

The same seed and configuration produce the same world with this version of the generator. Choosing "New game" derives a new seed from the original seed.

## Controls
- Console: enter `W`, `A`, `S`, or `D` for north, west, south, or east.
- Swing: click an adjacent tile button, use the direction buttons, or use arrow keys / WASD.
- `P` = player, `T` = treasure, `X` = trap, `E` = enemy, `.` = empty.
- The player's current position is shown as `P`, so if the player stands on the treasure or a hazard, the player symbol takes visual priority.

## Project structure
- `model`: player, grid, tiles, and tile results
- `engine`: game rules, configuration, movement/result enums, and grid generation
- `ui`: common UI interface, console UI, and Swing UI
- `test`: lightweight manual test checklist

## Design notes
- `Game` owns the rules and processes movement.
- `Tile.onEnter(Player)` implements polymorphic tile effects.
- `Grid` is immutable in its layout after construction; the generator builds the complete tile array first.
- `GameConfig` validates dimensions, resources, and hazard counts.
- The generator reserves an L-shaped safe path from the start to the treasure before placing hazards.
