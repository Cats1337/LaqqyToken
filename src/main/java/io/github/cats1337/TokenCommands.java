package io.github.cats1337;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TokenCommands implements CommandExecutor {
    private final TokenData tokenData;

    public TokenCommands(TokenMain plugin) {
        this.tokenData = plugin.getTokenData();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("getlb")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                UUID uuid = player.getUniqueId();
                int tokens = tokenData.getTokens(uuid);
                if (tokens >= 1) {
                    tokenData.setTokens(uuid, tokens - 1);
                    tokenData.save();
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lb give " + player.getName() + " random");
                    player.sendMessage(ChatColor.GREEN + "You have redeemed a Lucky Block!");
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have enough tokens to redeem a Lucky Block.");
                }
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "This command can only be run by a player.");
                return true;
            }
        }
        
        if (command.getName().equalsIgnoreCase("checktoken")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                UUID uuid = player.getUniqueId();
                int tokens = tokenData.getTokens(uuid);

                player.sendMessage(ChatColor.GREEN + "You have " + tokens + " tokens.");
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "This command can only be run by a player.");
                return true;
            }
        }

        if (command.getName().equalsIgnoreCase("token")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Usage: /token <player> <amount>");
                sender.sendMessage(ChatColor.RED + "Usage: /checktoken");
                sender.sendMessage(ChatColor.RED + "Usage: /getlb");
                return true;
            }

            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /token <player> <amount>");
                return true;
            }

            if (!sender.hasPermission("laqqytoken.token")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }

            int tokens;

            try {
                tokens = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid token amount.");
                return true;
            }

            if (tokens < 1) {
                sender.sendMessage(ChatColor.RED + "Token amount must be greater than zero.");
                return true;
            }

            UUID uuid = target.getUniqueId();

            tokenData.setTokens(uuid, tokenData.getTokens(uuid) + tokens);
            tokenData.save();

            sender.sendMessage(ChatColor.GREEN + "Gave " + target.getName() + " " + tokens + " tokens.");
            target.sendMessage(ChatColor.GREEN + "You have received " + tokens + " tokens.");
            return true;
        }

        return true;
    }
}