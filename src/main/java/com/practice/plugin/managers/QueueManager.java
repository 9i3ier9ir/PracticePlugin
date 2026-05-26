package com.practice.plugin.managers;

import com.practice.plugin.models.PlayerStats;
import com.practice.plugin.models.GameMap;
import com.practice.plugin.models.Match;
import com.practice.plugin.models.Duel;
import com.practice.plugin.utils.EloCalculator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

/**
 * Manages player queues and matchmaking
 */
public class QueueManager {
    private JavaPlugin plugin;
    private GamemodeManager gamemodeManager;
    private EloManager eloManager;
    private Map<String, LinkedList<Player>> queues; // gamemode -> queue
    private Map<String, Match> activeMatches; // player name -> match
    private Map<String, Duel> activeDuels; // player name -> duel
    private int matchmakingWaitTime;
    private BukkitTask matchmakingTask;

    public QueueManager(JavaPlugin plugin, GamemodeManager gamemodeManager, EloManager eloManager) {
        this.plugin = plugin;
        this.gamemodeManager = gamemodeManager;
        this.eloManager = eloManager;
        this.queues = new HashMap<>();
        this.activeMatches = new HashMap<>();
        this.activeDuels = new HashMap<>();
        this.matchmakingWaitTime = plugin.getConfig().getInt("elo.matchmaking-wait-time", 60) * 1000;
        startMatchmakingTask();
    }

    public void joinQueue(Player player, String gamemodeName) {
        if (!gamemodeManager.gamemodeExists(gamemodeName)) {
            player.sendMessage("§cGamemode does not exist!");
            return;
        }

        LinkedList<Player> queue = queues.computeIfAbsent(gamemodeName, k -> new LinkedList<>());
        
        if (queue.contains(player)) {
            player.sendMessage("§cYou are already in queue!");
            return;
        }

        queue.add(player);
        PlayerStats stats = eloManager.getOrCreatePlayerStats(player.getName(), gamemodeName);
        stats.setQueueJoinTime(System.currentTimeMillis());
        player.sendMessage("§aJoined queue for §b" + gamemodeName + "§a! (ELO: " + stats.getElo() + ")");
    }

    public void leaveQueue(Player player) {
        for (LinkedList<Player> queue : queues.values()) {
            queue.remove(player);
        }
        player.sendMessage("§aLeft queue!");
    }

    public boolean isInQueue(Player player) {
        for (LinkedList<Player> queue : queues.values()) {
            if (queue.contains(player)) {
                return true;
            }
        }
        return false;
    }

    public void startMatch(Player player1, Player player2, String gamemodeName, GameMap map) {
        // Teleport players to spawn locations
        player1.teleport(map.getFirstSpawn());
        player2.teleport(map.getSecondSpawn());

        // Apply blindness effect
        int duration = plugin.getConfig().getInt("match.blindness-duration", 3) * 20; // Convert to ticks
        int amplifier = plugin.getConfig().getInt("match.blindness-amplifier", 2) - 1;
        PotionEffect blindness = new PotionEffect(PotionEffectType.BLINDNESS, duration, amplifier);
        player1.addPotionEffect(blindness);
        player2.addPotionEffect(blindness);

        // Send title message
        player1.sendTitle("§6MATCH FOUND", "§eGood luck!", 10, 70, 20);
        player2.sendTitle("§6MATCH FOUND", "§eGood luck!", 10, 70, 20);

        // Set inventory kits (TODO: implement kit loading)
        
        // Create match record
        Match match = new Match(player1, player2, gamemodeName, map);
        activeMatches.put(player1.getName(), match);
        activeMatches.put(player2.getName(), match);
    }

    public Match getActiveMatch(Player player) {
        return activeMatches.get(player.getName());
    }

