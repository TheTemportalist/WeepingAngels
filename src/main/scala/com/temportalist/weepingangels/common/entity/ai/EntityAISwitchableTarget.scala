package com.temportalist.weepingangels.common.entity.ai

import java.util
import java.util.{Comparator, Collections, List}

import com.temportalist.origin.api.common.utility
import com.temportalist.origin.api.common.utility.Scala
import net.minecraft.command.IEntitySelector
import net.minecraft.entity.{Entity, EntityLivingBase, EntityCreature}
import net.minecraft.entity.ai.{EntityAITarget, EntityAINearestAttackableTarget}

/**
 * Created by TheTemportalist on 12/17/2015.
 */
abstract class EntityAISwitchableTarget(owner: EntityCreature,
		private val targetChance: Int, checkSight: Boolean, nearbyOnly: Boolean)
		extends EntityAITarget(owner, checkSight, nearbyOnly) {

	/**
	    Instance of EntityAINearestAttackableTargetSorter.
	 */
	private val theNearestAttackableTargetSorter: EntityAINearestAttackableTarget.Sorter =
		new EntityAINearestAttackableTarget.Sorter(this.taskOwner)

	private var targetEntity: EntityLivingBase = null

	this.setMutexBits(1)

	def getTargetClass: Class[_ <: EntityLivingBase]

	/**
	 * This filter is applied to the Entity search.  Only matching entities will be targetted.  (null -> no
	 * restrictions)
	 */
	def getEntitySelector: IEntitySelector = null

	override def shouldExecute(): Boolean = {
		if (this.targetChance > 0 && this.taskOwner.getRNG.nextInt(this.targetChance) != 0)
			return false
		val list: util.List[Entity] = this.selectEntities(this.getTargetClass,
			this.getTargetDistance, this.getEntitySelector)
		Collections.sort(list, new Comparator[Entity] {
			override def compare(e1: Entity, e2: Entity): Int = {
				val d0: Double = taskOwner.getDistanceSqToEntity(e1)
				val d1: Double = taskOwner.getDistanceSqToEntity(e2)
				if (d0 < d1) -1
				else if (d0 > d1) 1
				else 0
			}
		})
		if (list.isEmpty) false
		else {
			this.targetEntity = list.get(0).asInstanceOf[EntityLivingBase]
			true
		}
	}

	def selectEntities(target: Class[_ <: EntityLivingBase], horizontalFollowRange: Double,
			selector: IEntitySelector): util.List[Entity] = {
		val list: util.List[Entity] = new util.ArrayList[Entity]()
		val old = this.taskOwner.worldObj.selectEntitiesWithinAABB(
			target,
			this.taskOwner.boundingBox.expand(horizontalFollowRange, 4D, horizontalFollowRange),
			selector
		)
		for (i <- 0 until old.size()) list.add(old.get(i).asInstanceOf[Entity])
		list
	}

	override def startExecuting(): Unit = {
		this.taskOwner.setAttackTarget(this.targetEntity)
		super.startExecuting()
	}

}
