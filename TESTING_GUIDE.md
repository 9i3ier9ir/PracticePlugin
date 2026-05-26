# PracticePlugin - Testing Guide for New Features

## Quick Setup (First Time)

```bash
# 1. Copy JAR to server
cp target/PracticePlugin-1.0.0.jar /path/to/server/plugins/

# 2. Start server (plugin will create data folder)
# 3. Run these commands in-game as OP:

# Create test gamemode
/gamemode create test1

# Add a map to gamemode
/gamemode addmap test1

# Set spawn locations (stand where you want each spawn)
/setfirstspawn test1 1
/setsecondspawn test1 1

# Optional: Set duel rounds
/setrounds test1 3

# Optional: Save inventory kit
/invset test1
```

## Feature Testing Checklist

### ✅ Test 1: Basic Queue System (Already existed)
1. Two players run `/queue test1`
2. **Expected:** Players match within ~3 seconds, get teleported to map, get blindness effect
3. **Verify:** Players can fight normally

### ✅ Test 2: ELO & Ranking System (Already existed)
1. After match ends, victor gets ELO notification
2. Run `/eloview` to see rating
3. **Expected:** Winner's ELO increases, loser's decreases

### ✅ Test 3: RANK-UP NOTIFICATIONS (NEW)
1. Have Player A beat Player B multiple times to reach new rank/tier
2. **Expected:** When ranking up:
   - Player A sees title effect (yellow/gold colored)
   - Chat message: "You advanced to Gold 2!"
   - Player B sees: "Player A advanced to Gold 2"
   - Message includes color formatting

**Testing Steps:**
- Player starts with ~100 ELO (Wood tier)
- Each win vs equal player: +16 ELO  
- Win vs better player: +20-32 ELO
- Aim for ~200+ total wins to see multiple rank-ups

### ✅ Test 4: /setrounds Command (NEW)
```
/setrounds test1 3
```
**Expected:**
- Command executes: "Set test1 to 3 rounds"
- Rounds saved to `gamemodes.yml`
- Reload server → `/setrounds test1` should show 3 rounds (if loaded)

**Verify Config:**
- Check `plugins/PracticePlugin/gamemodes.yml`
- Should contain: `rounds: 3` under test1 gamemode

### ✅ Test 5: DUEL System (NEW - Core)
```
# Player A runs:
/duel PlayerB test1
# or shorter:
/d PlayerB test1
```

**Expected Behavior:**
1. ✅ Command accepted, Player A & B get notification
2. ✅ Both players teleported to test1 map, get blindness
3. ✅ Players fight
4. ✅ First death: ends ROUND 1, NOT the duel
5. ✅ Chat shows: "Round 1/3 won by PlayerA"
6. ✅ Players respawned at spawns for Round 2
7. ✅ After 2 wins (in 3-round duel): Duel ends
8. ✅ Winner announced, duel ends
9. ✅ **IMPORTANT:** Check `/eloview` - ELO should NOT change from duel!

**Test Steps:**
```
# Setup
/d PlayerB test1
→ Both teleported, Round 1 starts

# Player A wins first death
→ Message: "Round 1/3: PlayerA wins!"
→ Both get back at spawns

# Player A wins second death
→ Message: "Duel Over! PlayerA wins 2-0!"
→ Match ends

# Verify
/eloview
→ Neither player's ELO changed (duels don't count)
```

### ✅ Test 6: Multi-Round Duel with Custom Rounds (NEW)
```
# Create 5-round duel
/d PlayerB test1
# Then after first duel ends, check that next duel uses gamemode's setrounds setting
/setrounds test1 5
/d PlayerB test1
```

**Expected:**
- Duel goes to 5 rounds (need 3 wins to win duel)
- Each round displays counter correctly
- After reaching 3 wins, duel ends

### ✅ Test 7: Duel with Disconnect (NEW)
```
# Player A challenges Player B
/d PlayerB test1

# During duel, Player B disconnects
```

**Expected:**
- Player A gets message: "PlayerB disconnected. Duel ended."
- Player A can queue/duel again
- ELO unchanged

### ✅ Test 8: WorldEdit Integration (NEW)
**Requirements:** Must have WorldEdit plugin installed

```
# With WorldEdit:
# 1. Make a selection with WorldEdit (//pos1, //pos2 or wand)
# 2. Run:
/setregion test1 1

# Expected: "Region set successfully for test1"
```

**Test Match Boundaries:**
1. Get in match/duel in test1
2. Try to walk outside the region
3. **Expected:** Can't walk outside set boundary

**Without WorldEdit:**
```
/setregion test1 1
# Expected: "WorldEdit not found. Please install WorldEdit to use this feature."
```

### ✅ Test 9: Kit System (Already existed, verify)
```
# Set up inventory kit
/invset test1

# Join queue
/queue test1

# After match starts
# Expected: Your inventory matches what you set with /invset
```

## Edge Case Testing

### Edge Case 1: Invalid Player
```
/d NonexistentPlayer test1
# Expected: "Player not found!"
```

### Edge Case 2: Invalid Gamemode
```
/d PlayerB invalid_gamemode
# Expected: "Gamemode not found!"
```

### Edge Case 3: No Maps in Gamemode
```
# Create gamemode but don't add maps
/gamemode create empty
/d PlayerB empty
# Expected: "No maps available for this gamemode!"
```

### Edge Case 4: Invalid Rounds
```
/setrounds test1 0
# Expected: Error (rounds must be 1+)

/setrounds test1 -5
# Expected: Error
```

### Edge Case 5: Player Already in Match
```
# Player A in queue, gets matched with Player B
# While fighting, another player tries:
/d PlayerA test1
# Expected: "PlayerA is already in a match!"
```

### Edge Case 6: Duel During Queue
```
# Player A waiting in queue
/d PlayerA test1
# Expected: "PlayerA is already in a queue!"
```

## Performance Testing

- Join 10+ players to queue simultaneously
- **Expected:** Matches created, ELO calculations correct, no lag
- Check server logs for errors

## Debugging

### If Duels Don't Start
1. Check console for errors (might be in server logs)
2. Verify both players have enough permissions
3. Check if gamemode exists: `/gamemode list`
4. Check if maps are set: Look in `maps.yml`

### If ELO Wrong After Duel
- This should NOT happen (duels don't affect ELO)
- If it does, check QueueManager.endDuel() method

### If Rank-up Not Showing
- Check that players are in match (not duel)
- Duels don't trigger rank-ups (by design - practice only)
- Run matches from queue to trigger rank-ups

### If Rounds Not Persisting
- After `/setrounds test1 3`, restart server
- Check `gamemodes.yml` for `rounds: 3` entry
- If not there, GamemodeManager.loadGamemodes() may not be reading it

## Success Criteria

Plugin is ready for use when:
- ✅ Basic queue and matching works
- ✅ ELO calculated correctly
- ✅ Rank-up notifications appear
- ✅ Duels work without affecting ELO
- ✅ Multi-round duels work correctly
- ✅ /setrounds persists config
- ✅ WorldEdit optional (works or graceful error)
- ✅ No errors in console
- ✅ Data persists after server restart
