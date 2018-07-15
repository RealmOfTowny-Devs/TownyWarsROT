package net.realmoftowny.townywars.events;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import net.realmoftowny.townywars.managers.WarManager;
import net.realmoftowny.townywars.objects.War;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class TownyWarsEvents implements Listener {

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player p = event.getPlayer();

        // Check if player's nation is at war. Send them a message if they are.
        try {
            Resident r = TownyUniverse.getDataSource().getResident(p.getName());
            for (Nation n : TownyUniverse.getDataSource().getNations()) {
                if (n.hasResident(r)) {
                    for (War w : WarManager.getWarsForNation(n)) {
                        p.sendMessage(ChatColor.RED + "You are at war with nation " + w.getEnemy(n).getName() + "!");
                    }
                    break;
                }
            }
        } catch (NotRegisteredException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
