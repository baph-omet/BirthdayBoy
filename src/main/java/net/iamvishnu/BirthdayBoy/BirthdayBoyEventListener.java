package net.iamvishnu.BirthdayBoy;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;

@SuppressWarnings("deprecation")
public class BirthdayBoyEventListener implements Listener {
	@EventHandler (priority = EventPriority.MONITOR)
	public void onLogin(PlayerLoginEvent e) {
		Player player = e.getPlayer();
		Birthday b = Birthday.GetBirthday(player);
		if (b == null) {
			if (BirthdayBoy.GetConfig().getBoolean("age-validation") && !player.hasPermission("birthday.bypass")) {
				player.sendMessage(Messaging.setBdayPrompt());
			}
		} else if (BirthdayBoy.GetConfig().getBoolean("birthday") && b.IsToday()) {
			// Send birthday message to player
			String msg = ChatColor.translateAlternateColorCodes('&', BirthdayBoy.Plugin.getConfig().getString("msg-reward").replaceAll("%p", player.getName()));
			if (msg != null && !msg.isBlank()) player.sendMessage(msg);
			
			// Send separate birthday message to everyone else
			String msg_all = ChatColor.translateAlternateColorCodes('&', BirthdayBoy.Plugin.getConfig().getString("msg-login").replaceAll("%p", player.getName()));
			if (msg_all != null && !msg.isBlank()) {
				for (Player p : BirthdayBoy.Plugin.getServer().getOnlinePlayers()) {
					if (p == player) continue;
					p.sendMessage(msg_all);
				}
				
				BirthdayBoy.serverLog.info(String.format("Today is %s's birthday.", player.getName()));
			}
		}
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onPlayerMove(PlayerMoveEvent e) {
		if(!BirthdayBoy.GetConfig().getBoolean("age-validation")) return;
		Player player = e.getPlayer();
		Birthday b = Birthday.GetBirthday(player);
		if (b == null && BirthdayBoy.GetConfig().getBoolean("restrict-movement") && !player.hasPermission("birthday.bypass")) {
			e.setCancelled(true);
			String msg =  ChatColor.translateAlternateColorCodes('&', BirthdayBoy.GetConfig().getString("restrict-movement-msg"));
			if (msg != null && !msg.isBlank()) player.sendMessage(msg);
		}
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onPlayerChat(PlayerChatEvent e) {
		if(!BirthdayBoy.GetConfig().getBoolean("age-validation")) return;
		Player player = e.getPlayer();
		Birthday b = Birthday.GetBirthday(player);
		if (b == null && BirthdayBoy.GetConfig().getBoolean("restrict-chat") && !player.hasPermission("birthday.bypass")) {
			if (e.getMessage().charAt(0) != '/') return;
			e.setCancelled(true);
			String msg =  ChatColor.translateAlternateColorCodes('&', BirthdayBoy.GetConfig().getString("restrict-chat-msg"));
			if (msg != null && !msg.isBlank()) player.sendMessage(msg);
		}
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onCommand(PlayerCommandPreprocessEvent e) {
		if(!BirthdayBoy.GetConfig().getBoolean("age-validation")) return;
		Player player = e.getPlayer();
		Birthday b = Birthday.GetBirthday(player);
		if (b == null && BirthdayBoy.GetConfig().getBoolean("restrict-commands") && !player.hasPermission("birthday.bypass")) {
			
			// Check to see if command is allowed
			String message = e.getMessage();
			String[] arr = message.split(" ");
			List<String> allowed = BirthdayBoy.GetConfig().getStringList("command-whitelist");
			if (allowed.size() > 0 && allowed.contains(arr[0])) return;
			
			List<String> baked_in = new ArrayList<String>();
			baked_in.add("birthday");
			baked_in.add("bday");
			baked_in.add("bd");
			if (baked_in.contains(arr[0])) return;
			
			// Cancel event
			e.setCancelled(true);
			String msg =  ChatColor.translateAlternateColorCodes('&', BirthdayBoy.GetConfig().getString("restrict-chat-msg"));
			if (msg != null && !msg.isBlank()) player.sendMessage(msg);
		}
	}
}
