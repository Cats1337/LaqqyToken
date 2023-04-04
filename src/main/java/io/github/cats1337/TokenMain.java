package io.github.cats1337;

import java.util.logging.Logger;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class TokenMain extends JavaPlugin implements Listener {

    static final Logger LOGGER = Logger.getLogger("LaqqyToken");
    private TokenData tokenData;

    @Override
    public void onEnable() {
        LOGGER.info("LaqqyToken is enabled!");
        
        tokenData = new TokenData(this);
        tokenData.load();
        
        getServer().getPluginManager().registerEvents(this, this);
        
        getCommand("token").setExecutor(new TokenCommands(this));
        getCommand("checktoken").setExecutor(new TokenCommands(this));
        getCommand("getlb").setExecutor(new TokenCommands(this));

    }

    @Override
    public void onDisable() {
        LOGGER.info("LaqqyToken is disabled!");
        tokenData.save();
    }

    public TokenData getTokenData() {
        return tokenData;
    }

}
