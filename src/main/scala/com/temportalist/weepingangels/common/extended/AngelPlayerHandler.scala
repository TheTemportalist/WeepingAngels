package com.temportalist.weepingangels.common.extended

import com.temportalist.origin.internal.common.extended.ExtendedEntityHandler
import com.temportalist.weepingangels.common.WAOptions
import com.temportalist.weepingangels.common.entity.EntityAngel
import com.temportalist.weepingangels.common.lib.AngelUtility
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.TickEvent
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent
import cpw.mods.fml.relauncher.Side
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.DamageSource
import net.minecraftforge.event.entity.living.LivingAttackEvent

/**
 *
 *
 * @author TheTemportalist
 */
object AngelPlayerHandler {

	def get(player: EntityPlayer): AngelPlayer = {
		ExtendedEntityHandler.getExtended(player, classOf[AngelPlayer])
	}

	@SubscribeEvent
	def playerTickEvent(event: PlayerTickEvent): Unit = {
		val side: Side = event.side

		if (side == Side.SERVER) {
			if (event.phase == TickEvent.Phase.START) {
				val player: EntityPlayer = event.player
				val angelPlayer: AngelPlayer = ExtendedEntityHandler.getExtended(
					player, classOf[AngelPlayer])

				if (angelPlayer == null) return

				if (player.capabilities.isCreativeMode) {
					if (angelPlayer.converting()) {
						angelPlayer.stopConversion()
						angelPlayer.setAngelHealth(0.0F)
						angelPlayer.clearRegenTicks()

					}

				}
				else if (angelPlayer.converting()) {
					if (angelPlayer.getAngelHealth >= WAOptions.maxAngelHealth) {
						angelPlayer.stopConversion()

						val angelEntity: EntityAngel = new EntityAngel(player.worldObj)

						angelEntity.setPositionAndRotation(player.posX, player.posY, player.posZ,
							player.rotationYaw, player.rotationPitch)

						if (WAOptions.angelsTakePlayerName) {
							angelEntity.setCustomNameTag(player.getCommandSenderName)
						}

						if (WAOptions.angelsStealPlayerInventory) {
							val inventory: Array[ItemStack] = new
											Array[ItemStack](player.inventory.getSizeInventory)

							for (i <- 0 until player.inventory.getSizeInventory) {
								val itemStack: ItemStack = player.inventory.getStackInSlot(i)
								if (itemStack != null) {
									inventory(i) = itemStack.copy()
								}
								else {
									inventory(i) = null
								}
								player.inventory.setInventorySlotContents(i, null)
							}

							angelEntity.setStolenInventory(inventory)

						}

						if (!player.worldObj.isRemote)
							player.worldObj.spawnEntityInWorld(angelEntity)


						player.setHealth(0F)
						player.setDead()
						/*
						if (!player.attackEntityFrom(DamageSource.causeMobDamage(angelEntity),
							Float.MaxValue)) {
							if (angelEntity.hasStolenInventory) angelEntity.dropStolenInventory()
							angelEntity.setDead()
							// TODO Maybe return here, to prevent anything from occuring after death
						}
						*/

					}
					else {
						if (angelPlayer.getTicksUntilNextRegen <= 0) {
							angelPlayer.clearRegenTicks()
						}
						else {
							angelPlayer.decrementTicksUntilRegen()
						}
						angelPlayer.setHealthWithRespectToTicks()

					}

				}
				else if (!angelPlayer.converting() && angelPlayer.getAngelHealth > 0.0F) {
					angelPlayer.setAngelHealth(0.0F)
					angelPlayer.clearRegenTicks()
				}

				/*
				if (Api.getMorphEntity(player.getName, false)
						.isInstanceOf[EntityAngel]) {
					if (player.motionY > 0.4F) {
						player.motionY = 0.0F
						angelPlayer.syncEntity()
					}
				}
				*/

			}

		}

	}

	@SubscribeEvent
	def onLivingAttack(event: LivingAttackEvent): Unit = {
		event.entityLiving match {
			case player: EntityPlayer =>

				if (!AngelPlayerHandler.isAngel(player)) return

				// hurt entity is player
				this.onLivingAttack_do(player, isAttacker = false)
				// TODO, this needs configuring. Very OP
				if (!AngelUtility
						.canAttackEntityFrom(player.worldObj, event.source, event.ammount)) {
					event.setCanceled(true)
				}
			case _ =>
				event.source.getSourceOfDamage match {
					case player: EntityPlayer =>

						if (!AngelPlayerHandler.isAngel(player)) return

						// caused by player
						this.onLivingAttack_do(player, isAttacker = true)
					case _ =>
				}
		}
	}

	private def isAngel(player: EntityPlayer): Boolean = {
		/*
		Api.getMorphEntity(player.getName,
			FMLCommonHandler.instance().getEffectiveSide.isClient)
				.isInstanceOf[EntityAngel]
		*/
		false
	}

	private def onLivingAttack_do(player: EntityPlayer, isAttacker: Boolean): Unit = {
		val angelPlayer: AngelPlayer = AngelPlayerHandler.get(player)
		if (isAttacker) {
			angelPlayer.setIsAttacking()
		}
		else {
			angelPlayer.setIsAttacked()
		}

	}

}
