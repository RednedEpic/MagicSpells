package com.nisovin.magicjutsus.util;

import java.io.File;
import java.util.Set;
import java.util.List;
import java.io.FilenameFilter;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.nisovin.magicjutsus.MagicJutsus;

public class MagicConfig {
	
	private static final FilenameFilter FILENAME_FILTER = (File dir, String name) -> name.startsWith("jutsu") && name.endsWith(".yml");
	private static final FilenameFilter DIRECTORY_FILTER = (File dir, String name) -> name.startsWith("jutsus");

	private YamlConfiguration mainConfig;
	
	public MagicConfig(File file) {
		this(MagicJutsus.plugin);
	}
	
	/*
	 * general outline of the format of this config (WIP)
	 * general:
	 *     enable-volatile-features: true
	 *     debug: false
	 *     debug-null: true
	 *     debug-number-format: true
	 *     debug-level: 3
	 *     enable-error-logging: true
	 *     enable-profiling: false
	 *     text-color: ChatColor.DARK_AQUA.getChar()
	 *     broadcast-range: 20
	 *     ops-have-all-jutsus: true
	 *     default-all-perms-false: false
	 *     ignore-grant-perms: false
	 *     ignore-cast-perms: false
	 *     separate-player-jutsus-per-world: false
	 *     allow-cycle-to-no-jutsu: false
	 *     always-show-message-on-cycle: false
	 *     only-cycle-to-castable-jutsus: true
	 *     jutsu-icon-slot: -1
	 *     allow-cast-with-fist: false
	 *     cast-with-left-click: true
	 *     cast-with-right-click: false
	 *     ignore-default-bindings: false
	 *     ignore-cast-item-enchants: true
	 *     ignore-cast-item-names: false
	 *     ignore-cast-item-name-colors: false
	 *     check-world-pvp-flag: true
	 *     check-scoreboard-teams: false
	 *     show-str-cost-on-missing-reagents: true
	 *     los-transparent-blocks: new ArrayList<Byte>()
	 *     ignore-cast-item-durability: new ArrayList<Integer>()
	 *     global-cooldown: 500
	 *     cast-on-animate: false
	 *     use-exp-bar-as-cast-time-bar: true
	 *     cooldowns-persist-through-reload: true
	 *     entity-names:
	 *     sound-on-cooldown: null
	 *     sound-missing-reagents: null
	 *     str-cast-usage: "Usage: /cast <jutsu>. Use /cast list to see a list of jutsus."
	 *     str-unknown-jutsu: "You do not know a jutsu with that name."
	 *     str-jutsu-change: "You are now using the %s jutsu."
	 *     str-jutsu-change-empty: "You are no longer using a jutsu."
	 *     str-on-cooldown: "That jutsu is on cooldown."
	 *     str-missing-reagents: "You do not have the reagents for that jutsu."
	 *     str-cant-cast: "You can't cast that jutsu right now."
	 *     str-cant-bind: "You cannot bind that jutsu to that item."
	 *     str-wrong-world: "You cannot cast that jutsu here."
	 *     console-name: "Admin"
	 *     str-xp-auto-learned: "You have learned the %s jutsu!"
	 *     buff-check-interval: 0
	 *     ops-ignore-reagents: true
	 *     ops-ignore-cooldowns: true
	 *     ops-ignore-cast-times: true
	 *     hide-predefined-items-tooltips: false
	 *     enable-magic-xp: false
	 *     enable-dance-casting: true
	 *     enable-logging: false
	 *     enable-tab-completion: true
	 *     
	 *     
	 *     predefined-items:
	 *     variables:
	 *     modifiers:
	 * mana:
	 *     enable-mana-system: false
	 *     mana-potion-cooldown: 30
	 *     str-mana-potion-on-cooldown: "You cannot use another mana potion yet."
	 *     mana-potions: null
	 *     
	 * jutsus:
	 * no-magic-zones:
	 */
	public MagicConfig(MagicJutsus plugin) {
		try {
			File folder = plugin.getDataFolder();
			File file = new File(folder, "config.yml");
			
			// Load main config
			mainConfig = new YamlConfiguration();
			if (file.exists()) mainConfig.load(file);
			if (!mainConfig.contains("general")) mainConfig.createSection("general");
			if (!mainConfig.contains("mana")) mainConfig.createSection("mana");
			if (!mainConfig.contains("jutsus")) mainConfig.createSection("jutsus");
			
			// Load general
			File generalConfigFile = new File(folder, "general.yml");
			if (generalConfigFile.exists()) {
				YamlConfiguration generalConfig = new YamlConfiguration();
				try {
					generalConfig.load(generalConfigFile);
					Set<String> keys = generalConfig.getKeys(true);
					for (String key : keys) {
						mainConfig.set("general." + key, generalConfig.get(key));
					}
				} catch (Exception e) {
					MagicJutsus.error("Error loading config file general.yml");
					MagicJutsus.handleException(e);
				}
			}
			
			// Load mana
			File manaConfigFile = new File(folder, "mana.yml");
			if (manaConfigFile.exists()) {
				YamlConfiguration manaConfig = new YamlConfiguration();
				try {
					manaConfig.load(manaConfigFile);
					Set<String> keys = manaConfig.getKeys(true);
					for (String key : keys) {
						mainConfig.set("mana." + key, manaConfig.get(key));
					}
				} catch (Exception e) {
					MagicJutsus.error("Error loading config file mana.yml");
					MagicJutsus.handleException(e);
				}
			}
			
			// Load no magic zones
			File zonesConfigFile = new File(folder, "zones.yml");
			if (zonesConfigFile.exists()) {
				YamlConfiguration zonesConfig = new YamlConfiguration();
				try {
					zonesConfig.load(zonesConfigFile);
					Set<String> keys = zonesConfig.getKeys(true);
					for (String key : keys) {
						mainConfig.set("no-magic-zones." + key, zonesConfig.get(key));
					}
				} catch (Exception e) {
					MagicJutsus.error("Error loading config file zones.yml");
					MagicJutsus.handleException(e);
				}
			}
			
			// Load jutsu folders
			for (File directoryFile : folder.listFiles(DIRECTORY_FILTER)) {
				if (!directoryFile.isDirectory()) continue;
				for (File jutsuConfigFile : directoryFile.listFiles(FILENAME_FILTER)) {
					loadJutsuFiles(jutsuConfigFile);
				}
			}

			// load jutsu configs
			for (File jutsuConfigFile : folder.listFiles(FILENAME_FILTER)) {
				loadJutsuFiles(jutsuConfigFile);
			}
			
			// Load mini configs
			File jutsuConfigsFolder = new File(folder, "jutsuconfigs");
			if (jutsuConfigsFolder.exists()) loadJutsuConfigs(jutsuConfigsFolder);
		} catch (Exception ex) {
			MagicJutsus.handleException(ex);
		}
	}

