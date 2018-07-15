package net.realmoftowny.townywars.objects;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import net.realmoftowny.townywars.TownyWars;
import net.realmoftowny.townywars.managers.WarManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class War {
	
	protected Nation nation1, nation2;
	private int nation1points, nation2points;
	private Map<Town, Integer> towns = new HashMap<>();
	
	public War(Nation nat, Nation onat) {
		nation1 = nat;
		nation2 = onat;
		recalculatePoints(nat);
		recalculatePoints(onat);
	}
	
	public War(String s){
		try {
			JSONObject root = (JSONObject) new JSONParser().parse(s);

			nation1 = TownyUniverse.getDataSource().getNation((String) root.get("nation1"));
			nation2 = TownyUniverse.getDataSource().getNation((String) root.get("nation2"));

            nation1points = Integer.parseInt((String) root.get("n1points"));
            nation2points = Integer.parseInt((String) root.get("n2points"));

            JSONArray townsArray = (JSONArray) root.get("towns");

            for (Object o : townsArray) {
                towns.put(TownyUniverse.getDataSource().getTown((String) ((JSONObject) o).get("name")), Integer.parseInt((String) ((JSONObject) o).get("maxPoints")));
            }
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (NotRegisteredException e) {
			e.printStackTrace();
		}
	}

    public War() {
	    TownyWars.getInstance().getLogger().warning("Empty War object created! Nations must be set with setters or the plugin will error!");
    }

    /**
     * Converts object to JSON data string.
     * @return json
     */
	public String toString(){
        JSONObject root = new JSONObject();

        root.put("nation1", nation1.getName());
        root.put("nation2", nation2.getName());

        root.put("n1points", nation1points);
        root.put("n2points", nation2points);

        JSONArray townArray = new JSONArray();
		
		for(Town town : towns.keySet()){
			JSONObject townJson = new JSONObject();
			townJson.put("name", town.getName());
			townJson.put("maxPoints", towns.get(town));
			townArray.add(townJson);
		}

		root.put("towns", townArray);
		
		return root.toJSONString();
	}

	public void setDefender(Nation nation1) {
		this.nation1 = nation1;
	}

	public void setBelligerent(Nation nation2) {
		this.nation2 = nation2;
	}
	
	public Set<Nation> getNationsInWar() {
		HashSet<Nation> s = new HashSet<Nation>();
		s.add(nation1);
		s.add(nation2);
		return s;
	}

	protected void setNation1points(int p) {
	    nation1points = p;
    }

    protected void setNation2points(int p) {
        this.nation2points = p;
    }

    public void removeTown(Town town, Nation nation){
		towns.remove(town);
		if(nation == nation1)
			nation1points--;
		else if(nation == nation2)
			nation2points--;
	}
	
	public Integer getNationPoints(Nation nation) throws Exception {
		if(nation == nation1)
			return nation1points;
		if(nation == nation2)
			return nation2points;
		throw(new Exception("Not registred"));
	}

	public Integer getTownPoints(Town town) throws Exception {
		return towns.get(town);
	}

	//rewrite
	public final void recalculatePoints(Nation nat) {
		if(nat.equals(nation1))
			nation1points = nat.getNumTowns();
		else if(nat.equals(nation2))
			nation2points = nat.getNumTowns();
		for (Town town : nat.getTowns()) {
			towns.put(town, (int) getTownMaxPoints(town));
		}
	}
	
	public void addNewTown(Town town){
		towns.put(town, (int) getTownMaxPoints(town));
	}
	
	public static double getTownMaxPoints(Town town){
		return (50-50*Math.pow(Math.E, (-0.04605*town.getNumResidents()))) + (60-60*Math.pow(Math.E, (-0.00203*town.getTownBlocks().size())));
	}

	boolean hasNation(Nation onation) {
		if(onation != nation1 && onation != nation2 && (onation.getName().equals(nation1.getName()) || onation.getName().equals(nation2.getName())))
				System.out.println("hasNation() error. Please report to Noxer");
		return (onation.getName().equals(nation1.getName()) || onation.getName().equals(nation2.getName()));
	}

	public Nation getEnemy(Nation onation) throws Exception {
			if (nation1 == onation) {
				return nation2;
			}
			if (nation2 == onation) {
				return nation1;
			}
		throw new Exception("War.getEnemy: Specified nation is not in war.");
	}

	public void chargeTownPoints(Nation nnation, Town town, double i) {
		towns.replace(town, towns.get(town) - (int) i);
		if (towns.get(town) <= 0) {
			try {
				if(nnation.getTowns().size() > 1 && nnation.getCapital() == town){
					if(nnation.getTowns().get(0) != town){
						nnation.setCapital(nnation.getTowns().get(0));
					}else{
						nnation.setCapital(nnation.getTowns().get(1));
					}
				}
					
					
				towns.remove(town);
				Nation nation = this.getEnemy(nnation);
				removeNationPoint(nnation);
				addNationPoint(nation, town);
				try {	
						WarManager.townremove = town;
						nnation.removeTown(town);
				} catch (Exception ex) {
				}
				nation.addTown(town);
				town.setNation(nation);
				TownyUniverse.getDataSource().saveNation(nation);
				TownyUniverse.getDataSource().saveNation(nnation);
				try {
					WarManager.save();
				} catch (Exception e) {
					e.printStackTrace();
				}
				broadcast(
						nation,
						ChatColor.GREEN
								+ town.getName()
								+ " has been conquered and joined your nation in the war!");
			} catch (Exception ex) {
				Logger.getLogger(War.class.getName()).log(Level.SEVERE, null,
						ex);
			}
		}
		try {
			if (this.getNationPoints(nnation) <= 0) {
				try {
						Nation winner = getEnemy(nnation);
						Nation looser = nnation;
						boolean endWarTransfersDone = false;
						for(Rebellion r : Rebellion.getAllRebellions()){
							if(r.getRebelnation() == winner){
								winner.getCapital().collect(winner.getHoldingBalance());
								winner.pay(winner.getHoldingBalance(), "You are disbanded. You don't need money.");
								endWarTransfersDone = true;
								break;
							}
						}
						
						if(!endWarTransfersDone){
							winner.collect(looser.getHoldingBalance());
							looser.pay(looser.getHoldingBalance(), "Conquered. Tough luck!");
						}
						WarManager.endWar(winner, looser, false);

				} catch (Exception ex) {
					Logger.getLogger(War.class.getName()).log(Level.SEVERE, null,
							ex);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			WarManager.save();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeNationPoint(Nation nation) {
		if(nation1 == nation)
			nation1points--;
		if(nation2 == nation)
			nation2points--;
	}

	public void addNationPoint(Nation nation, Town town) {
		if(nation1 == nation)
			nation1points++;
		if(nation2 == nation)
			nation2points++;
		towns.put(town,
				(int) (town.getNumResidents()
						* TownyWars.pPlayer + TownyWars.pPlot
						* town.getTownBlocks().size()));
	}

	public static void broadcast(Nation n, String message) {
		for (Resident re : n.getResidents()) {
			Player plr = Bukkit.getPlayer(re.getName());
			if (plr != null) {
				plr.sendMessage(message);
			}
		}
	}

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof War) {
            if (((War) obj).getNationsInWar().containsAll(getNationsInWar())) {
                return true;
            }
        }
        return false;
    }
}
