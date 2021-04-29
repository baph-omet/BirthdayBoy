package net.iamvishnu.BirthdayBoy;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class BirthdayBoy extends JavaPlugin {
	static Plugin Plugin;
	static final Logger serverLog = Logger.getLogger("Minecraft");
	static Config BirthdayConfig;
	
	@Override
	public void onEnable() {
		Plugin = this;
		saveDefaultConfig();
		getConfig();
		BirthdayConfig = new Config("birthdays.yml");
		BirthdayConfig.save();
		BirthdayConfig.reload();
		
		getCommand("birthday").setExecutor(new CommandHandler());
		List<String> aliases = new ArrayList<String>();
		aliases.add("bday");
		aliases.add("bd");
		getCommand("birthday").setAliases(aliases);
		getServer().getPluginManager().registerEvents(new BirthdayBoyEventListener(), Plugin);
		serverLog.info("BirthdayBoy started successfully.");
	}
	
	public void onDisable() {
		saveConfig();
		BirthdayConfig.save();
		Plugin = null;
	}
	
	public static FileConfiguration GetConfig() {
		return Plugin.getConfig();
	}
}
