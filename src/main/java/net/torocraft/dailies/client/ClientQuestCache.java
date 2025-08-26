package net.torocraft.dailies.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.torocraft.dailies.capabilities.DailiesCapabilityImpl;
import net.torocraft.dailies.capabilities.DailiesCapabilityProvider;
import net.torocraft.dailies.capabilities.IDailiesCapability;
import net.torocraft.dailies.network.PacketHandler;
import net.torocraft.dailies.network.packets.GetQuestsPacket.QuestsFilter;
import net.torocraft.dailies.quests.DailyQuest;

import java.util.HashSet;
import java.util.Set;

/**
 * Client-side quest cache that properly manages quest data from the server.
 * Replaces the static quest fields in DailiesMod with a capability-based approach.
 */
public class ClientQuestCache {
    
    private static final ClientQuestCache INSTANCE = new ClientQuestCache();
    
    private Set<DailyQuest> cachedAvailableQuests = new HashSet<>();
    private Set<DailyQuest> cachedAcceptedQuests = new HashSet<>();
    private long lastSyncTime = 0;
    private static final long CACHE_VALIDITY_MS = 30000; // 30 seconds
    
    private ClientQuestCache() {}
    
    public static ClientQuestCache getInstance() {
        return INSTANCE;
    }
    
    /**
     * Get available quests, requesting from server if cache is invalid
     */
    public Set<DailyQuest> getAvailableQuests() {
        return getAvailableQuests(true);
    }
    
    /**
     * Get available quests with option to auto-refresh
     */
    public Set<DailyQuest> getAvailableQuests(boolean autoRefresh) {
        // Always try to get from player capability first for most up-to-date data
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            IDailiesCapability capability = player.getCapability(DailiesCapabilityProvider.DAILIES_CAPABILITY, null).orElse(new DailiesCapabilityImpl());
            if (capability != null) {
                Set<DailyQuest> capabilityQuests = capability.getAvailableQuests();
                if (capabilityQuests != null && !capabilityQuests.isEmpty()) {
                    cachedAvailableQuests = new HashSet<>(capabilityQuests);
                    return new HashSet<>(cachedAvailableQuests);
                }
            }
        }
        
        if (autoRefresh && shouldRefreshCache()) {
            requestQuestsFromServer();
        }
        return new HashSet<>(cachedAvailableQuests);
    }
    
    /**
     * Get accepted quests, requesting from server if cache is invalid
     */
    public Set<DailyQuest> getAcceptedQuests() {
        return getAcceptedQuests(true);
    }
    
    /**
     * Get accepted quests with option to auto-refresh
     */
    public Set<DailyQuest> getAcceptedQuests(boolean autoRefresh) {
        if (autoRefresh && shouldRefreshCache()) {
            requestQuestsFromServer();
        }
        
        // Try to get from player capability first
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            IDailiesCapability capability = player.getCapability(DailiesCapabilityProvider.DAILIES_CAPABILITY, null).orElse(new DailiesCapabilityImpl());
            if (capability != null) {
                Set<DailyQuest> capabilityQuests = capability.getAcceptedQuests();
                if (capabilityQuests != null && !capabilityQuests.isEmpty()) {
                    cachedAcceptedQuests = new HashSet<>(capabilityQuests);
                    return new HashSet<>(cachedAcceptedQuests);
                }
            }
        }
        
        return new HashSet<>(cachedAcceptedQuests);
    }
    
    /**
     * Update the cache with new quest data from server
     */
    public void updateAvailableQuests(Set<DailyQuest> quests) {
        if (quests != null) {
            this.cachedAvailableQuests = new HashSet<>(quests);
            this.lastSyncTime = System.currentTimeMillis();
        }
    }
    
    /**
     * Update the cache with new accepted quest data from server
     */
    public void updateAcceptedQuests(Set<DailyQuest> quests) {
        if (quests != null) {
            this.cachedAcceptedQuests = new HashSet<>(quests);
            this.lastSyncTime = System.currentTimeMillis();
            
            // Also update the player capability if available
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                IDailiesCapability capability = player.getCapability(DailiesCapabilityProvider.DAILIES_CAPABILITY, null).orElse(new DailiesCapabilityImpl());
                if (capability != null) {
                    capability.setAcceptedQuests(new HashSet<>(quests));
                }
            }
        }
    }
    
    /**
     * Check if we should refresh the cache
     */
    private boolean shouldRefreshCache() {
        return System.currentTimeMillis() - lastSyncTime > CACHE_VALIDITY_MS;
    }
    
    /**
     * Request fresh quest data from the server
     */
    private void requestQuestsFromServer() {
        PacketHandler.getQuests(QuestsFilter.AVAILABLE);
        PacketHandler.getQuests(QuestsFilter.ACCEPTED);
    }
    
    /**
     * Clear the cache (useful when disconnecting from server)
     */
    public void clearCache() {
        cachedAvailableQuests.clear();
        cachedAcceptedQuests.clear();
        lastSyncTime = 0;
    }
    
    /**
     * Force refresh from server
     */
    public void forceRefresh() {
        clearCache();
        requestQuestsFromServer();
    }
    
    /**
     * Check if cache has valid data
     */
    public boolean hasValidCache() {
        return !shouldRefreshCache() && (!cachedAvailableQuests.isEmpty() || !cachedAcceptedQuests.isEmpty());
    }
    
    /**
     * Clear cache when disconnecting from server (call from disconnect event)
     */
    public static void onClientDisconnect() {
        getInstance().clearCache();
    }
}