	private void loadJutsuFiles(File jutsuConfigFile) {
		YamlConfiguration jutsuConfig = new YamlConfiguration();
		try {
			jutsuConfig.load(jutsuConfigFile);
			Set<String> keys = jutsuConfig.getKeys(false);

			// TODO this should be refactored to allow registration of additional "special sections"
			for (String key : keys) {
				if (key.equals("predefined-items")) {
					ConfigurationSection sec = mainConfig.getConfigurationSection("general.predefined-items");
					if (sec == null) sec = mainConfig.createSection("general.predefined-items");
					for (String itemKey : jutsuConfig.getConfigurationSection("predefined-items").getKeys(false)) {
						sec.set(itemKey, jutsuConfig.get("predefined-items." + itemKey));
					}
				} else if (key.equals("variables")) {
					ConfigurationSection sec = mainConfig.getConfigurationSection("general.variables");
					if (sec == null) sec = mainConfig.createSection("general.variables");
					for (String itemKey : jutsuConfig.getConfigurationSection("variables").getKeys(false)) {
						sec.set(itemKey, jutsuConfig.get("variables." + itemKey));
					}
				} else if (key.equals("modifiers")) {
					ConfigurationSection sec = mainConfig.getConfigurationSection("general.modifiers");
					if (sec == null) sec = mainConfig.createSection("general.modifiers");
					for (String modifierKey : jutsuConfig.getConfigurationSection("modifiers").getKeys(false)) {
						sec.set(modifierKey, jutsuConfig.get("modifiers." + modifierKey));
					}
				} else {
					mainConfig.set("jutsus." + key, jutsuConfig.get(key));
				}
			}
		} catch (Exception e) {
			MagicJutsus.error("Error loading config file " + jutsuConfigFile.getName());
			MagicJutsus.handleException(e);
		}
	}
	
