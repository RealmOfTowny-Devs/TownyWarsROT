package net.realmoftowny.townywars.objects;

import com.palmergames.bukkit.towny.exceptions.*;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import net.realmoftowny.townywars.managers.WarManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

//Author: Noxer
public class Rebellion extends War {

    private static ArrayList<Rebellion> allRebellions = new ArrayList<Rebellion>();
    private String name;
    private Town leader;
    private List<Town> originalMotherTowns = new ArrayList<Town>();
    private List<Town> rebels = new ArrayList<Town>();

    public Rebellion(Nation mn, String n, Town l) throws AlreadyRegisteredException {
        this.setDefender(mn);
        Nation rebel = new Nation(n);
        rebel.addTown(l);
        this.setBelligerent(rebel);
        this.name = n;
        this.leader = l;
        allRebellions.add(this);
    }

    //create new rebellion from savefile string
    public Rebellion(String s) {
        super(s);

        try {
            JSONObject root = (JSONObject) new JSONParser().parse(s);

            name = (String) root.get("name");
            leader = TownyUniverse.getDataSource().getTown((String) root.get("rebelLeader"));

            JSONArray originalTownsArray = (JSONArray) root.get("originalTowns");
            for (Object o : originalTownsArray) {
                originalMotherTowns.add(TownyUniverse.getDataSource().getTown((String) ((JSONObject) o).get("name")));
            }

            JSONArray rebelTowns = (JSONArray) root.get("rebelTowns");
            for (Object o : originalTownsArray) {
                rebels.add(TownyUniverse.getDataSource().getTown((String) ((JSONObject) o).get("name")));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NotRegisteredException e) {
            e.printStackTrace();
        }

        allRebellions.add(this);
    }

    public static Rebellion getRebellionFromName(String s) throws Exception {
        for (Rebellion r : allRebellions)
            if (r.getName().equals(s))
                return r;
        throw (new Exception("Rebellion not found!"));
    }

    public Town getLeader() {
        return leader;
    }

    public List<Town> getRebels() {
        return rebels;
    }

    public void Execute(CommandSender cs) {
        try {
            TownyUniverse.getDataSource().newNation(name + "-rebels");
        } catch (AlreadyRegisteredException e2) {
            cs.sendMessage(ChatColor.RED + "Error: A nation with the name of your rebellion already exists.");
            return;
        } catch (NotRegisteredException e) {
            e.printStackTrace();
        }
        try {
            nation2 = TownyUniverse.getDataSource().getNation(name + "-rebels");
        } catch (NotRegisteredException e2) {
            e2.printStackTrace();
        }

        try {
            nation1.removeTown(leader);
        } catch (NotRegisteredException e1) {
            e1.printStackTrace();
        } catch (EmptyNationException e1) {
            e1.printStackTrace();
        }
        try {
            nation2.addTown(leader);
            TownyUniverse.getDataSource().saveTown(leader);
        } catch (AlreadyRegisteredException e1) {
            e1.printStackTrace();
        }

        for (Town town : rebels) {
            try {
                try {
                    nation1.removeTown(town);
                } catch (NotRegisteredException e) {
                    e.printStackTrace();
                } catch (EmptyNationException e) {

                }
                nation2.addTown(town);
                TownyUniverse.getDataSource().saveTown(town);
            } catch (AlreadyRegisteredException e) {
                e.printStackTrace();
            }
        }
        for (Town town : nation1.getTowns()) {
            originalMotherTowns.add(town);
        }

        nation2.setCapital(leader);
        try {
            nation2.setKing(leader.getMayor());
        } catch (TownyException e) {
            e.printStackTrace();
        }

        WarManager.createWar(nation2, nation1, cs);
        TownyUniverse.getDataSource().saveTown(leader);
        TownyUniverse.getDataSource().saveNation(nation2);
        TownyUniverse.getDataSource().saveNationList();
        try {
            WarManager.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
        cs.sendMessage(ChatColor.RED + "You executed your rebellion and are now at war with your nation!");
    }

    public void success() {
        ArrayList<Town> townsToBeMoved = new ArrayList<Town>();
        ArrayList<Town> townsToBeRemoved = new ArrayList<Town>();
        for (Town town : nation2.getTowns()) {
            if (originalMotherTowns.contains(town))
                townsToBeMoved.add(town);
            else
                townsToBeRemoved.add(town);
        }

        for (Town town : townsToBeMoved) {
            try {
                nation2.removeTown(town);
                town.setNation(null);
                nation1.addTown(town);
            } catch (AlreadyRegisteredException e) {
                e.printStackTrace();
            } catch (NotRegisteredException e) {
                e.printStackTrace();
            } catch (EmptyNationException e) {

            }
        }

        for (Town town : townsToBeRemoved) {
            try {
                nation2.removeTown(town);
            } catch (NotRegisteredException e) {
                e.printStackTrace();
            } catch (EmptyNationException e) {
                //exception WILL be created. Ignore.
            }
        }

        TownyUniverse.getDataSource().saveNation(nation1);
        TownyUniverse.getDataSource().saveNation(nation2);
        try {
            WarManager.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void peace() {
        try {
            nation1.collect(nation2.getHoldingBalance());
            nation2.pay(nation2.getHoldingBalance(), "Lost rebellion. Tough luck!");
        } catch (EconomyException e1) {
            e1.printStackTrace();
        }

        ArrayList<Town> l = new ArrayList<Town>(nation2.getTowns());
        for (Town town : l)
            try {
                WarManager.townremove = town;
                try {
                    nation2.removeTown(town);
                } catch (EmptyNationException e) {
                    e.printStackTrace();
                }
                nation1.addTown(town);
            } catch (AlreadyRegisteredException e) {
                e.printStackTrace();
            } catch (NotRegisteredException e) {
                e.printStackTrace();
            }

        try {
            WarManager.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Nation getRebelnation() {
        return nation2;
    }

    public boolean isRebelTown(Town town) {
        for (Town rebel : rebels)
            if (town == rebel)
                return true;
        return false;
    }

    public boolean isRebelLeader(Town town) {
        return town == leader;
    }

    public String getName() {
        return name;
    }

    public static ArrayList<Rebellion> getAllRebellions() {
        return allRebellions;
    }

    public Nation getMotherNation() {
        return nation1;
    }

    public void addRebell(Town town) {
        rebels.add(town);
    }

    public void removeRebell(Town town) {
        rebels.remove(town);
    }

    public String toString() {
        String s = super.toString();

        try {
            JSONObject root = (JSONObject) new JSONParser().parse(s);
            root.put("rebelLeader", leader.getName());

            JSONArray originalTownsArray = new JSONArray();
            for (Town town : originalMotherTowns) {
                originalTownsArray.add(town.getName());
            }

            JSONArray rebelTowns = new JSONArray();
            for (Town town : rebels) {
                rebelTowns.add(town.getName());
            }

            root.put("originalTowns", originalTownsArray);
            root.put("rebelTowns", rebelTowns);
            root.put("name", name);

            return root.toJSONString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return s;
    }
}