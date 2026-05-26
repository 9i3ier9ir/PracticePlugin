# PracticePlugin - Features Implemented

## Build Status
✅ Successfully compiled and packaged to `target/PracticePlugin-1.0.0.jar` (46 KB)

## Core Features

### 1. Queue & Matchmaking System
- `/queue <gamemode>` - Join queue for a gamemode
- ELO-based matching algorithm (strict ±200 ELO within first 60 seconds, then flexible)
- Automatic match creation when 2 compatible players found

### 2. ELO & Ranking System
- Per-gamemode ELO tracking (K-factor: 32)
- 9 Rank Tiers: Wood (1-3), Stone (1-3), Coal (1-2), Copper (1-3), Redstone (1-3), Gold (1-3), Emerald (1-2), Diamond (1-3), Netherite (1)
- **NEW: Rank-up Notifications** - Players get title + message when they advance ranks/tiers
- `/eloview [player]` - View player ELO across all gamemodes

### 3. Gamemode Management
- `/gamemode create <name>` - Create new gamemode
- `/gamemode delete <name>` - Delete gamemode
- `/gamemode addmap <gamemode>` - Add map to gamemode
- `/gamemode list` - List all gamemodes
- **NEW: `/setrounds <gamemode> <rounds>`** - Set number of rounds for duels in a gamemode
- Rounds persisted to config and loaded on server restart

### 4. Map Configuration
- `/setfirstspawn <gamemode> [mapNumber]` - Set player 1 spawn
- `/setsecondspawn <gamemode> [mapNumber]` - Set player 2 spawn
- **NEW: `/setregion <gamemode> [mapNumber]` with WorldEdit Support**
  - Automatically detects and uses WorldEdit selection if plugin installed
  - Graceful fallback if WorldEdit not available
  - Map boundaries restrict player movement

### 5. Duel System (NEW)
- **Non-ELO ranked duels** - Practice without affecting rating
- **Multi-round duels** - Best-of-N format using `/setrounds`
- **Commands:**
  - `/duel <player> <gamemode> <rounds>` - Challenge to duel
  - `/d <player> <gamemode> <rounds>` - Alias for duel
- **Features:**
  - Per-round tracking (e.g., "Round 2 of 5")
  - Automatic winner determination when player reaches rounds_needed/2 + 1 wins
  - No ELO changes from duels (practice only)
  - Disconnect forfeit support

### 6. Kit/Inventory Management
- `/invset <gamemode>` - Save current inventory as kit for gamemode
- Kits applied at match/duel start for consistent gameplay

### 7. Data Persistence
All data saved to YAML files:
- `elo.yml` - Player ELO ratings
- `gamemodes.yml` - Gamemode configs with rounds
- `kits.yml` - Player inventory presets
- `maps.yml` - Map spawn locations & regions

## File Structure
```
src/main/java/com/practice/plugin/
├── PracticePlugin.java (main entry point)
├── commands/
│   ├── DuelCommand.java (NEW)
│   ├── GamemodeCommand.java
│   ├── QueueCommand.java
│   ├── SetRoundsCommand.java (NEW)
│   ├── InventoryCommand.java
│   ├── MapCommand.java (enhanced)
│   └── EloViewCommand.java
├── managers/
│   ├── QueueManager.java (enhanced with duel system)
│   ├── GamemodeManager.java (enhanced with rounds loading)
│   ├── EloManager.java
│   ├── MapManager.java
│   └── ConfigManager.java
├── models/
│   ├── Duel.java (NEW)
│   ├── Gamemode.java (enhanced with roundsNeeded)
│   ├── GameMap.java
│   ├── Match.java
│   └── PlayerStats.java
└── listeners/
    ├── PlayerDeathListener.java (enhanced for duels)
    ├── PlayerQuitListener.java (enhanced for duels)
    └── BlockBreakListener.java
```

## How to Use

### Setup
1. Place JAR in server `/plugins/` folder
2. Restart server
3. Create gamemodes: `/gamemode create skywars`
4. Add maps: `/gamemode addmap skywars`
5. Set spawns: `/setfirstspawn skywars` and `/setsecondspawn skywars`
6. Set region (optional, with WorldEdit): `/setregion skywars`
7. Set kit: Hold items, run `/invset skywars`
8. Set rounds (optional): `/setrounds skywars 3`

### Playing
- Queue: `/queue skywars`
- View ELO: `/eloview`
- Duel: `/duel PlayerName skywars` or `/d PlayerName skywars` (uses gamemode's round setting)

## Technical Details

### Duel Implementation
- Tracked separately from matches in `QueueManager.activeDuels` Map
- Each duel tracks: player1, player2, current round scores, total rounds needed
- Non-ELO: Deaths end duel rounds but don't affect rating
- Disconnect handling: Opponent gets notification, duel ends

### Rank-up System
- Calculated in `EloCalculator.checkRankup()` after ELO change
- Returns formatted message with old/new rank
- Displayed to both players with title + message

### WorldEdit Integration
- Optional dependency using reflection
- No hard requirement if WorldEdit not installed
- Automatic detection of player's selection
- Fallback: Helpful error if not available

## Compilation Notes
- Target: Java 17+
- Build: `mvn clean package`
- Dependencies: Paper API 1.20.4
- Output: `target/PracticePlugin-1.0.0.jar` (46 KB)

## Permissions
- `practiceplugin.gamemode.create` - Gamemode creation (OP)
- `practiceplugin.gamemode.delete` - Gamemode deletion (OP)
- `practiceplugin.duel` - Duel command (default: true)
- All other commands inherit OP requirement
