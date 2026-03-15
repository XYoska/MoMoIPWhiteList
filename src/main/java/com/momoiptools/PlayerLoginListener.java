package com.momoiptools;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

public class PlayerLoginListener implements Listener {

    private final MoMoIPWhitelist plugin;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public PlayerLoginListener(MoMoIPWhitelist plugin) {
        this.plugin = plugin;
        // 确保日志目录存在
        File logDir = new File(plugin.getDataFolder(), "logs");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        String playerName = event.getPlayer().getName();
        String playerIP = event.getAddress().getHostAddress();
        String currentTime = dateFormat.format(new Date());

        // 记录登录日志到控制台
        plugin.getLogger().log(Level.INFO, "[{0}] Player {1} attempting to login from IP: {2}", new Object[]{currentTime, playerName, playerIP});

        // 检查玩家是否在白名单中
        if (plugin.getIPWhitelist().containsKey(playerName)) {
            List<String> allowedIPs = plugin.getIPWhitelist().get(playerName);
            if (!allowedIPs.contains(playerIP)) {
                event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                event.setKickMessage("You are not allowed to login from this IP address!");
                
                // 记录被拒绝的登录尝试
                String denialMessage = "[" + currentTime + "] Player " + playerName + " was denied login from IP: " + playerIP + " (not in whitelist)";
                plugin.getLogger().log(Level.WARNING, denialMessage);
                
                // 记录到单独的日志文件
                logToFile(denialMessage);
                
                // 通知管理员（根据配置）
                if (plugin.isNotificationsEnabled() && plugin.isNotifyOnDenial()) {
                    notifyAdmins(ChatColor.RED + "[MoMoIPWhitelist] " + ChatColor.WHITE + "Player " + ChatColor.YELLOW + playerName + ChatColor.WHITE + " was denied login from IP: " + ChatColor.RED + playerIP + ChatColor.WHITE + " (not in whitelist)");
                }
            } else {
                // 记录成功的登录
                String successMessage = "[" + currentTime + "] Player " + playerName + " successfully logged in from IP: " + playerIP;
                plugin.getLogger().log(Level.INFO, successMessage);
                
                // 记录到单独的日志文件
                logToFile(successMessage);
                
                // 通知管理员（根据配置）
                if (plugin.isNotificationsEnabled() && plugin.isNotifyOnSuccess()) {
                    notifyAdmins(ChatColor.GREEN + "[MoMoIPWhitelist] " + ChatColor.WHITE + "Player " + ChatColor.YELLOW + playerName + ChatColor.WHITE + " successfully logged in from IP: " + ChatColor.GREEN + playerIP);
                }
            }
        } else {
            // 玩家不在白名单中，允许登录
            String message = "[" + currentTime + "] Player " + playerName + " logged in from IP: " + playerIP + " (not in whitelist)";
            plugin.getLogger().log(Level.INFO, message);
            
            // 记录到单独的日志文件
            logToFile(message);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // 玩家登录后记录世界和坐标
        String playerName = event.getPlayer().getName();
        Location location = event.getPlayer().getLocation();
        String world = location.getWorld().getName();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        String currentTime = dateFormat.format(new Date());
        
        String message = "[" + currentTime + "] Player " + playerName + " joined server in world: " + world + " at coordinates: (" + x + ", " + y + ", " + z + ")";
        plugin.getLogger().log(Level.INFO, message);
        
        // 记录到单独的日志文件
        logToFile(message);
    }

    private void notifyAdmins(String message) {
        // 通知所有在线的拥有通知权限的玩家
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.hasPermission("momoipwhitelist.notify")) {
                player.sendMessage(message);
            }
        });
        
        // 同时在控制台输出
        plugin.getLogger().log(Level.INFO, ChatColor.stripColor(message));
    }

    private void logToFile(String message) {
        try {
            // 使用固定的日志文件名
            File logFile = new File(plugin.getDataFolder(), "logs/login-log.txt");
            
            // 确保文件存在
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            
            // 写入日志
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                writer.write(message);
                writer.newLine();
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to write to log file", e);
        }
    }
}
