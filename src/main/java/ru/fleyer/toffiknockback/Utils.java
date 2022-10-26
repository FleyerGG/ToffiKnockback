package ru.fleyer.toffiknockback;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Utils {
    static ToffiKnockback cg = ToffiKnockback.getInstance();

    public static void PerformCommand(String type, Player p1, Player p2) {

        if (type.equals("add")) {
            if (p2.hasPermission(cg.getConfig().getString("perms.kb-bypass"))) {
                p1.sendMessage(cg.msg("bypass").replace("{player}", p2.getName()));
                return;
            }
            if (Database.INSTANCE.getURL(p1.getName()).size() == cg.getConfig().getInt("settings-kb.maxLimitFriends")) {
                p1.sendMessage(cg.msg("noLimit"));
                return;
            }
            if (Database.INSTANCE.getURL(p1.getName()).contains(p2.getName())) {
                p1.sendMessage(cg.msg("wl-alredy").replace("{player}", p2.getName()));
                return;
            }

            Database.INSTANCE.addFriend(p1.getName(), p2.getName());
            p1.sendMessage(cg.msg("wl-add").replace("{player}", p2.getName()));
            return;

        }

    }

    public static Vector getTrajectory2d(Entity from, Entity to) {
        return to.getLocation().toVector().subtract(from.getLocation().toVector()).setY(0).normalize();
    }

    public static double offset(Entity a, Entity b) {
        return a.getLocation().toVector().subtract(b.getLocation().toVector()).length();
    }

    public static void velocity(Entity ent, Vector vec, double str, boolean ySet, double yBase, double yAdd, double yMax) {
        if (Double.isNaN(vec.getX()) || Double.isNaN(vec.getY()) || Double.isNaN(vec.getZ()) || vec.length() == 0.0) {
            return;
        }
        if (ySet) {
            vec.setY(yBase);
        }
        vec.normalize();
        vec.multiply(str);
        vec.setY(vec.getY() + yAdd);
        if (vec.getY() > yMax) {
            vec.setY(yMax);
        }
        ent.setFallDistance(0.0f);
        ent.setVelocity(vec);
    }


    public static int getLimit(Player player) {
        int i;
        for (i = 15; i > 0; i--) {
            if (player.isOp() || player.hasPermission("*")){
                return cg.getConfig().getInt("settings-kb.opDistance");
            }
            if (player.hasPermission(cg.getConfig().getString("perms.kb-limit") + i)) {
                return i;

            }
        }
        return i;
    }

}
