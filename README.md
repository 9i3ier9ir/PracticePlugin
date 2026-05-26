# PracticePlugin - Competitive PvP Queue System
**Minecraft 1.21.11 Plugin** | ELO-Based Ranking & Matchmaking

---

## 📋 Overview

PracticePlugin is a competitive PvP arena plugin that manages player queues, matchmaking, and ELO rankings across multiple gamemodes. Players queue into matches, are paired based on similar ELO ratings, and compete in 1v1 battles to climb ranks from Wood tier all the way to Netherite (top 10 only).

---

## 🎮 Core Features

### 1. **Queue System**
The plugin provides a fast and intuitive way for players to join competitive matches.

#### Queue Commands
- **`/queue <gamemode>`** - Join queue for a specific gamemode
- **`/q <gamemode>`** - Shorthand for queue
- **`/qu <gamemode>`** - Alternative shorthand for queue

#### How Matchmaking Works
1. When a player queues, the system searches for opponents with **similar ELO** (within a matching range)
2. **First 60 seconds**: Tries to match players with comparable ELO ratings
3. **After 60 seconds**: If no match found, pairs with any player waiting 60+ seconds in the queue
4. Player experiences:
   - Large gold **"MATCH FOUND"** message on screen
   - Blindness effect for 3 seconds
   - Teleported to an available map for that gamemode

#### Error Handling
- If no maps are available for the gamemode: **"No maps available"** message displayed
- Player remains in queue or is removed (TBD based on configuration)

---

### 2. **Gamemode Management**
Gamemodes are the foundation of the plugin. Each gamemode is independent with its own maps, ELO leaderboard, and kits.

#### Gamemode Creation
- **Command**: `/gamemode create <gamemode_name>`
- Creates a new gamemode container
- Each gamemode tracks:
  - Player ELO rankings
  - Available maps
  - Player spawn kits
  - Match history

#### Multiple Maps Per Gamemode
- Gamemodes can have **1 to many maps**
- Allows multiple simultaneous matches in the same gamemode
- Load balancing distributes players across available maps
- Enables scaling for popular gamemodes

---

### 3. **Map Configuration**

Maps are the battlegrounds where players compete. Proper map setup is essential.

#### Step 1: Define Map Region (WorldEdit)
1. Use WorldEdit to select the region you want to use as your map
2. Run **`/setregion <gamemode> <map_number>`**
   - `<map_number>`: Starting from 1, increment for additional maps
   - Example: `/setregion skywars 1` for the first SkyWars map

#### Step 2: Set First Player Spawn
1. Navigate to the location where the **first player** should spawn
2. Run **`/setfirstspawn <gamemode>`**
3. This saves the position for the first spawning player

#### Step 3: Set Second Player Spawn
1. Navigate to the location where the **second player** should spawn
2. Run **`/setsecondspawn <gamemode>`**
3. This saves the position for the second spawning player

#### Example Setup
```
Map Setup Workflow:
1. Create gamemode: /gamemode create boxfight
2. Select region in WorldEdit
3. /setregion boxfight 1
4. Go to player 1 spawn location: /setfirstspawn boxfight
5. Go to player 2 spawn location: /setsecondspawn boxfight
6. To add more maps, repeat steps 2-5 with /setregion boxfight 2, etc.
```

---

### 4. **Match Flow**

This is what happens when two players are matched:

#### Before Match
- Players waiting in queue for same gamemode
- System searches for ELO-matched opponents

#### Match Start
1. System finds available map for the gamemode
2. Teleports both players to their spawn positions
3. Player 1 → `/setfirstspawn` location
4. Player 2 → `/setsecondspawn` location
5. Both receive **Blindness III** effect for 3 seconds
6. Large golden text: **"MATCH FOUND"** appears on screen
7. After 3 seconds: Blindness effect ends, match begins

#### During Match
- Players spawn with their set inventory kit
- Combat occurs freely on the map
- Match ends when one player dies

#### After Match
- Winner receives ELO gain
- Loser receives ELO loss
- Both players see ELO change notification
- Players return to queue (optional) or to lobby

---

### 5. **ELO System & Ranking**

The ELO system is the heart of competitive balance. Every win/loss updates your rating.

#### ELO Fundamentals
- **Starting ELO**: Every new player begins at **100 ELO**
- **Stored In**: `elo.yml` configuration file
- **Per-Gamemode**: Each gamemode maintains separate ELO for each player
- **Progressive Gain/Loss**: ELO changes based on opponent strength

#### ELO Calculation (Conceptual)
```
If you beat a much stronger player (higher ELO):
  → Large ELO gain (+ 25-50 ELO)

If you beat a similar strength player:
  → Standard ELO gain (+ 15-20 ELO)

If you beat a weaker player (lower ELO):
  → Small ELO gain (+ 5-10 ELO)

If you lose to a stronger player:
  → Small ELO loss (- 5-10 ELO)

If you lose to a similar strength player:
  → Standard ELO loss (- 15-20 ELO)

If you lose to a weaker player:
  → Large ELO loss (- 25-50 ELO)
```

