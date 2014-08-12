package com.countrygamer.weepingangels.common.extended

import com.countrygamer.cgo.wrapper.common.extended.ExtendedEntityHandler
import com.countrygamer.weepingangels.common.WAOptions
import com.countrygamer.weepingangels.common.entity.EntityWeepingAngel
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.TickEvent
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent
import cpw.mods.fml.relauncher.Side
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.DamageSource

/**
 *
 *
 * @author CountryGamer
 */
object AngelPlayerHandler {

	def get(player: EntityPlayer): AngelPlayer = {
		ExtendedEntityHandler.getExtended(player, classOf[AngelPlayer]).asInstanceOf[AngelPlayer]
	}

	@SubscribeEvent
	def playerTickEvent(event: PlayerTickEvent): Unit = {
		val side: Side = event.side

		if (side == Side.SERVER) {
			if (event.phase == TickEvent.Phase.START) {
				val player: EntityPlayer = event.player
				val angelPlayer: AngelPlayer = ExtendedEntityHandler
						.getExtended(player, classOf[AngelPlayer]).asInstanceOf[AngelPlayer]

				if (angelPlayer == null) return

				if (player.capabilities.isCreativeMode) {
					if (angelPlayer.converting()) {
						angelPlayer.stopConversion()
						angelPlayer.setAngelHealth(0.0F)
						angelPlayer.clearRegenTicks()

					}

				}
				else if (angelPlayer.converting()) {
					if (angelPlayer.getAngelHealth() >= WAOptions.maxAngelHealth) {
						angelPlayer.stopConversion()

						val angelEntity: EntityWeepingAngel = new
										EntityWeepingAngel(player.worldObj)

						angelEntity
								.setPositionAndRotation(player.posX, player.posY, player.posZ,
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

						if (!player.worldObj.isRemote) player.worldObj
								.spawnEntityInWorld(angelEntity)


						if (!player.attackEntityFrom(DamageSource.causeMobDamage(angelEntity),
							Float.MaxValue)) {
							angelEntity.setDead()
						}

					}
					else {
						if (angelPlayer.getTicksUntilNextRegen() <= 0) {
							angelPlayer.clearRegenTicks()
						}
						else {
							angelPlayer.decrementTicksUntilRegen()
						}
						angelPlayer.setHealthWithRespectToTicks()

					}

				}
				else if (!angelPlayer.converting() && angelPlayer.getAngelHealth() > 0.0F) {
					angelPlayer.setAngelHealth(0.0F)
					angelPlayer.clearRegenTicks()
				}

			}

		}

	}

}
