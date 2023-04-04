package io.github.cats1337;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class TokenData {
    private final TokenMain plugin;
    private final FileConfiguration config;
    private final File file;

    private final Map<UUID, Integer> tokens;

    public TokenData(TokenMain plugin) {
        this.plugin = plugin;
        this.tokens = new HashMap<>();

        // Create the 'TokenData' folder if it doesn't exist
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        // Load the configuration file
        this.file = new File(dataFolder, "tokens.yml");
        this.config = YamlConfiguration.loadConfiguration(file);

        load();
    }

    public void load() {
        if (!file.exists()) {
            return;
        }

        for (String uuidString : config.getKeys(false)) {
            UUID uniqueId = UUID.fromString(uuidString);
            int numTokens = config.getInt(uuidString);
            tokens.put(uniqueId, numTokens);
        }
    }

    public void save() {
        for (Map.Entry<UUID, Integer> entry : tokens.entrySet()) {
            config.set(entry.getKey().toString(), entry.getValue());
        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save tokens.yml");
            e.printStackTrace();
        }
    }

    public int getTokens(UUID uuid) {
        return tokens.getOrDefault(uuid, 0);
    }

    public void setTokens(UUID uuid, int numTokens) {
        tokens.put(uuid, numTokens);
    }
    
}
