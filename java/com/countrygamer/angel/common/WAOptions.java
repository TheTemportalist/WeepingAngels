package com.countrygamer.angel.common;

import net.minecraftforge.common.config.Configuration;

import com.countrygamer.core.Base.Plugin.PluginOptionRegistry;
import com.countrygamer.countrygamercore.lib.CoreUtil;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class WAOptions implements PluginOptionRegistry {
	
	public static int		maxSpawnPerInstance_Angel;
	public static int		spawnRate_Angel;
	public static int		maxSpawnHeight_Angel;
	public static double	maxHealth_Angel	= 20.0D;
	public static int		attackStrength_Angel;
	public static boolean	angelCanTeleportTarget;
	public static int		teleportationChance;
	public static int		teleportationMaxRange;
	public static boolean	angelCanPoisonTarget;
	public static int		poisonChance;
	public static int		totalPoisonTicks;
	public static boolean	angelOnlyHurtWithPickaxe;
	
	public static boolean	addonVortexActive;
	public static boolean	addonSonicActive;
	
	@Override
	public boolean hasCustomConfiguration() {
		return false;
	}
	
	@Override
	public void customizeConfiguration(FMLPreInitializationEvent event) {
		
	}
	
	@Override
	public void registerOptions(Configuration config) {
		String angelStat = "Weeping Angel Statistics";
		String addon = "Mod Addons";
		
		WAOptions.angelCanPoisonTarget = CoreUtil.getAndComment(config, angelStat,
				"Angel Can Poison", "When attacked can the angel start the angel conversion. "
						+ "Overwrites 'Poison Chance Percentage' if false.", true);
		WAOptions.poisonChance = CoreUtil.getAndComment(config, angelStat,
				"Poison Chance Percentage", "Out of 100, what percentage of attacks can an "
						+ "angel start an angel conversion. Default 5%", 5);
		WAOptions.angelCanTeleportTarget = CoreUtil.getAndComment(config, angelStat,
				"Angel Can Cause Teleport", "Determines if an angel can teleport the player. "
						+ "If false, will override all other " + "teleportation of player stats.",
				true);
		WAOptions.teleportationChance = CoreUtil.getAndComment(config, angelStat,
				"Teleport Chance Percentage", "Out of 100, what percentage of attacks can an "
						+ "angel teleport the player. Default 20%", 20);
		WAOptions.teleportationMaxRange = CoreUtil.getAndComment(config, angelStat,
				"Teleport Range (Maximum)", "Maximum number of blocks an angel can "
						+ "teleport the player. Default 60", 60);
		WAOptions.attackStrength_Angel = CoreUtil.getAndComment(config, angelStat,
				"Attack Strength", "How many half hearts of damage the "
						+ "angel can deal. Default 6 (3 hearts)", 6);
		WAOptions.angelOnlyHurtWithPickaxe = CoreUtil.getAndComment(config, angelStat,
				"Hurt Angel with PickAxe only", "", true);
		// Other
		WAOptions.spawnRate_Angel = CoreUtil.getAndComment(config, angelStat, "Spawn Frequency",
				"If you make this higher it will spawn more often. Default 2", 2);
		WAOptions.maxSpawnPerInstance_Angel = CoreUtil.getAndComment(config, angelStat,
				"Max per group spawn", "When the mob has been chosen to be spawned there will "
						+ "spawn between 1 and X of them. Default 2", 2);
		WAOptions.maxSpawnHeight_Angel = CoreUtil.getAndComment(config, angelStat,
				"Max Spawn Y-Level", "The maximum height at which angels can spawn.", 40);
		// Addons
		WAOptions.addonVortexActive = CoreUtil.getAndComment(config, addon,
				"Enable Vortex Manipulator", "Enable the add-on for the Vortex Manipulator.", true);
		WAOptions.addonSonicActive = CoreUtil.getAndComment(config, addon,
				"Enable Sonic Screwdriver", "Enable the add-on for the Sonic Screwdriver.", true);
	}
	
}
