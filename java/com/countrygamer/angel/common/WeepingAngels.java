package com.countrygamer.angel.common;

import java.util.logging.Logger;

import net.minecraft.stats.Achievement;
import net.minecraft.util.ResourceLocation;

import com.countrygamer.angel.common.block.WABlocks;
import com.countrygamer.angel.common.entity.WAEntity;
import com.countrygamer.angel.common.extended.ExtendedAngelPlayer;
import com.countrygamer.angel.common.item.WAItems;
import com.countrygamer.angel.common.packet.PacketStoreCoords;
import com.countrygamer.core.Base.Plugin.PluginBase;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = WeepingAngels.PLUGIN_ID, name = WeepingAngels.PLUGIN_NAME,
		version = "@PLUGIN_VERSION@")
public class WeepingAngels extends PluginBase {
	
	public static final String PLUGIN_ID = "angel";
	public static final String PLUGIN_NAME = "Weeping Angels";
	public static final String BASE_TEX = PLUGIN_ID + ":";
	
	public static final Logger logger = Logger.getLogger(WeepingAngels.PLUGIN_NAME);
	
	@SidedProxy(serverSide = "com.countrygamer.angel.common.CommonProxy",
			clientSide = "com.countrygamer.angel.client.ClientProxy")
	public static CommonProxy proxy;
	
	@Instance(WeepingAngels.PLUGIN_ID)
	public static WeepingAngels instance;
	
	public static Achievement angelAchieve1, angelAchieve2, angelAchieve3;
	
	public static final ResourceLocation weepingAngelTex = new ResourceLocation(BASE_TEX
			+ "textures/entities/weepingangel.png");
	public static final ResourceLocation weepingAngelAngryTex = new ResourceLocation(BASE_TEX
			+ "textures/entities/weepingangel-angry.png");
	
	public static final ResourceLocation weepingAngelStatueTex = new ResourceLocation(BASE_TEX
			+ "textures/blocks/plinth.png");
	
	// Initializations
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		super.preInitialize(WeepingAngels.PLUGIN_NAME, event, WeepingAngels.proxy, new WAOptions(),
				new WAItems(), new WABlocks(), null, new WAEntity());
		
		this.registerExtendedPlayer("Extended Angel Player", ExtendedAngelPlayer.class, true);
		this.registerPacketClass(PacketStoreCoords.class);
		
		this.registerAchievements();
		
	}
	
	public void registerAchievements() {
		WeepingAngels.angelAchieve1 = new Achievement("XXX", "desc", 0, 0, WAItems.statue, null);
		WeepingAngels.angelAchieve2 = new Achievement("XXX", "desc", 0, 0, WAItems.statue, null);
		WeepingAngels.angelAchieve3 = new Achievement("XXX", "desc", 0, 0, WAItems.statue, null);
		
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		super.initialize(event);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		super.postInitialize(event);
	}
	
}
