package net.iamvishnu.BirthdayBoy;

import java.time.LocalDate;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {
	
	public static String[] Labels = new String[] {"birthday", "bday", "bd"};

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String cmdName = command.getName().toLowerCase();
		
		boolean found = false;
		for (String s : Labels) {
			if (s.equals(cmdName)) {
				found = true;
				break;
			}
		} if (!found) return false;
		
		if (args.length > 0) {
			return Delegate(sender, args);
		} else {
			if (sender instanceof Player) {
				Birthday b = Birthday.GetBirthday((Player)sender);
				if (b != null) sender.sendMessage(Messaging.showBirthday(b));
				else sender.sendMessage(Messaging.birthdayNotSet());
			} else sender.sendMessage(Messaging.playersOnly());
		}
		
		return true;
	}
	
	public static boolean Delegate(CommandSender sender, String[] args) {
		switch (args[0].toLowerCase()) {
			case "info":
				Info(sender, args);
			case "set":
				Set(sender, args);
				return true;
			case "remove":
				Remove(sender,args);
				return true;
			case "claim":
				Claim(sender);
				return true;
			default:
				sender.sendMessage(Messaging.noSuchSubcommand(args[0].toLowerCase()));
				return false;
		}
	}
	
	private static void Info(CommandSender sender, String[] args) {
		if (args.length < 2) {
			if (sender instanceof Player) {
				Player target = (Player)sender;
				Birthday b = Birthday.GetBirthday(target);
				if (b == null) sender.sendMessage(Messaging.birthdayNotSet());
				else sender.sendMessage(Messaging.showBirthday(b));
			} else sender.sendMessage(Messaging.notEnoughArguments("/bday info <player>"));
			
			return;
		}
		
		if (!sender.hasPermission("birthday.others")) {
			sender.sendMessage(Messaging.noPerms("birthday.others"));
			return;
		}
		
		Player target = null;
		if (args.length > 2) {
			target = GetPlayerByName(args[2], sender);
			if (target == null) return;
		}
		
		Birthday b = Birthday.GetBirthday(target);
		if (b == null) sender.sendMessage(Messaging.birthdayNotFound(target.getName()));
		else sender.sendMessage(Messaging.showBirthdayInfo(b, target.getName()));
	}
	
	private static void Set(CommandSender sender, String[] args) {
		Player target = null;
		if (args.length > 2) {
			target = GetPlayerByName(args[2], sender);
			if (target == null) return;
		}
		
		if (target == null) {
			if (sender instanceof Player) {
				target = (Player)sender;
			} else {
				sender.sendMessage(Messaging.playersOnly());
				return;
			}
		}
		
		Birthday b = Birthday.GetBirthday(target);
		if (b != null && !sender.hasPermission("birthday.others")) {
			sender.sendMessage(Messaging.birthdayAlreadySet());
			return;
		}
		
		if (args.length < 2) {
			sender.sendMessage(Messaging.notEnoughArguments("/bday set <yyyymmdd>"));
			return;
		}
		
		LocalDate date = Birthday.ParseDate(args[1]);
		if (date == null) {
			sender.sendMessage(Messaging.invalidArguments("/bday set <yyyymmdd>"));
			return;
		}
		
		LocalDate now = LocalDate.now();
		if (date.getDayOfMonth() == now.getDayOfMonth() && date.getMonth() == now.getMonth() && !BirthdayBoy.GetConfig().getBoolean("allow-sameday") && !sender.hasPermission("birthday.others")) {
			sender.sendMessage(Messaging.noSameDay());
			return;
		}
		
		if (BirthdayBoy.GetConfig().getBoolean("age-validation")) {
			LocalDate maximum = now.minusYears(BirthdayBoy.GetConfig().getInt("minimum-age"));
			if (date.isAfter(maximum)) {
				for (String c : BirthdayBoy.GetConfig().getStringList("cmds-on-validate-failure")) {
					SendCommand(c, target.getName());
				}
				
				BirthdayBoy.serverLog.info(sender.getName() + " failed age verification.");
				return;
			}
		}
		
		if (b != null) b.Date = date;
		else b = new Birthday(date, target.getUniqueId(), null);
		
		b.Save();
		if (BirthdayBoy.GetConfig().getBoolean("age-validation")) {
			for (String c : BirthdayBoy.GetConfig().getStringList("cmds-on-validate-success")) {
				SendCommand(c, target.getName());
			}
			BirthdayBoy.serverLog.info(target.getName() + " passed age validation.");
		}
		
		sender.sendMessage(ChatColor.BLUE + "Birthday is now set as "+ ChatColor.YELLOW + b.ToReadableString() + ChatColor.BLUE + ". Please contact an admin if you need it changed.");
	}
	
	private static void Remove(CommandSender sender, String[] args) {
		if (!sender.hasPermission("birthday.others")) {
			sender.sendMessage(Messaging.noPerms("birthday.others"));
			return;
		}
		
		Player target = null;
		if (args.length > 2) {
			target = GetPlayerByName(args[2], sender);
			if (target == null) return;
		}
		
		Birthday b = Birthday.GetBirthday(target);
		if (b == null) {
			sender.sendMessage(Messaging.birthdayNotFound(null));
			return;
		}
		
		b.Delete();
		
		sender.sendMessage(ChatColor.BLUE + "Birthday record for " + ChatColor.YELLOW + target.getName() + ChatColor.BLUE + " deleted.");
		
	}
	
	private static void Claim(CommandSender sender) {
		if (!BirthdayBoy.GetConfig().getBoolean("birthday")) {
			sender.sendMessage(Messaging.birthdayDisabled());
			return;
		}
		
		if (!(sender instanceof Player)) {
			sender.sendMessage(Messaging.playersOnly());
			return;
		}
		
		Player p = (Player)sender;
		Birthday b = Birthday.GetBirthday(p);
		if (b == null) {
			sender.sendMessage(Messaging.birthdayNotSet());
			return;
		}
		
		if (!b.IsToday()) {
			sender.sendMessage(Messaging.notYourBirthday());
			return;
		}
		
		if (b.ClaimedToday()) {
			sender.sendMessage(Messaging.alreadyClaimed());
			return;
		}
		
		List<String> worlds = BirthdayBoy.GetConfig().getStringList("allowed-worlds");
		if (worlds.size() > 0 && !worlds.contains(p.getWorld().getName())) {
			sender.sendMessage(Messaging.invalidWorld(worlds));
			return;
		}
		
		if (BirthdayBoy.GetConfig().getBoolean("block-creative-claiming") && p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR) {
			sender.sendMessage(Messaging.creativeBlocked());
			return;
		}
		
		// Actually give reward
		for (String c : BirthdayBoy.GetConfig().getStringList("cmds-reward")) {
			SendCommand(c, p.getName());
		}
		
		sender.sendMessage(ChatColor.BLUE + "Rewards claimed! Have a great day!");
		
		b.LastClaimedDate = LocalDate.now();
		b.Save();
	}

	private static Player GetPlayerByName(String name, CommandSender sender) {
		Player target = (Player)Bukkit.getOfflinePlayerIfCached(name);
		if (target == null) sender.sendMessage(Messaging.playerNotFound(name));
		return target;
	}
	
	private static String GetFormattedCommand(String input, String targetName) {
		return input.replace("%p", targetName).replace("%a", Integer.toString(BirthdayBoy.GetConfig().getInt("minimum-age")));
	}
	
	private static void SendCommand(String command, String targetName) {
		BirthdayBoy.Plugin.getServer().dispatchCommand(BirthdayBoy.Plugin.getServer().getConsoleSender(), GetFormattedCommand(command, targetName));
	}
}
