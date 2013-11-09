package WeepingAngels;

import java.lang.reflect.Field;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityEggInfo;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.src.ModLoader;
import net.minecraft.util.EnumArt;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.EnumHelper;
import net.minecraftforge.common.MinecraftForge;
import WeepingAngels.Blocks.BlockPlinth;
import WeepingAngels.Blocks.BlockWeepingAngelSpawn;
import WeepingAngels.Blocks.TileEnt.TileEntityPlinth;
import WeepingAngels.Entity.EntityStatue;
import WeepingAngels.Entity.EntityWAPainting;
import WeepingAngels.Entity.EntityWeepingAngel;
import WeepingAngels.Handlers.EventHandler;
import WeepingAngels.Items.ItemStatue;
import WeepingAngels.Items.ItemWeepPaint;
import WeepingAngels.Proxy.PacketHandler;
import WeepingAngels.Proxy.ServerProxy;
import WeepingAngels.lib.Reference;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod( modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
@NetworkMod(clientSideRequired=true, serverSideRequired=false, channels = {"statue"}, packetHandler = PacketHandler.class)
public class WeepingAngelsMod  {

	public static final Logger log = Logger.getLogger("WeepingAngels");
	
	public static Block	plinthBlock;
	public static Block blockWeepingAngelSpawn;
	public static Item statue;

	public static int spawnRate;
	public static int maxSpawn;
	public static int attackStrength;
	public static int teleportChance;
	public static int poisonChance;
	public static int maxSpawnHeight;
	
	public static int teleportRangeMin;
	public static int teleportRangeMax;
	public static int plinthBlockID;
	public static int spawnBlockID;
	public static int statueItemID;
	public static int entityWeepingAngelID;
	public static int entityWAPaintingID;

	public static Item waPaint;
	public static int waPaint_ID;
	public static boolean waP_Enable = true;

	public static final boolean DEBUG = true;
	public static boolean pickOnly;
	public static boolean worldSpawnAngels = true;

	public static Potion angelConvert;

	@Instance(Reference.MOD_ID)
	public static WeepingAngelsMod instance;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS,
			serverSide = Reference.SERVER_PROXY_CLASS)
	public static ServerProxy proxy;

	public static final String wapNAME = "WeepingAngelArt";
	/*
	EntityPainting wap = new EntityPainting(
			world, 0, 0, 0, 0, "WeepingAngelArt");
	 */
	public static EnumArt waa = EnumHelper.addArt(
			WeepingAngelsMod.wapNAME.toLowerCase(), WeepingAngelsMod.wapNAME, 16, 16, 0, 0);


	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();

		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load(); // load configs from its file

		entityWeepingAngelID = config.get(Configuration.CATEGORY_GENERAL,	"EntityWeepingAngelID",	300).getInt();
		entityWAPaintingID	= config.get(Configuration.CATEGORY_GENERAL,	"EntityWAPaintingID",	301).getInt();
		statueItemID = config.get(Configuration.CATEGORY_ITEM, "StatueItemID", 12034).getInt();
		plinthBlockID = config.get(Configuration.CATEGORY_BLOCK, "PlinthBlockID", 3023).getInt();
		spawnBlockID = config.get(Configuration.CATEGORY_BLOCK, "SpawnBlockID", 3024).getInt();
		
		attackStrength = config.get(Configuration.CATEGORY_GENERAL,
				"AttackStrength",			2).getInt();
		WeepingAngelsMod.poisonChance = config.get(Configuration.CATEGORY_GENERAL,
				"Poison Chance Percentage",		5).getInt();
		teleportChance = config.get(Configuration.CATEGORY_GENERAL,
				"Teleport Chance Percentage",	20).getInt();
		teleportRangeMin = config.get(Configuration.CATEGORY_GENERAL,
				"TeleportRangeMin",			0).getInt();
		teleportRangeMax = config.get(Configuration.CATEGORY_GENERAL,
				"TeleportRangeMax",			100).getInt();
		maxSpawn = config.get(Configuration.CATEGORY_GENERAL,
				"MaxSpawnedPerInstance",	2).getInt();
		spawnRate = config.get(Configuration.CATEGORY_GENERAL,
				"SpawnRate",				2).getInt();
		maxSpawnHeight = config.get(Configuration.CATEGORY_GENERAL,
				"Max Spawn Y-Level",		40).getInt();
		
		WeepingAngelsMod.pickOnly = config.get(Configuration.CATEGORY_GENERAL, "Hurt Angel with PickAxe only", false).getBoolean(false);
		WeepingAngelsMod.waPaint_ID = config.get(Configuration.CATEGORY_ITEM, "Weeping angel Painting", 3025).getInt();

		if(config.hasChanged()) {
			config.save(); // Configs saved to its file
		}

		MinecraftForge.EVENT_BUS.register(new EventHandler());



		Potion[] potionTypes = null;

		for (Field f : Potion.class.getDeclaredFields()) {
			f.setAccessible(true);
			try {
				if ((f.getName().equals("potionTypes")) || (f.getName().equals("field_76425_a"))) {
					Field modfield = Field.class.getDeclaredField("modifiers");
					modfield.setAccessible(true);
					modfield.setInt(f, f.getModifiers() & 0xFFFFFFEF);

					potionTypes = (Potion[])(Potion[])f.get(null);
					Potion[] newPotionTypes = new Potion[256];
					System.arraycopy(potionTypes, 0, newPotionTypes, 0, potionTypes.length);
					f.set(null, newPotionTypes);
				}
			} catch (Exception e) {
				System.err.println("Severe error, please report this to the mod author:");
				System.err.println(e);
			}

		}

	}

	@SuppressWarnings("unused")
	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.registerRenderThings();
		
		WeepingAngelsMod.angelConvert = new PotionConvert(40, true, 0).setPotionName("potion.angelconvert");
	    LanguageRegistry.instance().addStringLocalization("potion.angelconvert", "Angel Conversion");

		blockWeepingAngelSpawn = new BlockWeepingAngelSpawn(spawnBlockID, 1).setHardness(0.5F).setUnlocalizedName("weepingangelspawn").setCreativeTab(CreativeTabs.tabMisc);	

		
		if(WeepingAngelsMod.statueItemID != 0) {
			plinthBlock = (new BlockPlinth(plinthBlockID, TileEntityPlinth.class, Material.rock)).setHardness(2.0F).setResistance(10F).setStepSound(Block.soundStoneFootstep).setUnlocalizedName("Plinth");		
			statue = (new ItemStatue(statueItemID, EntityStatue.class)).setUnlocalizedName("Statue").setCreativeTab(CreativeTabs.tabMisc).setMaxStackSize(64);
			GameRegistry.registerBlock(plinthBlock, "Plinth");
			LanguageRegistry.addName(plinthBlock, "Plinth");
			LanguageRegistry.addName(statue,"Weeping Angel Statue");
			GameRegistry.addRecipe(new ItemStack(
					WeepingAngelsMod.blockWeepingAngelSpawn, 1), new Object[] {
				"xxx", "xcx", "xxx", 'x', Block.stone, 'c', WeepingAngelsMod.statue
			});
		}
	
		
		
		//EntityRegistry.registerModEntity(EntityWAPainting.class, "Weeping Angel Painting", entityWAPaintingID, this, 80, 3, false);
		//EntityList.IDtoClassMapping.put(entityWAPaintingID, EntityWAPainting.class);

		// Register all entities, blocks and items to game
		//Weeping Angel Entity
		EntityRegistry.registerModEntity(EntityWeepingAngel.class, "Weeping Angel", entityWeepingAngelID, this, 80, 3, true);
		EntityList.IDtoClassMapping.put(entityWeepingAngelID, EntityWeepingAngel.class);
		EntityList.entityEggs.put(entityWeepingAngelID, new EntityEggInfo(entityWeepingAngelID, 0, 0xffffff));
		//EntityRegistry.registerGlobalEntityID(EntityWeepingAngel.class, "Weeping Angel", ModLoader.getUniqueEntityId(), 0x00000, 0xffffff);
		if(spawnRate != 0){
			EntityRegistry.addSpawn(EntityWeepingAngel.class, spawnRate, 1, maxSpawn, EnumCreatureType.monster, new BiomeGenBase[] {
				BiomeGenBase.iceMountains,
				BiomeGenBase.icePlains,
				BiomeGenBase.taiga,
				BiomeGenBase.desert,
				BiomeGenBase.desertHills,
				BiomeGenBase.plains,
				BiomeGenBase.taiga,
				BiomeGenBase.taigaHills,
				BiomeGenBase.swampland,
				BiomeGenBase.beach,
				BiomeGenBase.river,
				BiomeGenBase.frozenRiver,
				BiomeGenBase.extremeHills,
				BiomeGenBase.extremeHillsEdge
			});
		}
		LanguageRegistry.instance().addStringLocalization("entity.WeepingAngels.Weeping Angel.name", "Weeping Angel");
		LanguageRegistry.instance().addStringLocalization("entity.WeepingAngels.Weeping Angel Painting.name", "Weeping Angel Painting");

		//Spawn Block Entity
		LanguageRegistry.addName(blockWeepingAngelSpawn, "Weeping Angel Spawn Block");

		//Statue Block and Item
		GameRegistry.registerBlock(blockWeepingAngelSpawn, "Weeping Angel Spawn Block");

		if(false){//WeepingAngelsMod.waP_Enable) {
			WeepingAngelsMod.waPaint = new ItemWeepPaint(
					WeepingAngelsMod.waPaint_ID, EntityWAPainting.class)
			.setUnlocalizedName("waPaint");
			LanguageRegistry.addName(
					WeepingAngelsMod.waPaint, "Weeping Angel Painting");
			WeepingAngelsMod.waPaint.setCreativeTab(CreativeTabs.tabDecorations);
		}
		
		//ModLoader.registerTileEntity(TileEntityPlinth.class, "plinth");

	}

	@Mod.EventHandler
	public static void postInit(FMLPostInitializationEvent event) {}

}
