package net.wdlvn.Leveling;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Main extends JavaPlugin implements Listener {

    public static Main plugin;

    @Override
    public void onEnable(){
        getConfig().options().copyDefaults(true);
        saveConfig();
        plugin = this;
        Bukkit.getPluginManager().registerEvents(this,this);
    }

    public static float eval(String infix, int lvl) {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        String stringResult;
        try {
            stringResult = engine.eval(infix.replace("{lvl}", String.valueOf(lvl))).toString();
            float doubleResult = Float.parseFloat(stringResult);
            return doubleResult;
        } catch (ScriptException ex) {
            ex.printStackTrace();
        }
        return (1);

    }


    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }


    public static double getMultiply (Player p){
        double d = 1.0;

        if (plugin.getConfig().isSet("Multiply."+p.getName())){
            d = plugin.getConfig().getDouble("Multiply."+p.getName());
        }

        return d;
    }

    public static void setMultiply (Player p, double multiply){
        plugin.getConfig().set("Multiply."+p.getName(), multiply);
    }

    public static void modifyMultiply (Player p, double value){
        plugin.getConfig().set("Multiply."+p.getName(), getMultiply(p)+value);
    }


    public static float getRequireExp(Player p) {
        float RExp = (getNextExp(p)-p.getExp());
        return RExp;
    }


    public static float getNextExp(Player p) {
        float RExp =  eval(plugin.getConfig().getString("System.Math"), p.getLevel()+1);
        return RExp;
    }


    @EventHandler
    public void onExp(PlayerExpChangeEvent e){
        Player player = e.getPlayer();
        float xpAmount = eval(plugin.getConfig().getString("System.Math"), player.getLevel());
        float xpBarCurrent = player.getExp() * xpAmount;
        float xpBarNew = (float) ((xpBarCurrent + e.getAmount()*getMultiply(player)) / xpAmount);
        e.setAmount(0);
        player.setExp((float) (xpBarNew));
    }

    @EventHandler
    public void onLevel(PlayerLevelChangeEvent e){
        Player p = e.getPlayer();
        if (e.getNewLevel() > plugin.getConfig().getInt("System.MaxLevel")){
            p.setLevel(plugin.getConfig().getInt("System.MaxLevel"));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (cmd.getName().equalsIgnoreCase("level")){
            if (args[0].equalsIgnoreCase("add")){
                if (args.length == 2){
                    if (sender instanceof  Player){
                        Player player = (Player) sender;
                        if (isNumeric(args[1])) {
                            float xpAmount = eval(plugin.getConfig().getString("System.Math"), player.getLevel());
                            float xpBarCurrent = player.getExp() * xpAmount;
                            float xpBarNew = (float) ((xpBarCurrent + Float.parseFloat(args[1]) * getMultiply(player)) / xpAmount);
                            player.setExp((float) (xpBarNew));
                        }
                        else {
                            sender.sendMessage(ChatColor.DARK_RED+"ERROR: "+ChatColor.GOLD+args[1]+" is not a valid number");
                        }
                    } else {
                        sender.sendMessage(ChatColor.DARK_RED+"ERROR: "+ChatColor.GOLD+" You must be a player to use this command");
                    }
                }
                else if (args.length == 3){
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player.isOnline()) {
                        if (isNumeric(args[2])) {
                            float xpAmount = eval(plugin.getConfig().getString("System.Math"), player.getLevel());
                            float xpBarCurrent = player.getExp() * xpAmount;
                            float xpBarNew = (float) ((xpBarCurrent + Float.parseFloat(args[2]) * getMultiply(player)) / xpAmount);
                            player.setExp((float) (xpBarNew));
                        }
                        else {
                            sender.sendMessage(ChatColor.DARK_RED+"ERROR: "+ChatColor.GOLD+args[2]+" is not a valid number");
                        }
                    } else {
                        sender.sendMessage(ChatColor.DARK_RED+"ERROR: "+ChatColor.GOLD+args[1]+" is not online");
                    }
                }
                else {
                    sender.sendMessage(ChatColor.DARK_RED+"ERROR: "+ChatColor.GOLD+"/level add [Player] <Value>");
                }
            }
            else if (args[0].equalsIgnoreCase("take")){
                if (args.length == 2){
                    if (sender instanceof  Player){
                        Player player = (Player) sender;
                        if (isNumeric(args[1])) {
                            float xpAmount = eval(plugin.getConfig().getString("System.Math"), player.getLevel());
                            float xpBarCurrent = player.getExp() * xpAmount;
                            float xpBarNew = (float) (((xpBarCurrent * getMultiply(player)) / xpAmount)-((Float.parseFloat(args[1]) * getMultiply(player)) / xpAmount));
                            player.setExp((float) (xpBarNew));
                        }
                        else {
                            sender.sendMessage(ChatColor.DARK_RED+"ERROR: "+ChatColor.GOLD+args[1]+" is not a valid number");
                        }
                    } else {
                        sender.sendMessage(ChatColor.DARK_RED+"ERROR: "+ChatColor.GOLD+" You must be a player to use this command");
                    }
                }
                else if (args.length == 3){
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player.isOnline()) {
                        if (isNumeric(args[2])) {
                            float xpAmount = eval(plugin.getConfig().getString("System.Math"), player.getLevel());
                            float xpBarCurrent = player.getExp() * xpAmount;
                            float xpBarNew = (float) (((xpBarCurrent * getMultiply(player)) / xpAmount)-((Float.parseFloat(args[2]) * getMultiply(player)) / xpAmount));
                            player.setExp((float) (xpBarNew));
                        }
                        else {
                            sender.sendMessage(ChatColor.DARK_RED+"ERROR: "+ChatColor.GOLD+args[2]+" is not a valid number");
                        }
                    } else {
                        sender.sendMessage(ChatColor.DARK_RED+"ERROR: "+ChatColor.GOLD+args[1]+" is not online");
                    }
                }
                else {
                    sender.sendMessage(ChatColor.DARK_RED+"ERROR: "+ChatColor.GOLD+"/level add [Player] <Value>");
                }
            }
            else if (args[0].equalsIgnoreCase("set")){
                if (args.length == 2){
                    if (sender instanceof  Player){
                        Player player = (Player) sender;
                        if (isNumeric(args[1])) {
                            float xpAmount = eval(plugin.getConfig().getString("System.Math"), player.getLevel());
                            float xpBarNew = (float)((Float.parseFloat(args[1]) * getMultiply(player)) / xpAmount);
                            player.setExp((float) (xpBarNew));
                        }
                        else {
                            sender.sendMessage(ChatColor.DARK_RED+"ERROR: "+ChatColor.GOLD+args[1]+" is not a valid number");
                        }
                    } else {
                        sender.sendMessage(ChatColor.DARK_RED+"ERROR: "+ChatColor.GOLD+" You must be a player to use this command");
                    }
                }
                else if (args.length == 3){
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player.isOnline()) {
                        if (isNumeric(args[2])) {
                            float xpAmount = eval(plugin.getConfig().getString("System.Math"), player.getLevel());
                            float xpBarNew = (float)((Float.parseFloat(args[2]) * getMultiply(player)) / xpAmount);
                            player.setExp((float) (xpBarNew));
                        }
                        else {
                            sender.sendMessage(ChatColor.DARK_RED+"ERROR: "+ChatColor.GOLD+args[2]+" is not a valid number");
                        }
                    } else {
                        sender.sendMessage(ChatColor.DARK_RED+"ERROR: "+ChatColor.GOLD+args[1]+" is not online");
                    }
                }
                else {
                    sender.sendMessage(ChatColor.DARK_RED+"ERROR: "+ChatColor.GOLD+"/level add [Player] <Value>");
                }
            }
        }
        return false;
    }
}