	private void loadJutsuConfigs(File folder) {
		YamlConfiguration conf;
		String name;
		File[] files = folder.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				// Recurse into folders
				loadJutsuConfigs(file);
			} else if (file.getName().endsWith(".yml")) {
				name = file.getName().replace(".yml", "");
				conf = new YamlConfiguration();
				try {
					conf.load(file);
					for(String key : conf.getKeys(false)) {
						mainConfig.set("jutsus." + name + '.' + key, conf.get(key));
					}
				} catch (Exception e) {
					MagicJutsus.error("Error reading jutsu config file: " + file.getName());
					e.printStackTrace();
				}
			}
		}
	}
	
	public boolean isLoaded() {
		return mainConfig.contains("general") && mainConfig.contains("jutsus");
	}
	
	public boolean contains(String path) {
		return mainConfig.contains(path);
	}
	
	public int getInt(String path, int def) {
		return mainConfig.getInt(path, def);
	}
	
	public long getLong(String path, long def) {
		return mainConfig.getLong(path, def);
	}
	
	public double getDouble(String path, double def) {
		if (mainConfig.contains(path) && mainConfig.isInt(path)) return mainConfig.getInt(path);
		return mainConfig.getDouble(path, def);
	}
	
	public boolean getBoolean(String path, boolean def) {		
		return mainConfig.getBoolean(path, def);
	}
	
	public boolean isString(String path) {
		return mainConfig.contains(path) && mainConfig.isString(path);
	}
	
	public String getString(String path, String def) {
		if (!mainConfig.contains(path)) return def;
		return mainConfig.get(path).toString();
	}
	
	public boolean isList(String path) {
		return mainConfig.contains(path) && mainConfig.isList(path);
	}
	
	public List<Integer> getIntList(String path, List<Integer> def) {
		if (!mainConfig.contains(path)) return def;
		List<Integer> l = mainConfig.getIntegerList(path);
		if (l != null) return l;
		return def;
	}
	
	public List<Byte> getByteList(String path, List<Byte> def) {
		if (!mainConfig.contains(path)) return def;
		List<Byte> l = mainConfig.getByteList(path);
		if (l != null) return l;
		return def;
	}
	
	public List<String> getStringList(String path, List<String> def) {
		if (!mainConfig.contains(path)) return def;
		List<String> l = mainConfig.getStringList(path);
		if (l != null) return l;
		return def;
	}
	
	public List<?> getList(String path, List<?> def) {
		if (!mainConfig.contains(path)) return def;
		List<?> l = mainConfig.getList(path);
		if (l != null) return l;
		return def;
	}
	
	public Set<String> getKeys(String path) {
		if (!mainConfig.contains(path)) return null;
		if (!mainConfig.isConfigurationSection(path)) return null;
		return mainConfig.getConfigurationSection(path).getKeys(false);
	}
	
	public boolean isSection(String path) {
		return mainConfig.contains(path) && mainConfig.isConfigurationSection(path);
	}
	
	public ConfigurationSection getSection(String path) {
		if (mainConfig.contains(path)) return mainConfig.getConfigurationSection(path);
		return null;
	}
	
	public Set<String> getJutsuKeys() {
		if (mainConfig == null) return null;
		if (!mainConfig.contains("jutsus")) return null;
		if (!mainConfig.isConfigurationSection("jutsus")) return null;
		return mainConfig.getConfigurationSection("jutsus").getKeys(false);
	}
	
}