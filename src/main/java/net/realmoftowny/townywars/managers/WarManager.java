package net.realmoftowny.townywars.managers;

import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import net.realmoftowny.townywars.objects.War;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class WarManager {
    public static Town townremove;

    private static ArrayList<War> wars = new ArrayList<>();

    public static void save() {
        // TODO sql and file io stuff
    }

    public static void endWar(Nation winner, Nation loser, boolean b) { // What is 'b'?
        ArrayList<War> activeWars = new ArrayList<>();
        for (War w : wars) {
            if (w.getNationsInWar().contains(winner) && w.getNationsInWar().contains(loser)) {
                activeWars.add(w);
            }
        }
        wars.removeAll(activeWars);
    }

    public static War[] getWarsForNation(Nation n) {
        ArrayList<War> activeWars = new ArrayList<>();
        for (War w : wars) {
            if (w.getNationsInWar().contains(n)) {
                activeWars.add(w);
            }
        }
        return activeWars.toArray(new War[0]);
    }

    public static void createWar(Nation rebelNation, Nation motherNation, CommandSender cs) {
        for (War w : getWarsForNation(rebelNation)) {
            if (w.equals(new War(motherNation, rebelNation))) {
                cs.sendMessage(ChatColor.RED + "Your nation is already at war with " + motherNation.getName());
                return;
            }
        }
        wars.add(new War(motherNation, rebelNation));
    }

    public static ArrayList<War> getWars() {
        return wars;
    }
}
