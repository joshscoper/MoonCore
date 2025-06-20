package net.moonfall.mooncore.data;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PlayerData {

    private final UUID uuid;
    private String username;

    // Timestamps & identity
    private long firstLogin;
    private long lastLogin;
    private String lastIp;

    // Progression
    private int level;
    private int xp;
    private long playtimeTicks;
    private double balance;

    // Inventory
    private ItemStack[] inventory;
    private ItemStack[] enderChest;
    private ItemStack[] armor;

    // Locations
    private Location lastLocation;
    private Location homeLocation;

    // Punishments
    private boolean isMuted;
    private boolean isBanned;
    private boolean isShadowMuted;
    private long muteUntil;
    private long banUntil;
    private String banReason;
    private final List<String> punishmentLog = new ArrayList<>();

    // Tags and titles
    private final Set<String> tags = new HashSet<>();
    private final List<String> titles = new ArrayList<>();

    // Settlement information
    private String settlementName = "";
    private String settlementRank = "";

    // Social features
    private final Set<UUID> friends = new HashSet<>();
    private final Set<UUID> ignored = new HashSet<>();
    private UUID activePartyId;
    private UUID lastMessaged;
    private final List<String> inbox = new ArrayList<>();

    // Toggled settings
    private boolean pvpEnabled = true;
    private boolean acceptsMessages = true;
    private boolean showJoinLeaveMessages = true;
    private boolean allowTeleportRequests = true;
    private boolean showHud = true;

    // Name color cosmetics
    private String nameColor = "WHITE";
    private final Set<String> unlockedNameColors = new HashSet<>();

    // Metadata
    private final Map<String, Object> metadata = new HashMap<>();

    public PlayerData(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
        this.firstLogin = System.currentTimeMillis();
        this.lastLogin = System.currentTimeMillis();
    }

    // === Getters & Setters ===

    public UUID getUuid() { return uuid; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public long getFirstLogin() { return firstLogin; }
    public void setFirstLogin(long firstLogin) { this.firstLogin = firstLogin; }

    public long getLastLogin() { return lastLogin; }
    public void setLastLogin(long lastLogin) { this.lastLogin = lastLogin; }

    public String getLastIp() { return lastIp; }
    public void setLastIp(String lastIp) { this.lastIp = lastIp; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = xp; }

    public long getPlaytimeTicks() { return playtimeTicks; }
    public void setPlaytimeTicks(long playtimeTicks) { this.playtimeTicks = playtimeTicks; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public ItemStack[] getInventory() { return inventory; }
    public void setInventory(ItemStack[] inventory) { this.inventory = inventory; }

    public ItemStack[] getEnderChest() { return enderChest; }
    public void setEnderChest(ItemStack[] enderChest) { this.enderChest = enderChest; }

    public ItemStack[] getArmor() { return armor; }
    public void setArmor(ItemStack[] armor) { this.armor = armor; }

    public Location getLastLocation() { return lastLocation; }
    public void setLastLocation(Location lastLocation) { this.lastLocation = lastLocation; }

    public Location getHomeLocation() { return homeLocation; }
    public void setHomeLocation(Location homeLocation) { this.homeLocation = homeLocation; }

    public boolean isMuted() { return isMuted; }
    public void setMuted(boolean muted) { this.isMuted = muted; }

    public boolean isBanned() { return isBanned; }
    public void setBanned(boolean banned) { this.isBanned = banned; }

    public boolean isShadowMuted() { return isShadowMuted; }
    public void setShadowMuted(boolean shadowMuted) { this.isShadowMuted = shadowMuted; }

    public long getMuteUntil() { return muteUntil; }
    public void setMuteUntil(long muteUntil) { this.muteUntil = muteUntil; }

    public long getBanUntil() { return banUntil; }
    public void setBanUntil(long banUntil) { this.banUntil = banUntil; }

    public String getBanReason() { return banReason; }
    public void setBanReason(String banReason) { this.banReason = banReason; }

    public List<String> getPunishmentLog() { return punishmentLog; }
    public void addPunishmentLogEntry(String entry) { punishmentLog.add(entry); }
    public void clearPunishmentLog() { punishmentLog.clear(); }

    public Set<String> getTags() { return tags; }
    public void addTag(String tag) { tags.add(tag); }
    public void removeTag(String tag) { tags.remove(tag); }
    public boolean hasTag(String tag) { return tags.contains(tag); }

    public List<String> getTitles() { return titles; }
    public void addTitle(String title) {
        if (!titles.contains(title)) titles.add(title);
    }
    public void removeTitle(String title) { titles.remove(title); }
    public boolean hasTitle(String title) { return titles.contains(title); }

    public Set<UUID> getFriends() { return friends; }
    public void addFriend(UUID uuid) { friends.add(uuid); }
    public void removeFriend(UUID uuid) { friends.remove(uuid); }
    public boolean isFriend(UUID uuid) { return friends.contains(uuid); }

    public Set<UUID> getIgnored() { return ignored; }
    public void ignore(UUID uuid) { ignored.add(uuid); }
    public void unignore(UUID uuid) { ignored.remove(uuid); }
    public boolean isIgnoring(UUID uuid) { return ignored.contains(uuid); }

    public UUID getActivePartyId() { return activePartyId; }
    public void setActivePartyId(UUID activePartyId) { this.activePartyId = activePartyId; }

    public UUID getLastMessaged() { return lastMessaged; }
    public void setLastMessaged(UUID lastMessaged) { this.lastMessaged = lastMessaged; }

    public List<String> getInbox() { return inbox; }
    public void addMail(String message) { inbox.add(message); }
    public void clearInbox() { inbox.clear(); }

    public boolean isPvpEnabled() { return pvpEnabled; }
    public void setPvpEnabled(boolean pvpEnabled) { this.pvpEnabled = pvpEnabled; }

    public boolean acceptsMessages() { return acceptsMessages; }
    public void setAcceptsMessages(boolean acceptsMessages) { this.acceptsMessages = acceptsMessages; }

    public boolean isShowJoinLeaveMessages() { return showJoinLeaveMessages; }
    public void setShowJoinLeaveMessages(boolean showJoinLeaveMessages) { this.showJoinLeaveMessages = showJoinLeaveMessages; }

    public boolean isAllowTeleportRequests() { return allowTeleportRequests; }
    public void setAllowTeleportRequests(boolean allowTeleportRequests) { this.allowTeleportRequests = allowTeleportRequests; }

    public boolean isShowHud() { return showHud; }
    public void setShowHud(boolean showHud) { this.showHud = showHud; }

    public String getNameColor() { return nameColor; }
    public void setNameColor(String nameColor) { this.nameColor = nameColor; }

    public Set<String> getUnlockedNameColors() { return unlockedNameColors; }
    public void unlockNameColor(String colorCode) { unlockedNameColors.add(colorCode); }
    public boolean hasUnlockedColor(String colorCode) { return unlockedNameColors.contains(colorCode); }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(String key, Object value) { metadata.put(key, value); }
    public Object getMetadata(String key) { return metadata.get(key); }

    public String getSettlementName() {
        return settlementName;
    }

    public String getSettlementRank() {
        return settlementRank;
    }

    public void setSettlementName(String settlementName){
        this.settlementName = settlementName;
    }
    public void setSettlementRank(String settlementRank){
        this.settlementRank = settlementRank;
    }
    public boolean isAcceptsMessages() {
        return acceptsMessages;
    }
}
