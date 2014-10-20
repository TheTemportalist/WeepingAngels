package com.temportalist.weepingangels.common.entity

import com.temportalist.weepingangels.common.init.WAItems
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityArrow
import net.minecraft.item.ItemStack
import net.minecraft.world.World

/**
 *
 *
 * @author TheTemportalist
 */
class EntityAngelArrow(world: World, shot: EntityLivingBase, f: Float)
		extends EntityArrow(world, shot, f) {

	def this(world: World) {
		this(world, new EntityWeepingAngel(world), 0.0F)
	}

	/**
	 * Called by a player entity when they collide with an entity
	 */
	override def onCollideWithPlayer(player: EntityPlayer): Unit = {
		///*
		if (!this.worldObj.isRemote && this.arrowShake <= 0) {
			if (this.canBePickedUp == 1) {
				if (player.inventory.addItemStackToInventory(new ItemStack(WAItems.angelArrow))) {
					this.playSound("random.pop", 0.2F,
						((this.rand.nextFloat - this.rand.nextFloat) * 0.7F + 1.0F) * 2.0F)
					player.onItemPickup(this, 1)
					this.setDead
				}
			}
			/*
			if (
				!(
						this.canBePickedUp == 1 &&
								!player.inventory
										.addItemStackToInventory(new ItemStack(WAItems.angelArrow))
						)
			) {
				this.playSound("random.pop", 0.2F,
					((this.rand.nextFloat - this.rand.nextFloat) * 0.7F + 1.0F) * 2.0F)
				player.onItemPickup(this, 1)
				this.setDead
			}
			*/
		}
		//*/
	}

}
