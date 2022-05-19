package me.danplayz.servercommand;

import me.danplayz.servercommand.commands.CommandServer;
import me.danplayz.servercommand.method.ConfigManager;
import net.md_5.bungee.api.plugin.Plugin;

public class Core extends Plugin
{
    public static ConfigManager config;
    
    public void onEnable() {
        (Core.config = new ConfigManager(this, "", "config.yml")).setupConfig();
        this.getProxy().getPluginManager().registerCommand(this, new CommandServer());
    }
}