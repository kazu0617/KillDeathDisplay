package net.kazu0617.killdeathdisplay;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author     kazu0617
 * @license    LGPLv3
 * @copyright  Copyright kazu0617 2015
 */
public class Main extends JavaPlugin implements Listener
{
    String Pluginprefix = "[" + ChatColor.GREEN + getDescription().getName() + ChatColor.RESET +"] ";
    String Pluginname = "[" + getDescription().getName() +"] ";
    public ConsoleLog cLog = new ConsoleLog(this);
    public ArrayList<String> enabled = new ArrayList();
    boolean DebugMode = false;
    
    @Override
    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(this, this);
        cLog.info("DebugMode is now ["+DebugMode+"].");
    }
    @Override
    public void onDisable()
    {
        
    }
    @EventHandler
    public void PlayerKill(PlayerDeathEvent e)
    {
        Player killer = e.getEntity().getKiller();
        int[][] kdmap = new int[2][2];
        Player death = e.getEntity().getPlayer();
        kdmap[0][0] = killer.getPlayer().getStatistic(Statistic.PLAYER_KILLS);
        kdmap[0][1] = killer.getPlayer().getStatistic(Statistic.DEATHS);
        kdmap[1][0] = death.getPlayer().getStatistic(Statistic.PLAYER_KILLS);
        kdmap[1][1] = death.getPlayer().getStatistic(Statistic.DEATHS);
        double killer_kd1;
        double death_kd1;
        if( kdmap[0][0] == 0 || kdmap[0][1] == 0)
            killer_kd1 = 1.0;
        else killer_kd1 = kdmap[0][0]/kdmap[0][1];
        if( kdmap[1][0] == 0 || kdmap[1][1] == 0)
            death_kd1 = 1.0;
        else death_kd1 = kdmap[1][0]/kdmap[1][1];
        BigDecimal killer_kd_bd = new BigDecimal(killer_kd1);
        BigDecimal killer_kd_bd1 = killer_kd_bd.setScale(1, BigDecimal.ROUND_HALF_UP);
        BigDecimal death_kd_bd = new BigDecimal(death_kd1);
        BigDecimal death_kd_bd1 = death_kd_bd.setScale(1, BigDecimal.ROUND_HALF_UP);
        e.setDeathMessage(ChatColor.LIGHT_PURPLE + "" + killer + "( "+ ChatColor.GREEN +"K." + kdmap[1][1] + ChatColor.LIGHT_PURPLE +"/"+ChatColor.RED + "D."+ kdmap[1][2] +ChatColor.LIGHT_PURPLE+": "+killer_kd_bd1 + ")さんが" 
                + death + "( "+ ChatColor.GREEN +"K." + kdmap[2][1] + ChatColor.LIGHT_PURPLE +"/"+ChatColor.RED + "D."+ kdmap[2][2] +ChatColor.LIGHT_PURPLE+": "+ death_kd_bd1 + ")を倒しました");
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        OfflinePlayer ofp;
        if(args.length == 1 && "top3".equalsIgnoreCase(args[0]))
        {
             HashMap<String, OfflinePlayer> players = new HashMap<String, OfflinePlayer>();
             List playerlist = new ArrayList();
             players = getAllValidPlayerNames();
             for(OfflinePlayer ofp2 : players.values())
             {
                 int death = ofp2.getPlayer().getStatistic(Statistic.DEATHS);
                 int kill = ofp2.getPlayer().getStatistic(Statistic.PLAYER_KILLS);
                 double kd;
                 if(kill == 0 ||death == 0)
                     kd = 1.0;
                 else kd = (double)kill / (double)death;
                 List player = new ArrayList();
                 player.add(ofp2);
                 player.add(kd);
                 playerlist.add(player);
             }
            for (int i_sortcount = 0; i_sortcount < playerlist.size() - 1; i_sortcount++) {
                for (int i_sort = 0; i_sort < playerlist.size() - 1; i_sort++) {
                    if ((double) ((List) playerlist.get(i_sort)).get(1) < (double) ((List) playerlist.get(i_sort + 1)).get(1)) {
                        List<List> temp = new ArrayList();
                        List p1 = (List) playerlist.get(i_sort);
                        List p2 = (List) playerlist.get(i_sort + 1);
                        temp = p1;
                        p1 = p2;
                        p2 = temp;
                        playerlist.set(i_sort, p1);
                        playerlist.set(i_sort + 1, p2);
                    }
                }
            }
            OfflinePlayer p_ofp[] = {(OfflinePlayer)((List)playerlist.get(0)).get(0),(OfflinePlayer)((List)playerlist.get(1)).get(0),(OfflinePlayer)((List)playerlist.get(2)).get(0)};
            double p_kd[] = {(double)((List)playerlist.get(0)).get(1),(double)((List)playerlist.get(1)).get(1),(double)((List)playerlist.get(2)).get(1)};
            for (int i = 0; i < 3; i++) {
            BigDecimal bd = new BigDecimal(p_kd[i]);
            BigDecimal bd2 = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
            //UUID p_uuid = p_ofp[i].getUniqueId();
            if(DebugMode)
                System.out.println(i + "位: " + p_ofp[i] + " - K/D: " + bd2);
            cLog.Message(sender, ChatColor.GREEN + "" + (i+1) + "位: " + p_ofp[i] + " - K/D: " + bd2);
            }
        }
        else if(args.length >= 2 && "reset" .equalsIgnoreCase(args[0]))
        {
            Player p = Bukkit.getPlayer(args[1]);
            p.getPlayer().setStatistic(Statistic.DEATHS, 0);
            p.getPlayer().setStatistic(Statistic.PLAYER_KILLS, 0);
            cLog.Message(sender,ChatColor.GREEN +""+ p + "(さんのK/Dをリセットしました");
        }
        else if(args.length >= 2 && "view" .equalsIgnoreCase(args[0]))
        {
            Player p = Bukkit.getPlayer(args[1]);
            int Death = p.getStatistic(Statistic.DEATHS);
            int Kill = p.getStatistic(Statistic.PLAYER_KILLS);
            double kd;
            if(Kill == 0 || Death == 0 )
                kd = 1.0;
            else kd = (double)Kill/(double)Death;
            BigDecimal bd = new BigDecimal(kd);
            cLog.Message(sender, args[1] + "さんのKill数: " + p.getPlayer().getStatistic(Statistic.PLAYER_KILLS)
            + ", Death数: " + p.getPlayer().getStatistic(Statistic.DEATHS)
            + ", K/D: " + bd.setScale(3, BigDecimal.ROUND_HALF_UP));
        }
        else if(args.length == 1 && "help" .equalsIgnoreCase(args[0]))
        {
            cLog.Message(sender, ChatColor.GREEN+"--- Command List ---");
            cLog.Message(sender, ChatColor.GREEN+"/"+label+" view <PlayerName> --- 指定したプレイヤーのK/Dを確認します");
            cLog.Message(sender, ChatColor.GREEN+"/"+label+" top3 --- K/Dの上位3人を確認します");
            cLog.Message(sender, ChatColor.GREEN+"/"+label+" reset --- 指定したプレイヤーのK/Dを確認します");
        }
        else if((args.length == 1) && "DebugMode".equalsIgnoreCase(args[0]))
        {
            if(DebugMode)
                DebugMode = false;
            else if(!DebugMode)
                DebugMode = true;
            cLog.Message(sender,"DebugMode is now ["+DebugMode+"].");
        }
        cLog.Message(sender, ChatColor.DARK_RED + "usage: /"+ label + " help");
        return true;
    }
    /*
    * @author     ucchy
    * @license    LGPLv3
    * @copyright  Copyright ucchy 2015
    */
    /**
     * 宛先として有効な全てのプレイヤー名を取得する
     * @return 有効な宛先
     */
    private HashMap<String, OfflinePlayer> getAllValidPlayerNames() {
        HashMap<String, OfflinePlayer> players = new HashMap<String, OfflinePlayer>();
        for ( OfflinePlayer player : Bukkit.getOfflinePlayers() ) {
            if ( player.hasPlayedBefore() || player.isOnline() ) {
                if ( !players.containsKey(player.getName()) ) {
                    players.put(player.getName(), player);
                }
            }
        }
        return players;
    }
            
}