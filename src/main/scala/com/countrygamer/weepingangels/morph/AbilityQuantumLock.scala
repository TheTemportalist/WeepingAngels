package com.countrygamer.weepingangels.morph

import com.countrygamer.weepingangels.common.WeepingAngels
import com.countrygamer.weepingangels.common.extended.{AngelPlayer, AngelPlayerHandler}
import com.countrygamer.weepingangels.common.lib.AngelUtility
import morph.api.Ability
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation

/**
 *
 *
 * @author CountryGamer
 */
class AbilityQuantumLock() extends Ability() {

	private val baseMovementSpeed: Double = 0.10000000149011612D

	override def getType: String = {
		"timelock"
	}

	override def getIcon: ResourceLocation = {
		new ResourceLocation(WeepingAngels.pluginID, "textures/blocks/Plinth.png")
	}

	override def kill(): Unit = {

	}

	override def tick(): Unit = {
		this.getParent match {
			case player: EntityPlayer =>
				val isWatched: Boolean = AngelUtility.canBeSeen_Multiplayer(
					player.worldObj, player, player.boundingBox, 64D)
				val angelPlayer: AngelPlayer = AngelPlayerHandler.get(player)
				if (isWatched) {
					player.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
							.setBaseValue(0.0D)
					angelPlayer.setWatched(isWatched = true)
				}
				else {
					player.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
							.setBaseValue(0.4D)
					angelPlayer.setWatched(isWatched = false)
				}
			case _ =>
		}

	}

	override def postRender(): Unit = {

	}

	override def save(tag: NBTTagCompound): Unit = {

	}

	override def load(tag: NBTTagCompound): Unit = {

	}

	/**
	 * Creates a copy of this ability for use with parents.
	 * As previously stated before the ability instance used during registration is a base so it needs to be cloned for use with parents.
	 */
	override def clone: Ability = {
		new AbilityQuantumLock()
	}

}
