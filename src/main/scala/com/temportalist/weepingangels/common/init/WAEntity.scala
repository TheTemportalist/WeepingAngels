package com.temportalist.weepingangels.common.init

import com.temportalist.origin.library.common.register.EntityRegister
import com.temportalist.weepingangels.common.entity.{EntityAngelArrow, EntityWeepingAngel}
import com.temportalist.weepingangels.common.{WAOptions, WeepingAngels}
import cpw.mods.fml.common.registry.EntityRegistry
import net.minecraft.entity.EnumCreatureType
import net.minecraft.world.biome.BiomeGenBase

/**
 *
 *
 * @author TheTemportalist
 */
object WAEntity extends EntityRegister {

	override def register(): Unit = {

		EntityRegistry.registerGlobalEntityID(classOf[EntityWeepingAngel], "WeepingAngel",
			EntityRegistry.findGlobalUniqueEntityId(), 0x808080, 0xD1D1D1)

		EntityRegistry.registerModEntity(
			classOf[EntityWeepingAngel], "WeepingAngel", 0, WeepingAngels, 80, 3, false
		)

		EntityRegistry.registerModEntity(
			classOf[EntityAngelArrow], "AngelArrow", 1, WeepingAngels, 100, 10, true
		)

		/*
		EntityRegistry.registerModEntity(
			classOf[EntityWeepingAngel], "Weeping Angel", 0, WeepingAngels, 80, 3, false
		)
		EntityList.


		EntityRegistry.registerModEntity(
			classOf[EntityWeepingAngel], "Weeping Angel", 1, WeepingAngels, 80, 3, false
		)

		EntityRegistry.registerModEntity(
			classOf[EntityAngelArrow], "Angel Arrow", 2, WeepingAngels, 100, 10, true
		)
		*/

	}

	/*
	def addEntity(entityClass: Class[_ <: Entity], name: String): Unit = {
		EntityRegistry.registerGlobalEntityID(entityClass, name, EntityRegistry.findGlobalUniqueEntityId())
	}

	def addEntity(entityClass: Class[_ <: Entity], name: String, backgroundColor: Int,
			foregroundColor: Int): Unit = {
	}
	*/

	override def addEntityMappings: Unit = {
		/*
		EntityRegistry.registerGlobalEntityID(
			classOf[EntityWeepingAngel], "Weeping Angel",
			EntityRegistry.findGlobalUniqueEntityId(), 0x808080, 0xD1D1D1
		)

		EntityRegistry.registerGlobalEntityID(
			classOf[EntityAngelArrow], "Angel Arrow",
			EntityRegistry.findGlobalUniqueEntityId()
		)
		*/

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
