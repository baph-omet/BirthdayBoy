package net.iamvishnu.BirthdayBoy;

import org.bukkit.ChatColor;

public class Messaging {
	public static String playerNotFound(String name) {
		return ChatColor.RED + "Player '" + name + "' not found.";
	}
	
	public static String noPerms(String node) {
		return ChatColor.RED + "You don't have permission to do that. Required permission: " + node;
	}
	
	public static String playersOnly() {
		return ChatColor.RED + "Only Players can run this command. Try \"/birthday help\" for more info.";
	}
	
	public static String noArguments() {
		return ChatColor.RED + "No arguments supplied. Type \"/birthday help\" for a list of useable commands.";
	}
	
	public static String noArguments(String helpCategory) {
		return ChatColor.RED + "No arguments supplied. Check \"/birthday help " + helpCategory + "\" for help.";
	}
	
	public static String invalidArguments(String useage) {
		return ChatColor.RED + "Invalid arguments supplied. Useage: \"" + useage + "\"";
	}
	
	public static String notEnoughArguments(String useage) {
		return ChatColor.RED + "Not enough arguments. Useage: \"" + useage + "\""; 
	}
	
	public static String noSuchSubcommand(String subcommand) {
		return ChatColor.RED + "No such subcommand \"" + subcommand + "\". Type \"/birthday help\" for a list of commands.";
	}
	
	public static String birthdayNotSet() {
		return ChatColor.RED + "Your birthday has not yet been set. Use \"" + ChatColor.YELLOW + "/birthday set <yyyyMMdd>" + ChatColor.RED + "\" to set.";
	}
	
	public static String birthdayNotFound(String name) {
		return ChatColor.RED + "Birthday record for " + name + " not found.";
	}
	
	public static String birthdayAlreadySet() {
		return ChatColor.RED + "Your birthday has already been set. Please ask an admin if you need help changing it.";
	}
	
	public static String showBirthday(Birthday b) {
		return ChatColor.BLUE + "Your birthday is set as: " + ChatColor.YELLOW + b.ToReadableString() + ChatColor.BLUE + ".";
	}
	
	public static String[] showBirthdayInfo(Birthday b, String name) {
		return new String[] { 
				ChatColor.BLUE + "Birthday for " + name + " is: " + ChatColor.YELLOW + b.ToReadableString() + ChatColor.BLUE + ".",
				ChatColor.BLUE + "Last claimed birthday gift: " + ChatColor.YELLOW + (b.LastClaimedDate == null ? "never" : b.LastClaimedToReadableString()) + ChatColor.BLUE + "."
		};
	}
	
	public static String notYourBirthday() {
		return ChatColor.RED + "It's not your birthday! Use " + ChatColor.YELLOW + "/bday" + ChatColor.BLUE + " to check your set birthday.";
	}
	
	public static String noSameDay() {
		return ChatColor.RED + "You cannot set your birthday to the current day. If today is actually your birthday, try again tomorrow!";
	}
	
	public static String alreadyClaimed() {
		return ChatColor.RED + "You've already claimed this year's birthday gift! You'll have to wait until next year.";
	}
	
	public static String invalidWorld(Iterable<String> validWorlds) {
		return ChatColor.RED + "You cannot claim your birthday reward in this world. Valid worlds: " + String.join(",", validWorlds);
	}
	
	public static String birthdayDisabled() {
		return ChatColor.RED + "Birthday rewards are not enabled on this server.";
	}
	
	public static String creativeBlocked() {
		return ChatColor.RED + "Birthday rewards cannot be claimed in Creative or Spectator mode.";
	}
	
	public static String setBdayPrompt() {
		return ChatColor.RED + "Before you can join the server, please take a moment to set your date of birth. Type \"" + ChatColor.YELLOW + "/bday set <yyyyMMdd>" + ChatColor.RED + "\" to set your date of birth.";
	}
	
	public static String specifyPlayerArgument() {
		return ChatColor.RED + "Please specify a player.";
	}
}
