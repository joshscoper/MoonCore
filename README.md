# 🌙 MoonCore

MoonCore is the central data handler and API for the Moonfall Minecraft server network. It provides real-time, cross-server persistent player data, database syncing with caching fallback, and a powerful runtime API for integration with other plugins.

## 🚀 Features

- ✅ Centralized player data storage (level, XP, inventory, titles, etc.)
- 🔄 Async database syncing with smart cache fallback
- 📦 Inventory serialization using NBT-API
- 💬 Advanced messaging system with MiniMessage + legacy color support
- 🧠 Designed to support future systems (towny, punishments, toggles, social, etc.)
- 🧩 API integration for other plugins in the network
- 🛠️ Command and debug tools

## 🧪 Commands

```
/mooncore reload      → Reloads plugin config
/mooncore flush       → Forces data save for all players
/mooncore debug <p>   → Prints debug info for a player
/mooncore dbstatus    → Shows database connection state
```

## 📦 Dependency Setup (Maven)

Add this to your plugin's `pom.xml` if you want to use MoonCore as a dependency:

```xml
<repositories>
    <repository>
        <id>mooncore-repo</id>
        <url>https://raw.githubusercontent.com/joshscoper/maven-repo/main</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>net.moonfall</groupId>
        <artifactId>MoonCore</artifactId>
        <version>1.0</version>
    </dependency>
</dependencies>
```

> ℹ️ Make sure you’ve added the MoonCore JAR to your GitHub Pages repo or custom Maven host.

## 🧠 Integration Example

```java
PlayerData data = MoonCoreAPI.getPlayerData(player);
int level = data.getLevel();
data.setBalance(1500.0);
MoonCoreAPI.savePlayerDataAsync(data);
```

## 🗃️ Config Example

```yaml
database:
  host: localhost
  port: 3306
  name: mooncore
  username: root
  password: secret
  pool-size: 10
  useSSL: false
```

## 🧱 Roadmap

- [x] PlayerData & Inventory Sync
- [x] SQL Schema Bootstrapper
- [x] Database Connection Watcher + Cache Sync
- [x] Command API
- [ ] Offline Data Fetching

## 🧑‍💻 Author

**JoshScoper**  
📦 [github.com/joshscoper](https://github.com/joshscoper)

---

Licensed under MIT.
