package WeepingAngels;

import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityEggInfo;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import CountryGamer_Core.Items.ItemMetadataBase;
import CountryGamer_Core.lib.CoreUtil;
import WeepingAngels.Blocks.BlockPlinth;
import WeepingAngels.Blocks.BlockWeepingAngelSpawn;
import WeepingAngels.Blocks.TileEnt.TileEntityPlinth;
import WeepingAngels.Client.Gui.GuiHandler;
import WeepingAngels.Entity.EntityStatue;
import WeepingAngels.Entity.EntityWeepingAngel;
import WeepingAngels.Handlers.EventHandler;
import WeepingAngels.Handlers.ServerTickHandler;
import WeepingAngels.Handlers.Packet.PacketHandler;
import WeepingAngels.Items.ItemStatue;
import WeepingAngels.Items.ItemVortex;
import WeepingAngels.Items.ItemWADebug;
import WeepingAngels.Proxy.ServerProxy;
import WeepingAngels.World.WorldGenerator;
import WeepingAngels.World.Structure.ComponentAngelDungeon;
import WeepingAngels.World.Structure.VillageHandlerAngelDungeon;
import WeepingAngels.lib.Reference;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
@NetworkMod(clientSideRequired = true, serverSideRequired = true, channels = {
		"WepAng_statue", "WepAng_teleport", "WepAng_vortex" }, packetHandler = PacketHandler.class)
public class WeepingAngelsMod {

	public static final Logger log = Logger.getLogger("WeepingAngels");
	@Instance(Reference.MOD_ID)
	public static WeepingAngelsMod instance;
	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static ServerProxy proxy;

	// Blocks
	public static Block plinthBlock;
	public static int plinthBlockID;
	public static Block blockWeepingAngelSpawn;
	public static int spawnBlockID;

	// Items
	public static Item statue;
	public static int statueItemID;

	public static final boolean DEBUG = true;
	public static Item debugItem;
	public static int debugItemiD;
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
	public static Item chronon;
	public static int chrononID;
	public static String chrononDustName = "Chronon Dust";
	public static String chrononDiamondName = "Chronon Diamond";
	public static String chrononMetalName = "Chronon Metal";
	public static Item vortexMan;
	public static int vortexManID;
	public static String vortexManName = "Vortex Manipulator";

	// Achievements
	public static Achievement angelAchieve;
	public static int angelAchieveiD;
	public static Achievement angelAchieve2;
	public static int angelAchieve2iD;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();

		Configuration config = new Configuration(
				event.getSuggestedConfigurationFile());
		String general = "General";
		String itemId = "Item IDs", blockId = "Block IDs";
		String achievement = "Achievement IDs";
		String angelStat = "Angel Stats", angelSpawn = "Angel Spawn Options";
		String addon = "Addons";
		config.load(); // load configs from its file

		// Entities
		WeepingAngelsMod.entityWeepingAngelID = CoreUtil.getAndComment(config,
				general, "EntityWeepingAngelID", "", 300);
		// Achievements
		WeepingAngelsMod.angelAchieveiD = CoreUtil.getAndComment(config,
				achievement, "Scared of an Angel ID", "", 10000);
		WeepingAngelsMod.angelAchieve2iD = CoreUtil.getAndComment(config,
				achievement, "Slayed by an Angel ID", "", 10001);
		// Blocks
		WeepingAngelsMod.plinthBlockID = CoreUtil.getAndComment(config,
				blockId, "PlinthBlockID", "", 3023);
		WeepingAngelsMod.spawnBlockID = CoreUtil.getAndComment(config, blockId,
				"SpawnBlockID", "", 3024);
		// Items
		WeepingAngelsMod.statueItemID = CoreUtil.getAndComment(config, itemId,
				"StatueItemID", "", 12034);
		WeepingAngelsMod.chrononID = CoreUtil.getAndComment(config, itemId,
				WeepingAngelsMod.chrononDustName, "", 12035);
		WeepingAngelsMod.vortexManID = CoreUtil.getAndComment(config, itemId,
				WeepingAngelsMod.vortexManName, "", 12036);
		if (WeepingAngelsMod.DEBUG)
			WeepingAngelsMod.debugItemiD = CoreUtil.getAndComment(config,
					itemId, WeepingAngelsMod.debugItemName, "", 12037);

