package ru.fleyer.toffiknockback;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;

public final class ToffiKnockback extends JavaPlugin implements Runnable, Listener {
    private static ToffiKnockback instance;
    FileConfiguration cfg = this.getConfig();
    public HashSet<Player> kbOnList = new HashSet();
    private static HikariDataSource hikari;

    public static ToffiKnockback getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        this.getServer().getScheduler().runTaskTimer(this, this, 1L, 5L);
        getServer().getPluginManager().registerEvents(this,this);
        getCommand("kb").setExecutor(new KnockbackCmd());
        hikari = new HikariDataSource();
        hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikari.addDataSourceProperty("serverName", cfg.getString("mysql.ip"));
        hikari.addDataSourceProperty("port", cfg.getInt("mysql.port"));
        hikari.addDataSourceProperty("databaseName", cfg.getString("mysql.database"));
        hikari.addDataSourceProperty("user", cfg.getString("mysql.username"));
        hikari.addDataSourceProperty("password", cfg.getString("mysql.password"));
        Database.INSTANCE.createTable();


        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        hikari.close();
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (!this.kbOnList.contains(player)) continue;
            for (Player other : Bukkit.getServer().getOnlinePlayers()) {
                if (player.equals(other) || Utils.offset(other, player) > Utils.getLimit(player)) continue;
                if (other.getGameMode() == GameMode.SPECTATOR) {
                    return;
                }
                if (other.hasPermission(getConfig().getString("perms.kb-bypass")) || Database.INSTANCE.getURL(player.getName()).contains(other.getName())) {
                    return;
                }


                Player bottom = other;
                while (bottom.getVehicle() != null) {
                    bottom = (Player) bottom.getVehicle();
                }
                other.sendMessage(msg("distaneNot").replace("{player}",player.getName()));
                Utils.velocity(bottom, Utils.getTrajectory2d(player, bottom), 1.6, true, cfg.getInt("settings-kb.yBase"), 0.0, cfg.getInt("settings-kb.yMax"));

            }
        }
    }

    @EventHandler
    public void leave(PlayerQuitEvent e){
        Player player = e.getPlayer();

        kbOnList.remove(player);

    }

    public HikariDataSource getHikari() {
        return hikari;
    }

    public String msg(String msg) {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages." + msg));
    }
}