    public void endMatch(Player winner, Player loser) {
        Match match = activeMatches.get(winner.getName());
        if (match == null) {
            return;
        }

        String gamemodeName = match.getGamemodeName();
        
        // Calculate ELO changes
        int winnerElo = eloManager.getPlayerElo(winner.getName(), gamemodeName);
        int loserElo = eloManager.getPlayerElo(loser.getName(), gamemodeName);
        
        // Store old ranks before ELO change
        String winnerOldRank = EloCalculator.getRankName(winnerElo);
        int winnerOldTier = EloCalculator.getRankTier(winnerElo);
        String loserOldRank = EloCalculator.getRankName(loserElo);
        int loserOldTier = EloCalculator.getRankTier(loserElo);
        
        int eloChange = EloCalculator.calculateEloChange(winnerElo, loserElo, true);
        int loserEloChange = EloCalculator.calculateEloChange(loserElo, winnerElo, false);
        
        // Update ELO
        eloManager.addPlayerElo(winner.getName(), gamemodeName, eloChange);
        eloManager.addPlayerElo(loser.getName(), gamemodeName, loserEloChange);
        
        // Record stats
        eloManager.recordWin(winner.getName(), gamemodeName);
        eloManager.recordLoss(loser.getName(), gamemodeName);
        
        // Get new ranks after ELO change
        int winnerNewElo = eloManager.getPlayerElo(winner.getName(), gamemodeName);
        int loserNewElo = eloManager.getPlayerElo(loser.getName(), gamemodeName);
        String winnerNewRank = EloCalculator.getRankName(winnerNewElo);
        int winnerNewTier = EloCalculator.getRankTier(winnerNewElo);
        String loserNewRank = EloCalculator.getRankName(loserNewElo);
        int loserNewTier = EloCalculator.getRankTier(loserNewElo);
        
        // Send winner message
        winner.sendMessage("§a§l━━━ VICTORY ━━━");
        winner.sendMessage("§a+" + eloChange + " ELO");
        winner.sendMessage("§fNow: " + EloCalculator.formatRank(winnerNewElo));
        
        // Check if winner ranked up using RankupInfo
        EloCalculator.RankupInfo winnerRankup = EloCalculator.checkRankup(winnerElo, winnerNewElo);
        if (winnerRankup != null) {
            winner.sendTitle("§6§lRANK UP!", winnerRankup.getFormattedMessage(), 10, 100, 20);
            winner.sendMessage(winnerRankup.getFormattedMessage());
        }
        
        winner.sendMessage("§a§l━━━━━━━━━━━━━");
        
        // Send loser message
        loser.sendMessage("§c§l━━━ DEFEAT ━━━");
        loser.sendMessage("§c" + loserEloChange + " ELO");
        loser.sendMessage("§fNow: " + EloCalculator.formatRank(loserNewElo));
        
        // Check if loser ranked up (rare but possible with +ELO from loss)
        EloCalculator.RankupInfo loserRankup = EloCalculator.checkRankup(loserElo, loserNewElo);
        if (loserRankup != null) {
            loser.sendMessage(loserRankup.getFormattedMessage());
        }
        
        loser.sendMessage("§c§l━━━━━━━━━━━━━");
        
        // Remove from active matches
        activeMatches.remove(winner.getName());
        activeMatches.remove(loser.getName());
    }

    public boolean isInMatch(Player player) {
        return activeMatches.containsKey(player.getName());
    }

    // ===== DUEL SYSTEM =====

    public void startDuel(Player player1, Player player2, String gamemodeName, int roundsNeeded) {
        List<GameMap> availableMaps = gamemodeManager.getAvailableMaps(gamemodeName);
        if (availableMaps.isEmpty()) {
            player1.sendMessage("§cNo maps available!");
            player2.sendMessage("§cNo maps available!");
            return;
        }

        GameMap map = availableMaps.get(0);
        Duel duel = new Duel(player1, player2, gamemodeName, map, roundsNeeded);

        // Teleport players to spawn locations
        player1.teleport(map.getFirstSpawn());
        player2.teleport(map.getSecondSpawn());

        // Apply blindness effect
        int duration = plugin.getConfig().getInt("match.blindness-duration", 3) * 20;
        int amplifier = plugin.getConfig().getInt("match.blindness-amplifier", 2) - 1;
        PotionEffect blindness = new PotionEffect(PotionEffectType.BLINDNESS, duration, amplifier);
        player1.addPotionEffect(blindness);
        player2.addPotionEffect(blindness);

        // Send title message
        player1.sendTitle("§d§lDUEL STARTED", "§fFirst to " + roundsNeeded, 10, 70, 20);
        player2.sendTitle("§d§lDUEL STARTED", "§fFirst to " + roundsNeeded, 10, 70, 20);

        // Track duel
        activeDuels.put(player1.getName(), duel);
        activeDuels.put(player2.getName(), duel);

        player1.sendMessage("§d§l━━━ DUEL STARTED ━━━");
        player1.sendMessage("§fGamemode: " + gamemodeName);
        player1.sendMessage("§fRounds: First to " + roundsNeeded);
        player1.sendMessage("§d§l━━━━━━━━━━━━━");

        player2.sendMessage("§d§l━━━ DUEL STARTED ━━━");
        player2.sendMessage("§fGamemode: " + gamemodeName);
        player2.sendMessage("§fRounds: First to " + roundsNeeded);
        player2.sendMessage("§d§l━━━━━━━━━━━━━");
    }

    public Duel getActiveDuel(Player player) {
        return activeDuels.get(player.getName());
    }

    public boolean isInDuel(Player player) {
        return activeDuels.containsKey(player.getName());
    }