		// Stats
		WeepingAngelsMod.canPoison = CoreUtil.getAndComment(config, angelStat,
				"Angel Can Poison", "", true);
		WeepingAngelsMod.poisonChance = CoreUtil.getAndComment(config,
				angelStat, "Poison Chance Percentage", "", 5);
		WeepingAngelsMod.canTeleport = CoreUtil.getAndComment(config,
				angelStat, "Angel Can Cause Teleport", "", true);
		WeepingAngelsMod.teleportChance = CoreUtil.getAndComment(config,
				angelStat, "Teleport Chance Percentage", "", 20);
		WeepingAngelsMod.teleportRangeMax = CoreUtil.getAndComment(config,
				angelStat, "TeleportRangeMax", "", 60);
		WeepingAngelsMod.attackStrength = CoreUtil.getAndComment(config,
				angelStat, "AttackStrength", "", 6);
		WeepingAngelsMod.pickOnly = CoreUtil.getAndComment(config, angelStat,
				"Hurt Angel with PickAxe only", "", false);
		// Other
		WeepingAngelsMod.spawnRate = CoreUtil.getAndComment(config, angelSpawn,
				"SpawnRate", "", 2);
		WeepingAngelsMod.maxSpawn = CoreUtil.getAndComment(config, angelSpawn,
				"MaxSpawnedPerWorldInstance", "", 2);
		WeepingAngelsMod.maxSpawnHeight = CoreUtil.getAndComment(config,
				angelSpawn, "Max Spawn Y-Level", "", 40);
		// Addons
		WeepingAngelsMod.addonVortex = CoreUtil.getAndComment(config, addon,
				"Enable " + WeepingAngelsMod.vortexManName, "", true);

		config.save();

	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.registerRenderThings();

		this.items();
		this.blocks();
		this.entities();
		this.craftSmelt();

		WeepingAngelsMod.angelAchieve = new Achievement(
				WeepingAngelsMod.angelAchieveiD, "AngelAchieve", -4, -7,
				statue, null).setSpecial().registerAchievement();
		LanguageRegistry.instance().addStringLocalization(
				"achievement.AngelAchieve", "en_US", "Scared of an Angel");
		LanguageRegistry.instance().addStringLocalization(
				"achievement.AngelAchieve.desc", "en_US",
				"The statue is coming. Don't Blink.");

		WeepingAngelsMod.angelAchieve2 = new Achievement(
				WeepingAngelsMod.angelAchieve2iD, "AngelAchieve2", -1, -7,
				statue, WeepingAngelsMod.angelAchieve).setSpecial()
				.registerAchievement();
		LanguageRegistry.instance().addStringLocalization(
				"achievement.AngelAchieve2", "en_US", "Slayed by an Angel");
		LanguageRegistry.instance().addStringLocalization(
				"achievement.AngelAchieve2.desc", "en_US",
				"I'm sorry. I'm so sorry But, you blinked.");

