package me.danplayz.servercommand.method;

import java.io.IOException;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import java.nio.file.Files;

import me.danplayz.servercommand.Core;

import java.nio.file.CopyOption;
import java.io.File;
import net.md_5.bungee.config.Configuration;

public class ConfigManager
{
    private Core plugin;
    private String path;
    private String file;
    private Configuration config;
    
    public ConfigManager(final Core plugin, final String path, final String file) {
        this.plugin = plugin;
        if (path.isEmpty()) {
            this.path = plugin.getDataFolder().toString();
        }
        else {
            this.path = path;
        }
        if (file.contains(".yml")) {
            this.file = file;
        }
        else {
            this.file = String.valueOf(file) + ".yml";
        }
    }
    
    public void setupConfig() {
        final File file = new File(this.path, this.file);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }
        try {
            if (!file.exists()) {
                Files.copy(this.plugin.getResourceAsStream(this.file), file.toPath(), new CopyOption[0]);
            }
            this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to copy configuration", e);
        }
    }
    
    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.getConfig(), new File(this.path, this.file));
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to save configuration", e);
        }
    }
    
    public Configuration getConfig() {
        return this.config;
    }
}