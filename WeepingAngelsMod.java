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
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import CountryGamer_Core.ItemBase;
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
	public static Item chrononDust;
	public static int chrononDustID;
	public static String chrononDustName = "Chronon Dust";
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
		WeepingAngelsMod.entityWeepingAngelID = config.get(general,
				"EntityWeepingAngelID", 300).getInt();
		// Achievements
		WeepingAngelsMod.angelAchieveiD = config.get(achievement,
				"Scared of an Angel ID", 10000).getInt();
		WeepingAngelsMod.angelAchieve2iD = config.get(achievement,
				"Slayed by an Angel ID", 10001).getInt();
		// Blocks
		WeepingAngelsMod.plinthBlockID = config.get(blockId, "PlinthBlockID",
				3023).getInt();
		WeepingAngelsMod.spawnBlockID = config.get(blockId, "SpawnBlockID",
				3024).getInt();
		// Items
		WeepingAngelsMod.statueItemID = config.get(itemId, "StatueItemID",
				12034).getInt();
		WeepingAngelsMod.chrononDustID = config.get(itemId,
				WeepingAngelsMod.chrononDustName, 12035).getInt();
		WeepingAngelsMod.vortexManID = config.get(itemId,
				WeepingAngelsMod.vortexManName, 12036).getInt();
		if (WeepingAngelsMod.DEBUG)
			WeepingAngelsMod.debugItemiD = config.get(itemId, debugItemName,
					12037).getInt();
		// Stats
		WeepingAngelsMod.canPoison = config.get(angelStat, "Angel Can Poison",
				true).getBoolean(true);
		WeepingAngelsMod.poisonChance = config.get(angelStat,
				"Poison Chance Percentage", 5).getInt();
		WeepingAngelsMod.canTeleport = config.get(angelStat,
				"Angel Can Cause Teleport", true).getBoolean(true);
		WeepingAngelsMod.teleportChance = config.get(angelStat,
				"Teleport Chance Percentage", 20).getInt();
		WeepingAngelsMod.teleportRangeMax = config.get(angelStat,
				"TeleportRangeMax", 60).getInt();
		WeepingAngelsMod.attackStrength = config.get(angelStat,
				"AttackStrength", 6).getInt();
		WeepingAngelsMod.pickOnly = config.get(angelStat,
				"Hurt Angel with PickAxe only", false).getBoolean(false);
		// Other
		WeepingAngelsMod.spawnRate = config.get(angelSpawn, "SpawnRate", 2)
				.getInt();
		WeepingAngelsMod.maxSpawn = config.get(angelSpawn,
				"MaxSpawnedPerWorldInstance", 2).getInt();
		WeepingAngelsMod.maxSpawnHeight = config.get(angelSpawn,
				"Max Spawn Y-Level", 40).getInt();
		// Addons
		WeepingAngelsMod.addonVortex = config.get(addon,
				"Enable " + WeepingAngelsMod.vortexManName, true).getBoolean(
				false);

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
		
		if (this.addonVortex)
			this.vortexItems();
		
		if (WeepingAngelsMod.DEBUG)
			WeepingAngelsMod.debugItem = new ItemWADebug(
					WeepingAngelsMod.debugItemiD, Reference.MOD_ID,
					WeepingAngelsMod.debugItemName);

	}

	public void vortexItems() {
		WeepingAngelsMod.chrononDust = new ItemBase(
				WeepingAngelsMod.chrononDustID, Reference.MOD_ID_LOWERCASE,
				WeepingAngelsMod.chrononDustName);
		WeepingAngelsMod.chrononDust.setCreativeTab(CreativeTabs.tabMaterials);

		WeepingAngelsMod.vortexMan = new ItemVortex(
				WeepingAngelsMod.vortexManID, Reference.MOD_ID_LOWERCASE,
				WeepingAngelsMod.vortexManName);
		WeepingAngelsMod.vortexMan.setCreativeTab(CreativeTabs.tabTools);
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
		GameRegistry
				.addRecipe(new ItemStack(WeepingAngelsMod.vortexMan),
						new Object[] { "vcv", "cxc", "vcv", 'x',
								Item.pocketSundial, 'c', Item.ingotIron, 'v',
								WeepingAngelsMod.chrononDust });
	}

	@Mod.EventHandler
	public void onServerStarting(FMLServerStartingEvent event) {
		GameRegistry.registerPlayerTracker(new EventHandler());

	}

}