		NetworkRegistry.instance().registerGuiHandler(
				WeepingAngelsMod.instance, new GuiHandler());
		GameRegistry.registerWorldGenerator(new WorldGenerator());
		MapGenStructureIO.func_143031_a(ComponentAngelDungeon.class,
				Reference.MOD_ID + ":AngelDungeon");
		VillagerRegistry.instance().registerVillageCreationHandler(
				new VillageHandlerAngelDungeon());
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		GameRegistry.registerPickupHandler(new EventHandler());
		TickRegistry.registerTickHandler(new ServerTickHandler(), Side.SERVER);
	}

	public void items() {
		statue = (new ItemStatue(statueItemID, Reference.MOD_ID_LOWERCASE,
				"statue", EntityStatue.class)).setUnlocalizedName("Statue")
				.setCreativeTab(CreativeTabs.tabMisc).setMaxStackSize(64);
		// LanguageRegistry.addName(statue, "Weeping Angel Statue");

		if (this.addonVortex) {
			this.chronon();
		}
		if (this.addonVortex)
			this.vortex();

		if (WeepingAngelsMod.DEBUG)
			WeepingAngelsMod.debugItem = new ItemWADebug(
					WeepingAngelsMod.debugItemiD, Reference.MOD_ID,
					WeepingAngelsMod.debugItemName);

	}

	private void chronon() {
		WeepingAngelsMod.chronon = new ItemMetadataBase(
				WeepingAngelsMod.chrononID, Reference.MOD_ID_LOWERCASE,
				new String[] { WeepingAngelsMod.chrononDustName,
						WeepingAngelsMod.chrononDiamondName,
						WeepingAngelsMod.chrononMetalName });
		WeepingAngelsMod.chronon.setCreativeTab(CreativeTabs.tabMaterials);
		// Chronon Diamond
		GameRegistry.addShapelessRecipe(new ItemStack(WeepingAngelsMod.chronon,
				1, 1), new Object[] { Item.diamond,
				new ItemStack(WeepingAngelsMod.chronon, 1, 0),
				new ItemStack(WeepingAngelsMod.chronon, 1, 0) });
		// Chronon Metal
		GameRegistry.addRecipe(new ItemStack(WeepingAngelsMod.chronon, 1, 2),
				new Object[] { "xxx", "ccc", "xxx", 'x', Item.ingotIron, 'c',
						// Chronon Diamond
						new ItemStack(WeepingAngelsMod.chronon, 1, 1) });

	}

	private void vortex() {
		WeepingAngelsMod.vortexMan = new ItemVortex(
				WeepingAngelsMod.vortexManID, Reference.MOD_ID_LOWERCASE,
				WeepingAngelsMod.vortexManName);
		WeepingAngelsMod.vortexMan.setCreativeTab(CreativeTabs.tabTools);
		GameRegistry.addRecipe(new ItemStack(WeepingAngelsMod.vortexMan),
				new Object[] { "xxx", "xcx", "xxx", 'x',
						new ItemStack(WeepingAngelsMod.chronon, 1, 2), 'c',
						Item.eyeOfEnder });
	}

	public void blocks() {
		plinthBlock = (new BlockPlinth(plinthBlockID, TileEntityPlinth.class,
				Material.rock)).setHardness(2.0F).setResistance(10F)
				.setStepSound(Block.soundStoneFootstep)
				.setUnlocalizedName("Plinth");
		GameRegistry.registerBlock(plinthBlock, "Plinth");
		LanguageRegistry.addName(plinthBlock, "Plinth");
		GameRegistry.registerTileEntity(TileEntityPlinth.class,
				"TileEntityPlinth");

		blockWeepingAngelSpawn = new BlockWeepingAngelSpawn(spawnBlockID, 1)
				.setHardness(0.5F).setUnlocalizedName("weepingangelspawn")
				.setCreativeTab(CreativeTabs.tabMisc);
		LanguageRegistry.addName(blockWeepingAngelSpawn,
				"Weeping Angel Spawn Block");
		GameRegistry.registerBlock(blockWeepingAngelSpawn,
				"Weeping Angel Spawn Block");

	}

	public void entities() {
		// Register all entities, blocks and items to game
		// Weeping Angel Entity
		EntityRegistry.registerModEntity(EntityWeepingAngel.class,
				"Weeping Angel", entityWeepingAngelID, this, 80, 3, true);
		EntityList.IDtoClassMapping.put(entityWeepingAngelID,
				EntityWeepingAngel.class);
		EntityList.entityEggs.put(entityWeepingAngelID, new EntityEggInfo(
				entityWeepingAngelID, 0x808080, 0xD1D1D1));
		if (spawnRate > 0) {
			EntityRegistry.addSpawn(EntityWeepingAngel.class, spawnRate, 1,
					maxSpawn, EnumCreatureType.monster, new BiomeGenBase[] {
							BiomeGenBase.iceMountains, BiomeGenBase.icePlains,
							BiomeGenBase.taiga, BiomeGenBase.desert,
							BiomeGenBase.desertHills, BiomeGenBase.plains,
							BiomeGenBase.taiga, BiomeGenBase.taigaHills,
							BiomeGenBase.swampland, BiomeGenBase.beach,
							BiomeGenBase.river, BiomeGenBase.frozenRiver,
							BiomeGenBase.extremeHills,
							BiomeGenBase.extremeHillsEdge });
		}
		LanguageRegistry.instance().addStringLocalization(
				"entity.WeepingAngels.Weeping Angel.name", "Weeping Angel");

	}

	public void craftSmelt() {
		GameRegistry.addRecipe(new ItemStack(
				WeepingAngelsMod.blockWeepingAngelSpawn, 1), new Object[] {
				"xxx", "xcx", "xxx", 'x', Block.stone, 'c',
				WeepingAngelsMod.statue });

	}

	@Mod.EventHandler
	public void onServerStarting(FMLServerStartingEvent event) {
		GameRegistry.registerPlayerTracker(new EventHandler());

	}

}
