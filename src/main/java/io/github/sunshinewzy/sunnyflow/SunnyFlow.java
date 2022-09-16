package io.github.sunshinewzy.sunnyflow;

import io.github.sunshinewzy.sunnyflow.listener.ChatListener;
import io.github.sunshinewzy.sunnyflow.server.SunnyFlowServer;
import io.github.sunshinewzy.sunnyflow.util.SunnyFlowUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public class SunnyFlow extends JavaPlugin {
	public static final PluginManager manager = Bukkit.getPluginManager();
	
	private static SunnyFlowServer sunnyFlowServer;
	private static String prefix;

	private int port;
	private String password;
	

	@Override
	public void onEnable() {
		if(!loadConfig()) {
			getLogger().info("配置文件加载失败!");
			return;
		}
		getLogger().info("配置文件加载成功!");

		try {
			sunnyFlowServer = new SunnyFlowServer(getLogger(), port, password);
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

	private boolean loadConfig() {
		saveDefaultConfig();
		
		port = getConfig().getInt("port", 25585);
		prefix = getConfig().getString("prefix", "[群消息] ");
		
		ConfigurationSection password = getConfig().getConfigurationSection("password");
		if(password == null) {
			getLogger().info("config.yml 中缺失 password 配置项");
			return false;
		}

		Optional.ofNullable(password.getString("md5"))
				.filter((md5) -> !md5.contentEquals("password"))
				.ifPresent((md5) -> this.password = md5);
		if(this.password != null) return true;

		Optional.ofNullable(password.getString("text"))
				.filter((text) -> !text.contentEquals("password"))
				.ifPresent((text) -> {
					try {
						this.password = SunnyFlowUtil.stringToMD5(text);
					} catch (NoSuchAlgorithmException ex) {
						ex.printStackTrace();
					}
				});
		if(this.password != null) return true;

		getLogger().info("请修改 password 项中的 md5 或 text 项");
		return false;
	}
	

	public static SunnyFlowServer getSunnyFlowServer() {
		return sunnyFlowServer;
	}

	public static String getPrefix() {
		return prefix;
	}
}