#### ELO Display
- After each match: **"You gained +15 ELO"** or **"You lost -8 ELO"**
- Shows the difference based on opponent's ELO rating

---

### 6. **Rank Tiers**

Players climb through 9 ranks based on accumulated ELO. Each rank has sub-tiers.

| Rank | Max Sub-Tier | ELO Range (Estimate) |
|------|-------------|----------------------|
| 🪵 **Wood** | 3 | 100 - 300 |
| 🪨 **Stone** | 3 | 300 - 600 |
| ⚫ **Coal** | 2 | 600 - 900 |
| 🟠 **Copper** | 3 | 900 - 1300 |
| 🔴 **Redstone** | 3 | 1300 - 1700 |
| 🟡 **Gold** | 3 | 1700 - 2100 |
| 💚 **Emerald** | 2 | 2100 - 2400 |
| 💎 **Diamond** | 3 | 2400 - 2800 |
| ⚪ **Netherite** | 1 | 2800+ (Top 10 Only) |

#### Rank Progression
- Each rank has multiple sub-tiers (e.g., Wood 1, Wood 2, Wood 3)
- Climbing through ranks requires earning ELO
- Ranks are **per-gamemode** (you can be Diamond in BoxFight but only Stone in SkyWars)
- **Netherite tier** is exclusive to the top 10 players in each gamemode

---

### 7. **Kit Management**

Kits determine what items players spawn with during matches.

#### Setting a Kit
1. Arrange your inventory exactly how you want it
2. Run **`/invset <gamemode>`**
3. Your current inventory is copied and saved as the spawn kit

#### Kit Features
- **Per-Gamemode**: Each gamemode can have different kits
- **Personal Kits**: Each player has their own kit per gamemode
- **Armor & Items**: Everything in your inventory is saved (including armor slots)
- **Persistence**: Kit is saved in configuration until changed

#### Example
```
Player wants to play BoxFight with diamond armor and sword:
1. Equip: Diamond helmet, chestplate, leggings, boots, diamond sword
2. Add potions or utility items to hotbar
3. Run: /invset boxfight
4. Next time player spawns in a BoxFight match, they spawn with this exact kit
```

---

### 8. **Player Statistics & Leaderboards**

Players can check their progress and compare with others.

#### View Your Stats
- **Command**: `/eloview`
- Displays:
  - Your current ELO for each gamemode
  - Your current rank and sub-tier
  - Total wins/losses (optional)
  - Win rate percentage (optional)

#### View Another Player's Stats
- **Command**: `/eloview <player_name>`
- Displays that player's public statistics

#### Example Output
```
═══ ELO Statistics ═══
BoxFight: 1250 ELO (Gold 2)
SkyWars: 850 ELO (Copper 1)
UHC: 320 ELO (Wood 3)

Wins: 145 | Losses: 89 | Win Rate: 61.9%
```

---

## 📁 File Structure

```
PracticePlugin/
├── README.md (This file)
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/practice/
│   │   │       ├── PracticePlugin.java (Main plugin class)
│   │   │       ├── managers/
│   │   │       │   ├── QueueManager.java
│   │   │       │   ├── GamemodeManager.java
│   │   │       │   ├── EloManager.java
│   │   │       │   └── MapManager.java
│   │   │       ├── listeners/
│   │   │       │   ├── PlayerDeathListener.java
│   │   │       │   ├── PlayerQuitListener.java
│   │   │       │   └── MatchListener.java
│   │   │       ├── commands/
│   │   │       │   ├── QueueCommand.java
│   │   │       │   ├── GamemodeCommand.java
│   │   │       │   ├── MapCommands.java
│   │   │       │   ├── InvsetCommand.java
│   │   │       │   └── EloviewCommand.java
│   │   │       ├── utils/
│   │   │       │   ├── ConfigManager.java
│   │   │       │   └── EloCalculator.java
│   │   │       └── models/
│   │   │           ├── Gamemode.java
│   │   │           ├── Map.java
│   │   │           ├── Match.java
│   │   │           └── PlayerStats.java
│   │   └── resources/
│   │       ├── plugin.yml
│   │       └── config.yml
│   └── test/ (Unit tests)
├── config/
│   ├── elo.yml (ELO storage)
│   ├── gamemodes.yml (Gamemode configurations)
│   ├── kits.yml (Player kits)
│   └── maps.yml (Map configurations)
└── pom.xml (Maven configuration)
```

---

## 🛠️ Configuration Files

### `elo.yml` - ELO Storage
Stores all player ELO ratings per gamemode.

```yaml
players:
  Steve:
    boxfight: 1250
    skywars: 850
    uhc: 320
  Alex:
    boxfight: 2100
    skywars: 1500
```

