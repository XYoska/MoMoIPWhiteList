package com.momoiptools;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;

public class IPWhitelistCommand implements CommandExecutor, TabCompleter {

    private final MoMoIPWhitelist plugin;

    public IPWhitelistCommand(MoMoIPWhitelist plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("momoipwhitelist.admin")) {
            sender.sendMessage("You don't have permission to use this command!");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("Usage: /" + label + " <add|remove|list|reload>");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "add":
                if (args.length < 3) {
                    sender.sendMessage("Usage: /" + label + " add <player> <ip>");
                    return true;
                }
                addIP(sender, args[1], args[2]);
                break;
            case "remove":
                if (args.length < 3) {
                    sender.sendMessage("Usage: /" + label + " remove <player> <ip>");
                    return true;
                }
                removeIP(sender, args[1], args[2]);
                break;
            case "list":
                if (args.length < 2) {
                    // 显示所有在白名单中的玩家
                    if (plugin.getIPWhitelist().isEmpty()) {
                        sender.sendMessage("No players in IP whitelist.");
                    } else {
                        sender.sendMessage("Players in IP whitelist:");
                        for (String player : plugin.getIPWhitelist().keySet()) {
                            // 使用TextComponent创建可点击的命令
                            TextComponent message = new TextComponent("- " + player);
                            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " list " + player));
                            if (sender instanceof Player) {
                                ((Player) sender).spigot().sendMessage(message);
                            } else {
                                sender.sendMessage("- " + player);
                            }
                        }
                    }
                    return true;
                }
                listIPs(sender, args[1]);
                break;
            case "reload":
                plugin.reloadConfig();
                sender.sendMessage("IP whitelist reloaded!");
                break;
            default:
                sender.sendMessage("Usage: /" + label + " <add|remove|list|reload>");
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("momoipwhitelist.admin")) {
            return completions;
        }

        if (args.length == 1) {
            // 补全子命令
            completions.add("add");
            completions.add("remove");
            completions.add("list");
            completions.add("reload");
        } else if (args.length == 2) {
            // 补全玩家名
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("add") || subCommand.equals("remove") || subCommand.equals("list")) {
                completions.addAll(plugin.getIPWhitelist().keySet());
            }
        } else if (args.length == 3) {
            // 补全IP地址
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("add") || subCommand.equals("remove")) {
                String player = args[1];
                if (plugin.getIPWhitelist().containsKey(player)) {
                    completions.addAll(plugin.getIPWhitelist().get(player));
                }
            }
        }

        return completions;
    }

    private void addIP(CommandSender sender, String player, String ip) {
        List<String> ips = plugin.getIPWhitelist().getOrDefault(player, new java.util.ArrayList<>());
        if (!ips.contains(ip)) {
            ips.add(ip);
            plugin.getIPWhitelist().put(player, ips);
            plugin.saveIPWhitelist();
            sender.sendMessage("Added IP " + ip + " to player " + player);
        } else {
            sender.sendMessage("IP " + ip + " is already in the whitelist for player " + player);
        }
    }

    private void removeIP(CommandSender sender, String player, String ip) {
        List<String> ips = plugin.getIPWhitelist().get(player);
        if (ips != null && ips.remove(ip)) {
            if (ips.isEmpty()) {
                plugin.getIPWhitelist().remove(player);
            }
            plugin.saveIPWhitelist();
            sender.sendMessage("Removed IP " + ip + " from player " + player);
        } else {
            sender.sendMessage("IP " + ip + " is not in the whitelist for player " + player);
        }
    }

    private void listIPs(CommandSender sender, String player) {
        List<String> ips = plugin.getIPWhitelist().get(player);
        if (ips != null && !ips.isEmpty()) {
            sender.sendMessage("IPs for player " + player + ":");
            for (String ip : ips) {
                sender.sendMessage("- " + ip);
            }
        } else {
            sender.sendMessage("No IPs found for player " + player);
        }
    }
}
