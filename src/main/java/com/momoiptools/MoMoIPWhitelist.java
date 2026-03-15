package com.momoiptools;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bstats.bukkit.Metrics;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoMoIPWhitelist extends JavaPlugin implements Listener {

    private FileConfiguration config;
    private Map<String, List<String>> ipWhitelist;
    private Metrics metrics;

    @Override
    public void onEnable() {
        // 保存默认配置文件
        saveDefaultConfig();
        
        // 加载配置
        reloadConfig();
        
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new PlayerLoginListener(this), this);
        
        // 注册命令
        IPWhitelistCommand commandExecutor = new IPWhitelistCommand(this);
        getCommand("moipwl").setExecutor(commandExecutor);
        getCommand("moipwl").setTabCompleter(commandExecutor);
        // 注册完整命令名称
        getCommand("momoipwhitelist").setExecutor(commandExecutor);
        getCommand("momoipwhitelist").setTabCompleter(commandExecutor);
        
        // 初始化bstats
        try {
            metrics = new Metrics(this, 12345);
            getLogger().info("bstats metrics enabled!");
        } catch (Exception e) {
            getLogger().warning("Failed to enable bstats metrics: " + e.getMessage());
        }
        
        getLogger().info("MoMoIPWhitelist has been enabled!");
    }

    @Override
    public void onDisable() {
        if (metrics != null) {
            metrics.shutdown();
        }
        getLogger().info("MoMoIPWhitelist has been disabled!");
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        config = getConfig();
        loadIPWhitelist();
    }

    private void loadIPWhitelist() {
        ipWhitelist = new HashMap<>();
        if (config.contains("players")) {
            for (String player : config.getConfigurationSection("players").getKeys(false)) {
                List<String> ips = config.getStringList("players." + player);
                ipWhitelist.put(player, ips);
            }
        }
    }

    public Map<String, List<String>> getIPWhitelist() {
        return ipWhitelist;
    }

    public void saveIPWhitelist() {
        config.set("players", ipWhitelist);
        saveConfig();
    }

    public boolean isNotificationsEnabled() {
        return config.getBoolean("notifications.enabled", true);
    }

    public boolean isNotifyOnSuccess() {
        return config.getBoolean("notifications.notifyOnSuccess", true);
    }

    public boolean isNotifyOnDenial() {
        return config.getBoolean("notifications.notifyOnDenial", true);
    }
}
