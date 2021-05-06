package net.iamvishnu.BirthdayBoy;

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
		BirthdayConfig.reload();
		
		CommandHandler commander = new CommandHandler();
		getCommand("birthday").setExecutor(commander);
		getServer().getPluginManager().registerEvents(new BirthdayBoyEventListener(), Plugin);
		serverLog.info("BirthdayBoy started successfully.");
	}
	
	@Override
	public void onDisable() {
		saveConfig();
		BirthdayConfig.save();
		Plugin = null;
	}
	
	public static FileConfiguration GetConfig() {
		return Plugin.getConfig();
	}
}
