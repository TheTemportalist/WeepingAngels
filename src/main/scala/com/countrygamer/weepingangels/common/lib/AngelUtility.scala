package com.countrygamer.weepingangels.common.lib

import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.{AxisAlignedBB, MathHelper, Vec3}
import net.minecraft.world.World

/**
 *
 *
 * @author CountryGamer
 */
object AngelUtility {

	def canBeSeen_Multiplayer(world: World, entity: EntityLivingBase, boundingBox: AxisAlignedBB,
			radius: Double): Boolean = {

		if (world.getFullBlockLightValue(
			MathHelper.floor_double(entity.posX),
			MathHelper.floor_double(entity.posY),
			MathHelper.floor_double(entity.posZ)
		) <= 1.0F)
			return false

		val entityList: java.util.List[_] = world
				.getEntitiesWithinAABB(classOf[EntityPlayer],
		            boundingBox.expand(radius, radius, radius))

		var numberOfPlayersWatching: Int = 0

		var index: Int = 0
		for (index <- 0 until entityList.size()) {
			val player: EntityPlayer = entityList.get(index).asInstanceOf[EntityPlayer]

			if (this.isInFieldOfViewOf(player, entity)) {
				numberOfPlayersWatching = numberOfPlayersWatching + 1
			}

		}

		numberOfPlayersWatching > 0
	}

	def isInFieldOfViewOf(entity: EntityLivingBase, thisEntity: EntityLivingBase): Boolean = {
		val entityVec: Vec3 = entity.getLookVec
		var difVec: Vec3 = Vec3.createVectorHelper(
			thisEntity.posX - entity.posX,
			(thisEntity.boundingBox.minY + thisEntity.height.asInstanceOf[Double]) -
					(entity.posY + entity.getEyeHeight.asInstanceOf[Double]),
			thisEntity.posZ - entity.posZ
		)
		val lengthOfDif: Double = difVec.lengthVector()
		difVec = difVec.normalize()
		// Check for blocks between
		val d1: Double = entityVec.dotProduct(difVec)

		if (d1 > ((1.0D - 0.025D) / lengthOfDif)) {
			entity.canEntityBeSeen(thisEntity)
		}
		else {
			false
		}
	}

}
