package com.temportalist.weepingangels.common.entity.ai

import java.util.Calendar

import net.minecraft.command.IEntitySelector
import net.minecraft.entity.monster.IMob
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.{EntityLiving, EntityCreature, EntityLivingBase}

/**
 * Created by TheTemportalist on 12/17/2015.
 */
class EntityAIAngelAttackTarget(owner: EntityCreature)
		extends EntityAISwitchableTarget(owner, 0, true, false) {

	override def getTargetClass: Class[_ <: EntityLivingBase] = {
		if (this.isChristmas(0) || (this.isChristmas(7) && this.hasNearbyPlayer))
			classOf[EntityLiving]
		else classOf[EntityPlayer]
	}

	/**
	 * This filter is applied to the Entity search.  Only matching entities will be targetted.  (null -> no
	 * restrictions)
	 */
	override def getEntitySelector: IEntitySelector = {
		if (this.isChristmas(0) || (this.isChristmas(7) && this.hasNearbyPlayer))
			IMob.mobSelector
		else null
	}

	def hasNearbyPlayer: Boolean = {
		val followRange = this.getTargetDistance * 2 / 3
		!this.selectEntities(classOf[EntityPlayer], followRange, null).isEmpty
	}

	// TODO move to origin
	def isChristmas(leniencyDays: Int): Boolean = {
		val currentDate = Calendar.getInstance().getTime
		currentDate.getMonth == 12 && currentDate.getDate >= 25 - leniencyDays &&
				currentDate.getDate < 26 + leniencyDays
	}

}
