package ru.fleyer.toffiknockback;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KnockbackCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ToffiKnockback cg = ToffiKnockback.getInstance();


        if (!(sender instanceof Player)) {
            sender.sendMessage(cg.msg("ConsoleSender"));
            return false;
        }
        final Player player = (Player) sender;

        if (args.length >= 1) {
            if (!player.hasPermission(cg.getConfig().getString("perms.kb-usecmd"))) {
                player.sendMessage(cg.msg("noPerm"));
                return false;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                if (player.hasPermission(cg.getConfig().getString("perms.kb-reload"))) {
                    cg.reloadConfig();
                    player.sendMessage(cg.msg("reloadConfigs"));
                    return false;
                }else player.sendMessage(cg.msg("noPerm"));
                return false;
            }
            if (args[0].equalsIgnoreCase("help")) {
                for (String s : cg.getConfig().getStringList("messages.help")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
                }
                return false;
            } else if (args[0].equalsIgnoreCase("list")) {
                player.sendMessage(cg.msg("friends-name").replace("{friends-size}", String.valueOf(Database.INSTANCE.getURL(player.getName()).size())));
                for (Object s : Database.INSTANCE.getURL(player.getName()).toArray())
                    player.sendMessage(cg.msg("list").replace("{friends}", String.valueOf(s)));

                return false;

            }
            if (args.length != 1) {
                Player target = Bukkit.getPlayer(args[1]);
                if (args[0].equalsIgnoreCase("add")) {
                    if (args[1].equalsIgnoreCase(player.getName())) {
                        player.sendMessage(cg.msg("youNot"));
                        return false;
                    }
                    if (target == null || !target.isOnline()) {
                        player.sendMessage(cg.msg("notPlayer").replace("{target}", args[1]));
                        return false;
                    }

                    Utils.PerformCommand("add", player, Bukkit.getPlayer(args[1]));
                    return false;
                } else if (args[0].equalsIgnoreCase("remove")) {
                    if (Database.INSTANCE.getURL(player.getName()).size() == 0) {
                        player.sendMessage(cg.msg("wl-null"));
                        return false;
                    }
                    if (!Database.INSTANCE.getURL(player.getName()).contains(args[1])) {
                        player.sendMessage(cg.msg("wl-notPlayer").replace("{player}", args[1]));
                        return false;
                    }


                    Database.INSTANCE.removefriend(player.getName(), args[1]);
                    player.sendMessage(cg.msg("wl-remove").replace("{player}", args[1]));
                    return false;
                }

            } else {
                for (String s : cg.getConfig().getStringList("messages.help")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
                }
                return false;
            }

        } else if (player.hasPermission(cg.getConfig().getString("perms.kb-cmd"))) {
            if (!cg.kbOnList.contains(player)) {
                cg.kbOnList.add(player);
                player.sendMessage(cg.msg("start"));
                return false;
            } else {
                cg.kbOnList.remove(player);
                player.sendMessage(cg.msg("stop"));
                return false;
            }
        } else {
            player.sendMessage(cg.msg("noPerm"));
        }
        return false;
    }
}
