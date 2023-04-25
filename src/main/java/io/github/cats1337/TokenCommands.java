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
                    // check the players inventory to make sure they have an open slot
                    // if they do not have an open slot, send them a message saying they need to make room
                    // if they do have an open slot, remove a token and give them a lucky block
                    if (player.getInventory().firstEmpty() == -1) {
                        player.sendMessage(ChatColor.YELLOW + " " + ChatColor.RED + "You do not have enough room in your inventory to redeem a Lucky Block.");
                        return true;
                    }
                    tokenData.setTokens(uuid, tokens - 1);
                    tokenData.save();
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lb give " + player.getName() + " random");
                    player.sendMessage(ChatColor.YELLOW + " " + ChatColor.GREEN + "You have redeemed a Lucky Block!");
                } else {
                    player.sendMessage(ChatColor.YELLOW + " " + ChatColor.RED + "You do not have enough tokens to redeem a Lucky Block.");
                }
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Console doesn't have tokens or any inventory idiot.");
                return true;
            }
        }
        
        if (command.getName().equalsIgnoreCase("checktoken")) {
            if ((sender instanceof Player)  && args.length == 0){
                Player player = (Player) sender;
                UUID uuid = player.getUniqueId();
                int tokens = tokenData.getTokens(uuid);

                player.sendMessage(ChatColor.YELLOW + " " + ChatColor.GREEN + "You have " + tokens + " tokens.");
                return true;
            }
            if(sender.hasPermission("laqqytoken.token") || !(sender instanceof Player)) {
                if (args.length == 1) {
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target == null) {
                        sender.sendMessage(ChatColor.RED + "Player not found.");
                        return true;
                    }
                    UUID uuid = target.getUniqueId();
                    int tokens = tokenData.getTokens(uuid);
                    sender.sendMessage(ChatColor.GREEN + target.getName() + " has " + tokens + " tokens.");
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Console doesn't have tokens idiot, try specifying a player.");
                return true;
            }
        }

        if (command.getName().equalsIgnoreCase("token") && sender.hasPermission("laqqytoken.token")) {
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
                // remove tokens up to 0
                UUID uuid = target.getUniqueId();
                
                int rmvToken = tokenData.getTokens(uuid) - tokens;
                if (rmvToken < 0) {
                    rmvToken = 0;
                }
                tokenData.setTokens(uuid, rmvToken);
                tokenData.save();
                sender.sendMessage(ChatColor.YELLOW + " " + ChatColor.RED + "Removed "+ rmvToken +" tokens from" + target + ".");
                sender.sendMessage(ChatColor.YELLOW + " " + ChatColor.GREEN + "Token Amount of" + target + ": "+ tokenData.getTokens(uuid));

                return true;
            }

            UUID uuid = target.getUniqueId();

            tokenData.setTokens(uuid, tokenData.getTokens(uuid) + tokens);
            tokenData.save();

            sender.sendMessage(ChatColor.YELLOW + " " + ChatColor.GREEN + "Gave " + target.getName() + " " + tokens + " tokens.");
            target.sendMessage(ChatColor.YELLOW + " " + ChatColor.GREEN + "You have received " + tokens + " lucky block(s)." + ChatColor.YELLOW + " /getlb");
            return true;
        }

        if (command.getName().equalsIgnoreCase("tokenall") && sender.hasPermission("laqqytoken.token")) {if (command.getName().equalsIgnoreCase("tokenall") && sender.hasPermission("laqqytoken.token")) {
            int tokens;
        
            try {
                tokens = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid token amount.");
                return true;
            }
        
            if (tokens < 1) {
                sender.sendMessage(ChatColor.RED + "Token amount must be greater than zero.");
                return true;
            }
        
            int count = 0;
            for (Player player : Bukkit.getOnlinePlayers()) {
                UUID uuid = player.getUniqueId();
                tokenData.setTokens(uuid, tokenData.getTokens(uuid) + tokens);
                player.sendMessage(ChatColor.YELLOW + " " + ChatColor.GREEN + "You have received " + tokens + " lucky block(s)." + ChatColor.YELLOW + " /getlb");
                count++;
            }
        
            tokenData.save();
            sender.sendMessage(ChatColor.YELLOW + " " + ChatColor.GREEN + "Gave " + count + " players " + tokens + " tokens.");
            return true;
        }
    }
    return true;
    }
}
