package com.countrygamer.angel.common.item;

import net.minecraft.item.Item;

import com.countrygamer.angel.common.WAOptions;
import com.countrygamer.angel.common.WeepingAngels;
import com.countrygamer.angel.common.entity.EntityStatue;
import com.countrygamer.core.Base.Plugin.PluginItemRegistry;
import com.countrygamer.core.Base.common.item.ItemBase;
import com.countrygamer.countrygamercore.common.Core;

public class WAItems implements PluginItemRegistry {
	
	public static Item	statue;
	public static Item	chrononDust;
	public static Item	chrononDiamond;
	public static Item	chrononMetal;
	public static Item	vortexManipulator;
	public static Item	sonicScrewdriver;
	
	@Override
	public void registerItems() {
		WAItems.statue = new ItemStatue(WeepingAngels.PLUGIN_ID, "Weeping Angel Statue",
				EntityStatue.class);
		Core.addItemToTab(WAItems.statue);
		
		WeepingAngels.logger.info(WAOptions.addonVortexActive + " : " + WAOptions.addonSonicActive);
		if (WAOptions.addonVortexActive || WAOptions.addonSonicActive) {
			WAItems.chrononDust = new ItemBase(WeepingAngels.PLUGIN_ID, "Chronon Dust");
			Core.addItemToTab(WAItems.chrononDust);
			WAItems.chrononDiamond = new ItemBase(WeepingAngels.PLUGIN_ID, "Chronon Diamond");
			Core.addItemToTab(WAItems.chrononDiamond);
			WAItems.chrononMetal = new ItemBase(WeepingAngels.PLUGIN_ID, "Chronon Metal");
			Core.addItemToTab(WAItems.chrononMetal);
		}
		if (WAOptions.addonVortexActive) {
			WAItems.vortexManipulator = new ItemVortex(WeepingAngels.PLUGIN_ID,
					"Vortex Manipulator");
			Core.addItemToTab(WAItems.vortexManipulator);
		}
		if (WAOptions.addonSonicActive) {
			WAItems.sonicScrewdriver = new ItemSonic(WeepingAngels.PLUGIN_ID, "Sonic Screwdriver");
			Core.addItemToTab(WAItems.sonicScrewdriver);
		}
		
	}
	
	@Override
	public void registerItemsPostBlock() {
		
	}
	
	@Override
	public void registerItemCraftingRecipes() {
		
	}
	
	@Override
	public void registerItemSmeltingRecipes() {
		
	}
	
	@Override
	public void registerOtherItemRecipes() {
		
	}
	
}
