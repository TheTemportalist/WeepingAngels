package com.countrygamer.angel.common.entity;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;

import com.countrygamer.angel.common.WAOptions;
import com.countrygamer.angel.common.WeepingAngels;
import com.countrygamer.core.Base.Plugin.PluginEntityRegistry;

import cpw.mods.fml.common.registry.EntityRegistry;

public class WAEntity implements PluginEntityRegistry {
	
	@Override
	public void registerEntities() {
		EntityRegistry.registerModEntity(EntityWeepingAngel.class, "Weeping Angel", 1,
				WeepingAngels.instance, 80, 3, false);
		
	}
	
	@Override
	public void addEntityMappings() {
		EntityList.addMapping(EntityWeepingAngel.class, "WeepingAngel", this.getNewEntityID(),
				0x808080, 0xD1D1D1);
		
	}
	
	@Override
	public void registerEntitySpawns() {
		if (WAOptions.spawnRate_Angel > 0) {
			for (int i = 0; i < BiomeGenBase.getBiomeGenArray().length; i++) {
				if (BiomeGenBase.getBiomeGenArray()[i] != null) {
					// ~ mob file, spawn frequency, group size,
					// ~ creature type, biome
					cpw.mods.fml.common.registry.EntityRegistry.addSpawn(EntityWeepingAngel.class,
							WAOptions.spawnRate_Angel, 1, WAOptions.maxSpawnPerInstance_Angel,
							EnumCreatureType.monster, BiomeGenBase.getBiomeGenArray()[i]);
				}
			}
		}
		
	}
	
	public int getNewEntityID() {
		int id = 0;
		while (EntityList.getStringFromID(id) != null)
			id++;
		return id;
	}
	
}
