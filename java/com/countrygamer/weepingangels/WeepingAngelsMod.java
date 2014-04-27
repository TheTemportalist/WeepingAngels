package com.countrygamer.weepingangels;

import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.countrygamer.core.Base.common.item.ItemMetadataBase;
import com.countrygamer.core.Base.common.packet.PacketPipeline;
import com.countrygamer.core.common.Core;
import com.countrygamer.core.common.lib.CoreUtil;
import com.countrygamer.weepingangels.Blocks.BlockPlinth;
import com.countrygamer.weepingangels.Blocks.BlockWeepingAngelSpawn;
import com.countrygamer.weepingangels.Blocks.TileEnt.TileEntityPlinth;
import com.countrygamer.weepingangels.Client.Gui.GuiHandler;
import com.countrygamer.weepingangels.Entity.EntityStatue;
import com.countrygamer.weepingangels.Entity.EntityWeepingAngel;
import com.countrygamer.weepingangels.Handlers.EventHandler;
import com.countrygamer.weepingangels.Handlers.Packet.PacketStoreCoords;
import com.countrygamer.weepingangels.Items.ItemSonic;
import com.countrygamer.weepingangels.Items.ItemStatue;
import com.countrygamer.weepingangels.Items.ItemVortex;
import com.countrygamer.weepingangels.Items.ItemWADebug;
import com.countrygamer.weepingangels.World.WorldGenerator;
import com.countrygamer.weepingangels.lib.Reference;
import com.countrygamer.weepingangels.proxy.ServerProxy;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class WeepingAngelsMod {

	public static final Logger log = Logger.getLogger("WeepingAngels");
	@Instance(Reference.MOD_ID)
	public static WeepingAngelsMod instance;
	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static ServerProxy proxy;

	// Packet
	public static final PacketPipeline packetChannel = new PacketPipeline();

	// Blocks
	public static Block plinthBlock;
	public static Block blockWeepingAngelSpawn;

	// Items
	public static Item statue;
	public static Item debugItem;
	public static String debugItemName = "Debugger";

	// Entity
	public static int entityWeepingAngelID;
	public static int maxSpawn;
	public static int spawnRate;
	public static int maxSpawnHeight;
	public static double maxHealth = 20.0D;
	public static int attackStrength;
	public static boolean canTeleport;
	public static int teleportChance;
	public static int teleportRangeMax;
	public static boolean canPoison;
	public static int poisonChance;
	public static int totalConvertTicks = 20 * 60 * 2;
	public static boolean pickOnly;

	// Addons
	public static boolean addonVortex;
	public static boolean addonSonic;
	public static Item chronon;
	public static String chrononDustName = "Chronon Dust";
	public static String chrononDiamondName = "Chronon Diamond";
	public static String chrononMetalName = "Chronon Metal";
	public static Item vortexMan;
	public static String vortexManName = "Vortex Manipulator";
	public static Item sonicScrew;
	public static String sonicScrewName = "Sonic Screwdriver";

	// Achievements
	public static Achievement angelAchieve1;
	public static Achievement angelAchieve2;
	public static Achievement angelAchieve3;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();
		this.config(event);
		this.handlers();

		proxy.registerRenderThings();
		this.items();
		this.blocks();
		this.entities();
		this.craftSmelt();

		this.achievements();

	}

	private void config(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(
				event.getSuggestedConfigurationFile());
		//String general = "General";
		//String achievement = "Achievement IDs";
		String angelStat = "Angel Stats", angelSpawn = "Angel Spawn Options";
		String addon = "Addons";
		config.load(); // load configs from its file

		// Stats
		WeepingAngelsMod.canPoison = CoreUtil.getAndComment(config, angelStat,
				"Angel Can Poison",
				"When attacked can the angel start the angel conversion. "
						+ "Overwrites 'Poison Chance Percentage' if false.",
				true);
		WeepingAngelsMod.poisonChance = CoreUtil.getAndComment(config,
				angelStat, "Poison Chance Percentage",
				"Out of 100, what percentage of attacks can an "
						+ "angel start an angel conversion. Default 5%", 5);
		WeepingAngelsMod.canTeleport = CoreUtil.getAndComment(config,
				angelStat, "Angel Can Cause Teleport",
				"Determines if an angel can teleport the player. "
						+ "If false, will override all other "
						+ "teleportation of player stats.", true);
		WeepingAngelsMod.teleportChance = CoreUtil.getAndComment(config,
				angelStat, "Teleport Chance Percentage",
				"Out of 100, what percentage of attacks can an "
						+ "angel teleport the player. Default 20%", 20);
		WeepingAngelsMod.teleportRangeMax = CoreUtil.getAndComment(config,
				angelStat, "Teleport Range (Maximum)",
				"Maximum number of blocks an angel can "
						+ "teleport the player. Default 60", 60);
		WeepingAngelsMod.attackStrength = CoreUtil.getAndComment(config,
				angelStat, "Attack Strength",
				"How many half hearts of damage the "
						+ "angel can deal. Default 6 (3 hearts)", 6);
		WeepingAngelsMod.pickOnly = CoreUtil.getAndComment(config, angelStat,
				"Hurt Angel with PickAxe only", "", true);
		// Other
		WeepingAngelsMod.spawnRate = CoreUtil.getAndComment(config, angelSpawn,
				"Spawn Frequency",
				"If you make this higher it will spawn more often. Default 2",
				2);
		WeepingAngelsMod.maxSpawn = CoreUtil.getAndComment(config, angelSpawn,
				"Max per group spawn",
				"When the mob has been chosen to be spawned there will "
						+ "spawn between 1 and X of them. Default 2", 2);
		WeepingAngelsMod.maxSpawnHeight = CoreUtil.getAndComment(config,
				angelSpawn, "Max Spawn Y-Level",
				"The maximum height at which angels can spawn.", 40);
		// Addons
		WeepingAngelsMod.addonVortex = CoreUtil.getAndComment(config, addon,
				"Enable " + WeepingAngelsMod.vortexManName,
				"Enable the add-on for the Vortex Manipulator.", true);
		WeepingAngelsMod.addonSonic = CoreUtil.getAndComment(config, addon,
				"Enable " + WeepingAngelsMod.sonicScrewName,
				"Enable the add-on for the Sonic Screwdriver.", true);

		config.save();
	}

	private void achievements() {
		String angelAchieve = "";

		angelAchieve = "AngelAchieve1";
		WeepingAngelsMod.angelAchieve1 = new Achievement(angelAchieve,
				angelAchieve, 9, 1, statue, AchievementList.killEnemy)
				.setSpecial().registerStat();

		angelAchieve = "AngelAchieve2";
		WeepingAngelsMod.angelAchieve2 = new Achievement(angelAchieve,
				angelAchieve, 8, 3, statue, WeepingAngelsMod.angelAchieve1)
				.setSpecial().registerStat();

		angelAchieve = "AngelAchieve3";
		WeepingAngelsMod.angelAchieve3 = new Achievement(angelAchieve,
				angelAchieve, 10, 3, statue, WeepingAngelsMod.angelAchieve1)
				.setSpecial().registerStat();

	}

	private void handlers() {
		GameRegistry.registerWorldGenerator(new WorldGenerator(), 0);
		NetworkRegistry.INSTANCE.registerGuiHandler(WeepingAngelsMod.instance,
				new GuiHandler());
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}

	public void items() {
		WeepingAngelsMod.statue = (new ItemStatue(Reference.MOD_ID,
				"Weeping Angel Statue", EntityStatue.class)).setCreativeTab(
				CreativeTabs.tabMisc).setMaxStackSize(64);
		Core.addItemToTab(WeepingAngelsMod.statue);

		if (WeepingAngelsMod.addonVortex || WeepingAngelsMod.addonSonic) {
			this.chronon();
		}
		if (WeepingAngelsMod.addonVortex)
			this.vortex();
		if (WeepingAngelsMod.addonSonic)
			this.sonic();

		if (Core.DEBUG) {
			WeepingAngelsMod.debugItem = new ItemWADebug(Reference.MOD_ID,
					WeepingAngelsMod.debugItemName);
			Core.addItemToTab(WeepingAngelsMod.debugItem);
		}

	}

	private void chronon() {
		WeepingAngelsMod.chronon = new ItemMetadataBase(Reference.MOD_ID,
				new String[] { WeepingAngelsMod.chrononDustName,
						WeepingAngelsMod.chrononDiamondName,
						WeepingAngelsMod.chrononMetalName });
		Core.addItemToTab(WeepingAngelsMod.chronon);
		// Chronon Diamond
		GameRegistry.addShapelessRecipe(new ItemStack(WeepingAngelsMod.chronon,
				1, 1), new Object[] { Items.diamond,
				new ItemStack(WeepingAngelsMod.chronon, 1, 0),
				new ItemStack(WeepingAngelsMod.chronon, 1, 0) });
		// Chronon Metal
		GameRegistry.addRecipe(new ItemStack(WeepingAngelsMod.chronon, 1, 2),
				new Object[] { "xxx", "ccc", "xxx", 'x', Items.iron_ingot, 'c',
						// Chronon Diamond
						new ItemStack(WeepingAngelsMod.chronon, 1, 1) });

	}

	private void vortex() {
		WeepingAngelsMod.vortexMan = new ItemVortex(Reference.MOD_ID,
				WeepingAngelsMod.vortexManName);
		Core.addItemToTab(WeepingAngelsMod.vortexMan);
		GameRegistry.addRecipe(new ItemStack(WeepingAngelsMod.vortexMan),
				new Object[] { "xxx", "xcx", "xxx", 'x',
						new ItemStack(WeepingAngelsMod.chronon, 1, 2), 'c',
						Items.ender_eye });
	}

	private void sonic() {
		WeepingAngelsMod.sonicScrew = new ItemSonic(Reference.MOD_ID,
				WeepingAngelsMod.sonicScrewName);
		Core.addItemToTab(WeepingAngelsMod.sonicScrew);
		GameRegistry.addRecipe(new ItemStack(WeepingAngelsMod.sonicScrew),
				new Object[] { " ge", "lig", "rl ", 'g', Items.gold_ingot, 'e',
						Items.emerald, 'l', Items.leather, 'i',
						Items.iron_ingot, 'r', Items.redstone });
	}

	public void blocks() {
		TileEntity.addMapping(TileEntityPlinth.class, Reference.MOD_ID + "_Plinth");
		plinthBlock = new BlockPlinth(TileEntityPlinth.class, Material.rock);
		plinthBlock.setHardness(2.0F).setResistance(10F);
		plinthBlock.setStepSound(Block.soundTypeStone);
		plinthBlock.setBlockName("Plinth");
		GameRegistry.registerBlock(plinthBlock, "Plinth");

		blockWeepingAngelSpawn = new BlockWeepingAngelSpawn().setHardness(0.5F);
		blockWeepingAngelSpawn.setBlockName("weepingangelspawn");
		Core.addBlockToTab(WeepingAngelsMod.blockWeepingAngelSpawn);
		GameRegistry.registerBlock(blockWeepingAngelSpawn,
				"Weeping Angel Spawn Block");

	}

	@SuppressWarnings("unchecked")
	public void entities() {
		// Register all entities, blocks and items to game
		// Weeping Angel Entity
		// ~ class, nameToBeLocalized, id per mod, mod instance,
		// ~ amount of blocks before despawn, tracking rate,
		// ~ send velocity updates
		EntityRegistry.registerModEntity(EntityWeepingAngel.class,
				"Weeping Angel", 1, WeepingAngelsMod.instance, 80, 3, false);
		EntityList.IDtoClassMapping.put(entityWeepingAngelID,
				EntityWeepingAngel.class);
		EntityList.entityEggs.put(entityWeepingAngelID, new EntityEggInfo(
				entityWeepingAngelID, 0x808080, 0xD1D1D1));
		if (spawnRate > 0) {
			for (int i = 0; i < BiomeGenBase.getBiomeGenArray().length; i++) {
				if (BiomeGenBase.getBiomeGenArray()[i] != null) {
					// ~ mob file, spawn frequency, group size,
					// ~ creature type, biome
					EntityRegistry.addSpawn(EntityWeepingAngel.class,
							spawnRate, 1, maxSpawn, EnumCreatureType.monster,
							BiomeGenBase.getBiomeGenArray()[i]);
				}
			}
		}
		// LanguageRegistry.instance().addStringLocalization(
		// "entity.WeepingAngels.Weeping Angel.name", "Weeping Angel");

	}

	public void craftSmelt() {
		GameRegistry.addRecipe(new ShapedOreRecipe(
				WeepingAngelsMod.blockWeepingAngelSpawn, new Object[] { "xxx",
						"xcx", "xxx", Character.valueOf('x'), "stone",
						Character.valueOf('c'), WeepingAngelsMod.statue }));
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		WeepingAngelsMod.packetChannel.initalise(Reference.MOD_ID);
		WeepingAngelsMod.packetChannel.registerPacket(PacketStoreCoords.class);
		this.iChun_Morph();
	}

	private void iChun_Morph() {
		/*
		 * if (CG_Core.isMorphLoaded()) {
		 * morph.api.Ability.registerAbility("timelock",
		 * MorphAbilityTimeLock.class);
		 * 
		 * morph.api.Ability.mapAbilities(EntityWeepingAngel.class, new
		 * MorphAbilityTimeLock(), new morph.common.ability.AbilityStep(3.0F),
		 * new morph.common.ability.AbilityHostile(), new
		 * morph.common.ability.AbilityFireImmunity()); }
		 */
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		WeepingAngelsMod.packetChannel.postInitialise();
	}

}
