package WeepingAngels;

import WeepingAngels.Blocks.BlockPlinth;
import WeepingAngels.Blocks.BlockWeepingAngelSpawn;
import WeepingAngels.Blocks.TileEnt.TileEntityPlinth;
import WeepingAngels.Entity.EntityStatue;
import WeepingAngels.Entity.EntityWeepingAngel;
import WeepingAngels.Items.ItemStatue;
import WeepingAngels.Proxy.PacketHandler;
import WeepingAngels.Proxy.ServerProxy;
import WeepingAngels.lib.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityEggInfo;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
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
	
	public static Block	plinthBlock;
	public static Block blockWeepingAngelSpawn;
	public static Item statue;

	public static int spawnRate;
	public static int maxSpawn;
	public static int attackStrength;
	public static int teleportChance;
	public static int teleportRangeMin;
	public static int teleportRangeMax;
	public static int plinthBlockID;
	public static int spawnBlockID;
	public static int statueItemID;
	public static int entityWeepingAngelID;
	
	public static final boolean DEBUG = true;
	public static boolean pickOnly;

	@Instance(Reference.MOD_ID)
	public static WeepingAngelsMod instance;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS,
			serverSide = Reference.SERVER_PROXY_CLASS)
	public static ServerProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();

		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load(); // load configs from its file
		
		entityWeepingAngelID = config.get(Configuration.CATEGORY_GENERAL, "EntityWeepingAngelID", 300).getInt();
		statueItemID = config.get(Configuration.CATEGORY_ITEM, "StatueItemID", 12034).getInt();
		plinthBlockID = config.get(Configuration.CATEGORY_BLOCK, "PlinthBlockID", 3023).getInt();
		spawnBlockID = config.get(Configuration.CATEGORY_BLOCK, "SpawnBlockID", 3024).getInt();
		attackStrength = config.get(Configuration.CATEGORY_GENERAL, "AttackStrength", 2).getInt();
		teleportChance = config.get(Configuration.CATEGORY_GENERAL, "TeleportChance", 5).getInt();
		teleportRangeMin = config.get(Configuration.CATEGORY_GENERAL, "TeleportRangeMin", 0).getInt();
		teleportRangeMax = config.get(Configuration.CATEGORY_GENERAL, "TeleportRangeMax", 100).getInt();
		maxSpawn = config.get(Configuration.CATEGORY_GENERAL, "MaxSpawnedPerInstance", 2).getInt();
		spawnRate = config.get(Configuration.CATEGORY_GENERAL, "SpawnRate", 2).getInt();	
		maxSpawn = config.get(Configuration.CATEGORY_GENERAL, "MaxSpawnedPerInstance", 2).getInt();
		
		WeepingAngelsMod.pickOnly = config.get(Configuration.CATEGORY_GENERAL, "Hurt Angel with PickAxe only", false).getBoolean(false);
		
		if(config.hasChanged()) {
			config.save(); // Configs saved to its file
		}
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		
		plinthBlock = (new BlockPlinth(plinthBlockID, TileEntityPlinth.class, Material.rock)).setHardness(2.0F).setResistance(10F).setStepSound(Block.soundStoneFootstep).setUnlocalizedName("Plinth");		
		blockWeepingAngelSpawn = new BlockWeepingAngelSpawn(spawnBlockID, 1).setHardness(0.5F).setUnlocalizedName("weepingangelspawn").setCreativeTab(CreativeTabs.tabMisc);	
		statue = (new ItemStatue(statueItemID, EntityStatue.class)).setUnlocalizedName("Statue").setCreativeTab(CreativeTabs.tabMisc).setMaxStackSize(64);

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

		//Spawn Block Entity
		LanguageRegistry.addName(blockWeepingAngelSpawn, "Weeping Angel Spawn Block");

		//Statue Block and Item
		GameRegistry.registerBlock(plinthBlock, "Plinth");
		GameRegistry.registerBlock(blockWeepingAngelSpawn, "Weeping Angel Spawn Block");
		LanguageRegistry.addName(plinthBlock, "Plinth");
		LanguageRegistry.addName(statue,"Weeping Angel Statue");
		
		proxy.registerRenderThings();	
	}

	@Mod.EventHandler
	public static void postInit(FMLPostInitializationEvent event) {}

}