    public void endDuelRound(Player roundWinner, Player roundLoser) {
        Duel duel = activeDuels.get(roundWinner.getName());
        if (duel == null) {
            return;
        }

        // Increment round wins
        if (roundWinner.equals(duel.getPlayer1())) {
            duel.player1WinsRound();
        } else {
            duel.player2WinsRound();
        }

        roundWinner.sendMessage("§a✓ Round Won! (" + (duel.getPlayer1().equals(roundWinner) ? duel.getPlayer1Wins() : duel.getPlayer2Wins()) + "/" + duel.getRoundsNeeded() + ")");
        roundLoser.sendMessage("§c✗ Round Lost! (" + (duel.getPlayer1().equals(roundLoser) ? duel.getPlayer1Wins() : duel.getPlayer2Wins()) + "/" + duel.getRoundsNeeded() + ")");

        // Check if duel is over
        if (duel.isDuelOver()) {
            endDuel(duel.getWinner(), duel.getLoser(), duel.getGamemodeName());
        } else {
            // Restart round - teleport to spawns again
            roundWinner.teleport(duel.getMap().getFirstSpawn());
            roundLoser.teleport(duel.getMap().getSecondSpawn());

            // Apply blindness again for next round
            int duration = plugin.getConfig().getInt("match.blindness-duration", 3) * 20;
            int amplifier = plugin.getConfig().getInt("match.blindness-amplifier", 2) - 1;
            PotionEffect blindness = new PotionEffect(PotionEffectType.BLINDNESS, duration, amplifier);
            roundWinner.addPotionEffect(blindness);
            roundLoser.addPotionEffect(blindness);
        }
    }

    public void endDuel(Player winner, Player loser, String gamemodeName) {
        // Note: Duels do NOT affect ELO
        winner.sendMessage("§d§l━━━ DUEL WON ━━━");
        winner.sendMessage("§dYou won the duel! (No ELO change)");
        winner.sendMessage("§d§l━━━━━━━━━━━━━");

        loser.sendMessage("§8§l━━━ DUEL LOST ━━━");
        loser.sendMessage("§8Better luck next time! (No ELO change)");
        loser.sendMessage("§8§l━━━━━━━━━━━━━");

        // Remove from active duels
        activeDuels.remove(winner.getName());
        activeDuels.remove(loser.getName());
    }

    private void startMatchmakingTask() {
        matchmakingTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (String gamemodeName : queues.keySet()) {
                processMatchmaking(gamemodeName);
            }
        }, 0L, 20L); // Run every 1 second
    }

    private void processMatchmaking(String gamemodeName) {
        LinkedList<Player> queue = queues.get(gamemodeName);
        if (queue == null || queue.size() < 2) {
            return;
        }

        List<GameMap> availableMaps = gamemodeManager.getAvailableMaps(gamemodeName);
        if (availableMaps.isEmpty()) {
            return;
        }

        // Try to find ELO-matched opponents
        Player player1 = null;
        Player player2 = null;
        
        int eloRange = plugin.getConfig().getInt("queue.elo-range", 200);
        long currentTime = System.currentTimeMillis();

        // First pass: strict ELO matching within 60 seconds
        for (int i = 0; i < queue.size(); i++) {
            Player p1 = queue.get(i);
            PlayerStats stats1 = eloManager.getOrCreatePlayerStats(p1.getName(), gamemodeName);
            
            if (currentTime - stats1.getQueueJoinTime() < matchmakingWaitTime) {
                continue; // Skip if not waited long enough
            }

            for (int j = i + 1; j < queue.size(); j++) {
                Player p2 = queue.get(j);
                PlayerStats stats2 = eloManager.getOrCreatePlayerStats(p2.getName(), gamemodeName);
                
                if (currentTime - stats2.getQueueJoinTime() < matchmakingWaitTime) {
                    continue;
                }

                // Check if ELO is within range
                if (Math.abs(stats1.getElo() - stats2.getElo()) <= eloRange) {
                    player1 = p1;
                    player2 = p2;
                    break;
                }
            }
            if (player1 != null) break;
        }

        // Second pass: match anyone waiting 60+ seconds
        if (player1 == null && queue.size() >= 2) {
            for (int i = 0; i < queue.size(); i++) {
                Player p1 = queue.get(i);
                PlayerStats stats1 = eloManager.getOrCreatePlayerStats(p1.getName(), gamemodeName);
                
                if (currentTime - stats1.getQueueJoinTime() >= matchmakingWaitTime) {
                    for (int j = i + 1; j < queue.size(); j++) {
                        Player p2 = queue.get(j);
                        PlayerStats stats2 = eloManager.getOrCreatePlayerStats(p2.getName(), gamemodeName);
                        
                        if (currentTime - stats2.getQueueJoinTime() >= matchmakingWaitTime) {
                            player1 = p1;
                            player2 = p2;
                            break;
                        }
                    }
                    if (player1 != null) break;
                }
            }
        }

        // Start match if found
        if (player1 != null && player2 != null) {
            queue.remove(player1);
            queue.remove(player2);
            
            GameMap map = availableMaps.get(0); // Use first available map
            startMatch(player1, player2, gamemodeName, map);
        }
    }

    public void stopMatchmakingTask() {
        if (matchmakingTask != null) {
            matchmakingTask.cancel();
        }
    }
}
