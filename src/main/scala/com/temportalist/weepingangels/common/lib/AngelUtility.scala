package com.temportalist.weepingangels.common.lib

import java.util
import com.temportalist.origin.library.common.lib.vec.V3O
import com.temportalist.weepingangels.common.WAOptions
import com.temportalist.weepingangels.common.entity.EntityWeepingAngel
import net.minecraft.block.Block
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util._
import net.minecraft.world.{EnumDifficulty, World}

/**
 *
 *
 * @author TheTemportalist
 */
object AngelUtility {

	def canBeSeen_Multiplayer(world: World, entity: EntityLivingBase, boundingBox: AxisAlignedBB,
			radius: Double): Boolean = {

		if (world.getLight(entity.getPosition) <= 1.0F)
			return false

		this.canBeSeen(world, entity, boundingBox, radius, classOf[EntityPlayer]) ||
				this.canBeSeen(world, entity, boundingBox, radius, classOf[EntityWeepingAngel])
	}

	def canBeSeen(world: World, entity: EntityLivingBase, boundingBox: AxisAlignedBB,
			radius: Double, clazz: Class[_ <: EntityLivingBase]): Boolean = {
		/*
		val entityList: java.util.List[_] = world
				.getEntitiesWithinAABB(clazz, boundingBox.expand(radius, radius, radius))
		for (i <- 0 until entityList.size()) {
			val e: EntityLivingBase = entityList.get(i).asInstanceOf[EntityLivingBase]
			if (this.isInFieldOfViewOf(e, entity)) {
				e match {
					case angel: EntityWeepingAngel =>
						if (angel.getArmState > 0)
							return true
					case _ =>
						return true
				}
			}
		}
		false
		*/
		!this.getLookingList(world, entity, boundingBox, radius, clazz).isEmpty
	}

	def getLookingList(world: World, entity: EntityLivingBase, boundingBox: AxisAlignedBB,
			radius: Double, clazz: Class[_ <: EntityLivingBase]): util.List[EntityLivingBase] = {
		val entityList: java.util.List[_] = world
				.getEntitiesWithinAABB(clazz, boundingBox.expand(radius, radius, radius))
		val lookingList: util.List[EntityLivingBase] = new util.ArrayList[EntityLivingBase]()
		for (i <- 0 until entityList.size()) {
			val e: EntityLivingBase = entityList.get(i).asInstanceOf[EntityLivingBase]
			if (this.isInFieldOfViewOf(e, entity)) {
				e match {
					case angel: EntityWeepingAngel =>
						if (angel.getArmState > 0)
							lookingList.add(angel)
					case _ =>
						lookingList.add(e)
				}
			}
		}
		lookingList
	}

	/**
	 * Will return the closest entity to the passed entity that can see the passed entity
	 * @param world
	 * @param entity
	 * @param boundingBox
	 * @param radius
	 * @param clazz
	 * @return
	 */
	def getEntityLooking(world: World, entity: EntityLivingBase, boundingBox: AxisAlignedBB,
			radius: Double, clazz: Class[_ <: EntityLivingBase]): EntityLivingBase = {
		val lookingList: util.List[EntityLivingBase] = this.getLookingList(
			world, entity, boundingBox, radius, clazz
		)

		if (!lookingList.isEmpty) {
			var retEntity: EntityLivingBase = null
			var retDistance: Float = radius.asInstanceOf[Float]
			var lookingDistance: Float = 0.0F
			for (i <- 0 until lookingList.size()) {
				lookingList.get(i) match {
					case player: EntityPlayer =>
						lookingDistance = entity.getDistanceToEntity(lookingList.get(i))
						if (lookingDistance < retDistance) {
							retDistance = lookingDistance
							retEntity = lookingList.get(i)
						}
					case _ =>
				}
			}
			retEntity
		}
		else {
			null
		}
	}

	def isInFieldOfViewOf(entity: EntityLivingBase, thisEntity: EntityLivingBase): Boolean = {
		val entityLookVec: V3O = new V3O(entity.getLook(1.0F))
		val thisEntityPos: V3O = new V3O(thisEntity.getPositionVector).add(0, thisEntity.height, 0)
		val entityPos: V3O = new V3O(entity.getPositionVector).add(0, entity.getEyeHeight, 0)
		val differenceVec: V3O = thisEntityPos - entityPos

		val lengthVec: Double = differenceVec.magnitude()

		val differenceVec_normal = differenceVec.normalize()

		val d1: Double = entityLookVec.dotProduct(differenceVec_normal)

		if (d1 > (1.0D - 0.025D) / lengthVec && thisEntity.canEntityBeSeen(entity)) {
			true
		}
		else {
			false
		}
	}

	def getNearbyAngels(entity: EntityLivingBase): util.List[_] = {
		entity.worldObj.getEntitiesWithinAABB(classOf[EntityWeepingAngel],
			entity.getBoundingBox.expand(20D, 20D, 20D))
	}

	def canAttackEntityFrom(world: World, source: DamageSource, damage: Float): Boolean = {
		if (source != null) {
			val validSources: Boolean =
				source == DamageSource.generic ||
						source == DamageSource.magic ||
						source.damageType.equals("player")

			if (!validSources) {
				return false
			}

			source.getSourceOfDamage match {
				case player: EntityPlayer =>
					var canDamage: Boolean = false
					val heldStack: ItemStack = player.inventory.getCurrentItem

					if (WAOptions.angelsOnlyHurtWithPickaxe) {
						if (heldStack != null) {

							var blockLevel: Block = Blocks.dirt

							if (world.getDifficulty == EnumDifficulty.PEACEFUL) {
								blockLevel = Blocks.dirt // anything
							}
							else if (world.getDifficulty == EnumDifficulty.EASY) {
								blockLevel = Blocks.iron_ore // Stone or higher
							}
							else if (world.getDifficulty == EnumDifficulty.NORMAL) {
								blockLevel = Blocks.diamond_ore // Iron or higher
							}
							else if (world.getDifficulty == EnumDifficulty.HARD) {
								blockLevel = Blocks.obsidian // Diamond or higher
							}

							canDamage = heldStack.getItem.canHarvestBlock(blockLevel, heldStack) ||
									heldStack.getItem.canHarvestBlock(blockLevel)

						}
					}
					else {
						canDamage = true
					}

					if (canDamage) {
						return true
					}

					return false

				case _ =>
					return true
			}

		}
		false
	}

	def getDecrepitation(age: Int): Int = {
		if (age <= 0) {
			return 0
		}
		// f(age) = -age + 6000
		// where age is in terms of 6000 -> 0 (can divide by 1200, otherwise known as ticks per decrepitation)
		WAOptions.maxDecrepitation_amount - (age / WAOptions.ticksPerDecrepitation)
	}

}
