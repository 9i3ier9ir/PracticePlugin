# PracticePlugin - Minecraft 1.21+ Competitive PvP Queue System
A fully-featured Minecraft plugin for running competitive PvP practice servers with ELO ranking, multi-round duels, and customizable gamemodes. Built for Paper API 1.20.4+ with optional WorldEdit integration.

**Status:** ✅ PRODUCTION READY  
**JAR Size:** 46 KB  
**Java Target:** 17+

---

## Table of Contents

- [Quick Start](#quick-start)
- [Features](#features)
- [Installation](#installation)
- [Commands](#commands)
- [Setup Guide](#setup-guide)
- [Testing](#testing)
- [Technical Details](#technical-details)
- [Troubleshooting](#troubleshooting)

---

## Quick Start

```bash
# 1. Copy JAR to server
cp target/PracticePlugin-1.0.0.jar /path/to/server/plugins/

# 2. Restart server (auto-creates data folder)

# 3. Setup in-game (OP commands)
/gamemode create skywars
/gamemode addmap skywars
/setfirstspawn skywars 1
/setsecondspawn skywars 1
/setrounds skywars 3

# 4. Play!
/queue skywars          # Join ranked queue
/d PlayerName skywars   # Challenge to duel
/eloview                # Check your rating
```

---

## Features

### 1. Queue & Matchmaking System
- `/queue <gamemode>` - Join competitive queue
- ELO-based matching algorithm:
  - **First 60s:** Strict ±200 ELO range
  - **After 60s:** Flexible matching with anyone waiting
- Automatic match creation when compatible players found
- Match start teleport + blindness effect

### 2. ELO & Ranking System
- **Per-gamemode tracking** - Each gamemode tracks separate ELO
- **K-factor:** 32
- **9 Rank Tiers:**
  - Wood (1-3)
  - Stone (1-3)
  - Coal (1-2)
  - Copper (1-3)
  - Redstone (1-3)
  - Gold (1-3)
  - Emerald (1-2)
  - Diamond (1-3)
  - Netherite (1, top 10 only)
- **Rank-up Notifications:** Titles and messages when advancing
- `/eloview [player]` - View ratings across gamemodes

### 3. Gamemode Management
- `/gamemode create <name>` - Create a new gamemode
- `/gamemode delete <name>` - Delete a gamemode
- `/gamemode addmap <gamemode>` - Add a new map to a gamemode
- `/gamemode list` - List all gamemodes
- `/setrounds <gamemode> <rounds>` - Set duel round count

### 4. Map Configuration
- `/setfirstspawn <gamemode> [map]` - Set player 1 spawn
- `/setsecondspawn <gamemode> [map]` - Set player 2 spawn
- `/setregion <gamemode> [map]` - Set map bounds using WorldEdit selection

### 5. Duel System (Non-ELO)
- `/duel <player> <gamemode>` - Challenge a player to a duel
- `/d <player> <gamemode>` - Alias for duel
- Best-of-N rounds using `/setrounds`
- No ELO changes from duels
- Disconnect handling and round tracking

### 6. Kit/Inventory Management
- `/invset <gamemode>` - Save current inventory as kit for gamemode
- Kits are applied when a match or duel starts

### 7. Data Persistence
Stored in YAML files under `plugins/PracticePlugin/`:
- `elo.yml` - Player ELO ratings
- `gamemodes.yml` - Gamemode configurations and round settings
- `kits.yml` - Player inventory kits
- `maps.yml` - Map spawns and regions

---

## Installation

### Requirements
- Minecraft Server 1.20.4+ (Paper recommended)
- Java 17+
- Optional: WorldEdit (required only for `/setregion`)

### Steps
1. Copy `target/PracticePlugin-1.0.0.jar` into your server `plugins/` folder
2. Restart the server
3. Configure gamemodes and maps with the commands below

---

## Commands

### Player Commands

| Command | Alias | Description |
|---------|-------|-------------|
| `/queue <gamemode>` | `/q`, `/qu` | Join competitive queue |
| `/duel <player> <gamemode>` | `/d` | Challenge to duel |
| `/eloview [player]` | - | View ELO ratings |
| `/invset <gamemode>` | - | Save current inventory kit |

### Admin Commands

| Command | Usage | Permission | Description |
|---------|-------|------------|-------------|
| `/gamemode create <name>` | Create a gamemode | `practiceplugin.gamemode.create` | Create new gamemode |
| `/gamemode delete <name>` | Delete a gamemode | `practiceplugin.gamemode.create` | Delete gamemode |
| `/gamemode addmap <gamemode>` | Add a map | `practiceplugin.gamemode.create` | Add a map to a gamemode |
| `/gamemode list` | List gamemodes | `practiceplugin.gamemode.create` | List gamemodes |
| `/setfirstspawn <gamemode> [map]` | Set player 1 spawn | `practiceplugin.map.set` | Set spawn |
| `/setsecondspawn <gamemode> [map]` | Set player 2 spawn | `practiceplugin.map.set` | Set spawn |
| `/setregion <gamemode> [map]` | Set region bounds | `practiceplugin.map.set` | Set map boundaries |
| `/setrounds <gamemode> <rounds>` | Set duel rounds | `practiceplugin.gamemode.create` | Set rounds for gamemode |

### Permissions

```yaml
practiceplugin.queue: true
practiceplugin.duel: true
practiceplugin.invset: true
practiceplugin.eloview: true
practiceplugin.gamemode.create: op
practiceplugin.map.set: op
```

---

## Setup Guide

### Basic Setup

```bash
/gamemode create skywars
/gamemode addmap skywars
/setfirstspawn skywars 1
/setsecondspawn skywars 1
/setrounds skywars 3
```

### Optional WorldEdit Region Setup

1. Select area with WorldEdit: `//pos1` and `//pos2`
2. Run: `/setregion skywars 1`

### Multiple Maps

```bash
/gamemode addmap skywars
/setfirstspawn skywars 2
/setsecondspawn skywars 2
//pos1 && //pos2
/setregion skywars 2
/setrounds skywars 5
```

---

## Testing

### Quick Test

1. Create test gamemode and add map
2. Set both spawns and rounds
3. Start a queue match with two players
4. Start a duel with `/d PlayerName skywars`
5. Confirm ELO does not change after duel

### Feature Checklist

- [ ] Queue matching works
- [ ] Matches start and teleport players
- [ ] Spawns and regions work
- [ ] ELO updates correctly after matches
- [ ] Rank-up notifications appear when rank changes
- [ ] ` /setrounds` persists in `gamemodes.yml`
- [ ] Duels run best-of-N correctly
- [ ] Duels do not change ELO
- [ ] Disconnects properly end duels
- [ ] WorldEdit `/setregion` works if installed

### Important Tests

#### Duel System
- `/d PlayerB skywars`
- Round ends on first death
- Match resets for next round
- Duel ends after required wins
- `/eloview` remains unchanged for both players

#### WorldEdit
- With WorldEdit: selection + `/setregion skywars 1`
- Without WorldEdit: command returns helpful error
- Region boundaries prevent leaving map during match

---

## Technical Details

### File Structure

```
src/main/java/com/practice/plugin/
├── PracticePlugin.java
├── commands/
│   ├── DuelCommand.java
│   ├── GamemodeCommand.java
│   ├── QueueCommand.java
│   ├── SetRoundsCommand.java
│   ├── InventoryCommand.java
│   ├── MapCommand.java
│   └── EloViewCommand.java
├── managers/
│   ├── QueueManager.java
│   ├── GamemodeManager.java
│   ├── EloManager.java
│   ├── MapManager.java
│   └── ConfigManager.java
├── models/
│   ├── Duel.java
│   ├── Gamemode.java
│   ├── GameMap.java
│   ├── Match.java
│   └── PlayerStats.java
└── listeners/
    ├── PlayerDeathListener.java
    ├── PlayerQuitListener.java
    └── BlockBreakListener.java
```

### ELO System

- **Starting ELO:** 100
- **K-factor:** 32
- **Per-gamemode ratings**
- **Saved to** `elo.yml`
- **Rank-up detection** on match win

### Duel System

- **Non-ELO practice duels**
- **Best-of-N** logic using `/setrounds`
- **Round wins** tracked per player
- **Disconnects** end the duel and notify opponent
- **Separate tracking** from ranked queue matches

### WorldEdit Integration

- Optional dependency via reflection
- Works if WorldEdit installed and selection exists
- Fallback error if WorldEdit is missing
- `/setregion` uses the player's current selection

### Data Persistence

- `elo.yml` - Player ratings per gamemode
- `gamemodes.yml` - Gamemode and round data
- `kits.yml` - Saved player kits
- `maps.yml` - Map spawns and regions

---

## Troubleshooting

### Common Issues

#### Players not matching
- Verify gamemode exists
- Verify at least one map is added
- Verify spawns are set
- Check for console errors

#### Duel command fails
- Verify both players are online
- Verify neither is already in queue or match
- Verify gamemode has maps
- Verify `/duel` permission is allowed

#### `/setregion` errors
- Install WorldEdit if missing
- Make a valid selection with `//pos1` and `//pos2`
- Run the command again

#### ELO is incorrect
- Verify match results are recorded
- Rank ups are only triggered after ranked matches
- Duels do not affect ELO

#### Rounds not persisting
- Confirm `gamemodes.yml` contains `rounds: <number>`
- Restart server to reload config

---

## Build & Deployment

```bash
mvn clean compile
mvn clean package
```

**Output:** `target/PracticePlugin-1.0.0.jar`

**Dependencies:**
- Paper API 1.20.4-R0.1-SNAPSHOT
- Java 17+

---

## Notes

- Duels are designed for practice and do not affect rating
- Each gamemode has its own leaderboard and ELO seed
- WorldEdit support is helpful but optional
- Maps are rotated randomly when multiple maps exist

---

## Ready to Deploy

```bash
cp target/PracticePlugin-1.0.0.jar /path/to/server/plugins/
```

Restart the server and configure your first gamemode.
