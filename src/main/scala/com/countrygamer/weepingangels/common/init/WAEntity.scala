package com.countrygamer.weepingangels.common.init

import com.countrygamer.cgo.wrapper.common.registries.EntityRegister
import com.countrygamer.weepingangels.common.entity.EntityWeepingAngel
import com.countrygamer.weepingangels.common.{WAOptions, WeepingAngels}
import cpw.mods.fml.common.registry.EntityRegistry
import net.minecraft.entity.EnumCreatureType
import net.minecraft.world.biome.BiomeGenBase

/**
 *
 *
 * @author CountryGamer
 */
object WAEntity extends EntityRegister {

	override def register(): Unit = {
		EntityRegistry
				.registerModEntity(classOf[EntityWeepingAngel], "Weeping Angel", 1, WeepingAngels,
		            80, 3, false)

	}

	override def addEntityMappings: Unit = {
		EntityRegistry.registerGlobalEntityID(classOf[EntityWeepingAngel], "Weeping Angel",
			this.getNewEntityID(), 0x808080, 0xD1D1D1)

	}

	override def addEntitySpawns: Unit = {
		var index: Int = 0
		for (index <- 0 until BiomeGenBase.getBiomeGenArray.length) {
			val biome: BiomeGenBase = BiomeGenBase.getBiomeGenArray()(index)

			if (biome != null && !(biome == BiomeGenBase.mushroomIsland ||
					biome == BiomeGenBase.mushroomIslandShore)) {
				EntityRegistry
						.addSpawn(classOf[EntityWeepingAngel], WAOptions.spawnProbability, 1, 1,
				            EnumCreatureType.monster, biome)

			}

		}

	}

}