### `gamemodes.yml` - Gamemode Definitions
Defines all gamemodes and their properties.

```yaml
gamemodes:
  boxfight:
    name: "BoxFight"
    maps: 3
    firstSpawn: "world,100,64,100"
    secondSpawn: "world,110,64,100"
  skywars:
    name: "SkyWars"
    maps: 5
```

### `kits.yml` - Player Kits
Stores inventory kits for each player per gamemode.

```yaml
kits:
  Steve:
    boxfight:
      helmet: "DIAMOND_HELMET"
      chestplate: "DIAMOND_CHESTPLATE"
      leggings: "DIAMOND_LEGGINGS"
      boots: "DIAMOND_BOOTS"
      items:
        - "DIAMOND_SWORD"
        - "BOW"
```

---

## 📊 Command Reference

| Command | Alias | Usage | Description |
|---------|-------|-------|-------------|
| `/queue` | `/q`, `/qu` | `/queue <gamemode>` | Join a competitive queue |
| `/gamemode create` | N/A | `/gamemode create <name>` | Create a new gamemode |
| `/setregion` | N/A | `/setregion <gamemode> <number>` | Set map region (use WorldEdit first) |
| `/setfirstspawn` | N/A | `/setfirstspawn <gamemode>` | Set player 1 spawn location |
| `/setsecondspawn` | N/A | `/setsecondspawn <gamemode>` | Set player 2 spawn location |
| `/invset` | N/A | `/invset <gamemode>` | Save current inventory as spawn kit |
| `/eloview` | N/A | `/eloview [player]` | View ELO stats (yours or another player's) |

---

## 🎯 Player Journey Example

### Day 1: New Player
1. Player joins server
2. Runs `/queue boxfight`
3. Waits for opponent
4. Gets "MATCH FOUND" message with blindness
5. Spawned at default location with default kit
6. Loses match: `-8 ELO` (now at 92 ELO, Wood tier)

### Day 5: Improving Player
1. Wins several matches, reaches 250 ELO (Wood 3)
2. Customizes kit: `/invset boxfight` (adds armor and sword)
3. Wins more consistently
4. Climbs to 320 ELO (Stone 1)
5. Continues grinding

### Day 30: Ranked Player
1. Reaches 1250 ELO (Gold 2 in BoxFight)
2. Noticed as a competitive player
3. Matchmaking pairs them with similarly skilled opponents
4. Views leaderboard: `/eloview steve` to see their rank
5. Aims to reach Diamond tier

### Day 100: Top Player
1. Reaches 2500 ELO in BoxFight
2. Competes with other top-ranked players
3. Working toward Netherite tier (top 10 only)
4. Has different ELO ratings across multiple gamemodes

---

## 🔧 Technical Highlights

### Performance Considerations
- **Queue Matching**: Background task runs every second to match players
- **ELO Calculations**: Stored in memory, written to disk after each match
- **Multi-Gamemode Support**: Each gamemode operates independently
- **Concurrent Matches**: Multiple matches can occur simultaneously on different maps

### Data Persistence
- All ELO data saved to `elo.yml`
- Survives server restarts
- Player progression is permanent

### Matchmaking Algorithm
1. Fetch all players in queue for a gamemode
2. For each queued player, find opponents within ELO range
3. If match found: Remove both from queue, start match
4. If no match in 60 seconds: Expand search to anyone waiting 60+ seconds
5. Repeat every 1 second

---

## 🎨 User Experience Features

### Visual Feedback
- Large golden "MATCH FOUND" message
- Blindness effect for dramatic entry
- Teleport animation to battle arena
- ELO change notification after match

### Rank Progression
- Clear tier system (9 ranks)
- Visual progression from Wood → Netherite
- Exclusive Netherite tier rewards dedication
- Per-gamemode rankings for variety

### Competitive Balance
- ELO-based matchmaking prevents stomping
- Difficulty scaling through skill-based matching
- New players protected from experts
- Grinders rewarded with harder opponents

---

## 🚀 Future Enhancement Ideas

- [ ] Ranked seasons with resets
- [ ] Leaderboard commands
- [ ] Win/loss streaks
- [ ] Achievement system
- [ ] Statistics tracking (KD ratio, etc.)
- [ ] Party/team queue system
- [ ] Ban system for toxic players
- [ ] Replay system
- [ ] Prize pool for top players
- [ ] Cross-gamemode ELO points

---

## 📝 Notes

- Plugin is designed for **1v1 competitive matches only**
- Each gamemode operates independently with separate ELO
- Top 10 Netherite ranking is exclusive and changes dynamically
- Maps can be added to gamemodes after creation
- Kits are personal and per-gamemode (not global)
- ELO resets can be configured per season

---

**Version**: 1.0.0  
**Minecraft**: 1.21.11  
**Status**: In Development