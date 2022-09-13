package io.github.sunshinewzy.sunnyflow;

import io.github.sunshinewzy.sunnyflow.listener.ChatListener;
import io.github.sunshinewzy.sunnyflow.server.SunnyFlowServer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SunnyFlow extends JavaPlugin {
	public static final PluginManager manager = Bukkit.getPluginManager();
	private static SunnyFlowServer sunnyFlowServer;
	

	@Override
	public void onEnable() {
		ConfigurationSection password = getConfig().getConfigurationSection("password");
		if(password == null) {
			getLogger().info("config.yml 中缺失 password 配置项");
			return;
		}
		
		String text = password.getString("text");
		String md5 = password.getString("md5");

		try {
			sunnyFlowServer = new SunnyFlowServer(getLogger(), 25585);
			sunnyFlowServer.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		registerEvents();
	}

	@Override
	public void onDisable() {
		
	}
	
	
	private void registerEvents() {
		manager.registerEvents(new ChatListener(), this);
	}


	public static SunnyFlowServer getSunnyFlowServer() {
		return sunnyFlowServer;
	}
}
