package com.practice.plugin.utils;

/**
 * Calculates ELO changes based on match outcomes
 */
public class EloCalculator {
    private static final double K_FACTOR = 32.0; // Standard ELO K-factor

    /**
     * Calculate ELO change for a player based on match outcome
     * @param playerElo The player's current ELO
     * @param opponentElo The opponent's ELO
     * @param playerWon Whether the player won
     * @return The ELO change (positive or negative)
     */
    public static int calculateEloChange(int playerElo, int opponentElo, boolean playerWon) {
        double expectedScore = calculateExpectedScore(playerElo, opponentElo);
        double actualScore = playerWon ? 1.0 : 0.0;
        double eloChange = K_FACTOR * (actualScore - expectedScore);
        return (int) Math.round(eloChange);
    }

    /**
     * Calculate expected score based on ELO ratings
     * Uses standard ELO formula: EA = 1 / (1 + 10^((RB - RA) / 400))
     */
    private static double calculateExpectedScore(int playerElo, int opponentElo) {
        double eloDifference = opponentElo - playerElo;
        return 1.0 / (1.0 + Math.pow(10.0, eloDifference / 400.0));
    }

    /**
     * Get the rank name based on ELO
     */
    public static String getRankName(int elo) {
        if (elo < 300) return "Wood";
        if (elo < 600) return "Stone";
        if (elo < 900) return "Coal";
        if (elo < 1300) return "Copper";
        if (elo < 1700) return "Redstone";
        if (elo < 2100) return "Gold";
        if (elo < 2400) return "Emerald";
        if (elo < 2800) return "Diamond";
        return "Netherite";
    }

    /**
     * Get the rank tier within a rank based on ELO
     */
    public static int getRankTier(int elo) {
        if (elo < 300) {
            return Math.min(3, Math.max(1, (elo - 100) / 67 + 1));
        } else if (elo < 600) {
            return Math.min(3, Math.max(1, (elo - 300) / 100 + 1));
        } else if (elo < 900) {
            return Math.min(2, Math.max(1, (elo - 600) / 150 + 1));
        } else if (elo < 1300) {
            return Math.min(3, Math.max(1, (elo - 900) / 133 + 1));
        } else if (elo < 1700) {
            return Math.min(3, Math.max(1, (elo - 1300) / 133 + 1));
        } else if (elo < 2100) {
            return Math.min(3, Math.max(1, (elo - 1700) / 133 + 1));
        } else if (elo < 2400) {
            return Math.min(2, Math.max(1, (elo - 2100) / 150 + 1));
        } else if (elo < 2800) {
            return Math.min(3, Math.max(1, (elo - 2400) / 133 + 1));
        }
        return 1; // Netherite
    }

    /**
     * Format ELO and rank for display
     */
    public static String formatRank(int elo) {
        String rank = getRankName(elo);
        int tier = getRankTier(elo);
        if (rank.equals("Netherite")) {
            return rank + " (" + elo + " ELO)";
        }
        return rank + " " + tier + " (" + elo + " ELO)";
    }

    /**
     * Check if player ranked up and return rank up info
     * Returns null if no rank up, otherwise returns formatted message
     */
    public static RankupInfo checkRankup(int oldElo, int newElo) {
        String oldRank = getRankName(oldElo);
        int oldTier = getRankTier(oldElo);
        
        String newRank = getRankName(newElo);
        int newTier = getRankTier(newElo);
        
        // Check if rank or tier changed
        if (!oldRank.equals(newRank) || oldTier != newTier) {
            return new RankupInfo(oldRank, oldTier, newRank, newTier);
        }
        
        return null;
    }

    /**
     * Helper class to store rankup information
     */
    public static class RankupInfo {
        public String oldRank;
        public int oldTier;
        public String newRank;
        public int newTier;

        public RankupInfo(String oldRank, int oldTier, String newRank, int newTier) {
            this.oldRank = oldRank;
            this.oldTier = oldTier;
            this.newRank = newRank;
            this.newTier = newTier;
        }

        public String getFormattedMessage() {
            if (newRank.equals("Netherite")) {
                return "§c§l🏆 PROMOTED TO NETHERITE! 🏆\n§f" + oldRank + " " + oldTier + " → " + newRank;
            }
            return "§a§l⬆ RANK UP!\n§f" + oldRank + " " + oldTier + " → " + newRank + " " + newTier;
        }
    }
}
