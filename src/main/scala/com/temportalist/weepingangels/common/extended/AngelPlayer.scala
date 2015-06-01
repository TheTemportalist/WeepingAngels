package com.temportalist.weepingangels.common.extended

import com.temportalist.origin.foundation.common.extended.ExtendedEntity
import com.temportalist.origin.foundation.common.network.PacketExtendedSync
import com.temportalist.weepingangels.common.WAOptions
import com.temportalist.weepingangels.common.lib.AngelUtility
import cpw.mods.fml.relauncher.Side
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World

/**
 *
 *
 * @author TheTemportalist
 */
class AngelPlayer(player: EntityPlayer) extends ExtendedEntity(player) {

	private var isConverting: Boolean = false
	private var angelHealth: Float = 0.0F
	private var fractionalHealth: Float = 0.0F
	private var isWatched: Boolean = false
	private var ticksUntilNextRegen: Int = 0
	private var ticksWhenAttack: Int = -1
	private var ticksWhenAttacked: Int = -1

	// Default Constructor
	{
		this.ticksUntilNextRegen = this.getMaxTicksPerRegen

	}

	// End Constructor

	override def init(entity: Entity, world: World): Unit = {
		this.ticksWhenAttack = -1
		this.ticksWhenAttacked = -1

	}

	override def saveNBTData(tagCom: NBTTagCompound): Unit = {
		tagCom.setBoolean("isConverting", this.isConverting)
		tagCom.setFloat("angelHealth", this.angelHealth)
		tagCom.setFloat("fractionalHealth", this.fractionalHealth)
		tagCom.setBoolean("isWatched", this.isWatched)
		tagCom.setInteger("ticksUntilNextRegen", this.ticksUntilNextRegen)
		tagCom.setInteger("ticksWhenAttack", this.ticksWhenAttack)
		tagCom.setInteger("ticksWhenAttacked", this.ticksWhenAttacked)

	}

	override def loadNBTData(tagCom: NBTTagCompound): Unit = {
		this.isConverting = tagCom.getBoolean("isConverting")
		this.angelHealth = tagCom.getFloat("angelHealth")
		this.fractionalHealth = tagCom.getFloat("fractionalHealth")
		this.isWatched = tagCom.getBoolean("isWatched")
		this.ticksUntilNextRegen = tagCom.getInteger("ticksUntilNextRegen")
		this.ticksWhenAttack = tagCom.getInteger("ticksWhenAttack")
		this.ticksWhenAttacked = tagCom.getInteger("ticksWhenAttacked")

	}

	def getMaxTicksPerRegen: Int = {
		val totalTime: Int = (WAOptions.totalConversionTime.asInstanceOf[Double] /
				WAOptions.maxAngelHealth.asInstanceOf[Double]).asInstanceOf[Int]
		totalTime
	}

	def startConversion(): Unit = {
		this.isConverting = true
		this.syncEntity("converting", this.isConverting)
	}

	def stopConversion(): Unit = {
		this.isConverting = false
		this.syncEntity("converting", this.isConverting)
	}

	def converting(): Boolean = {
		this.isConverting
	}

	def setAngelHealth(newHealth: Float): Unit = {
		this.angelHealth = newHealth
		this.syncEntity("health", this.angelHealth)
	}

	def getAngelHealth: Float = {
		this.angelHealth + this.fractionalHealth
	}

	def setHealthWithRespectToTicks(): Unit = {

		val maxTicks: Int = this.getMaxTicksPerRegen
		this.fractionalHealth = ((maxTicks - this.ticksUntilNextRegen).asInstanceOf[Double] /
				maxTicks.asInstanceOf[Double]).asInstanceOf[Float]
		if (this.fractionalHealth >= 1.0F) {
			this.angelHealth += 1
			this.fractionalHealth = 0.0F
		}

		this.syncEntity("health_with_ticks", this.angelHealth, this.fractionalHealth)

	}

	def setTicksUntilNextRegen(newTicks: Int): Unit = {
		this.ticksUntilNextRegen = newTicks
		this.syncEntity("regen_ticks", this.ticksUntilNextRegen)
	}

	def getTicksUntilNextRegen: Int = {
		this.ticksUntilNextRegen
	}

	def decrementTicksUntilRegen(): Unit = {
		this.ticksUntilNextRegen -= 1
		this.syncEntity("regen_ticks", this.ticksUntilNextRegen)
	}

	def clearRegenTicks(): Unit = {
		this.ticksUntilNextRegen = this.getMaxTicksPerRegen
		this.syncEntity("regen_ticks", this.ticksUntilNextRegen)
	}

	def getOpacityForBlackout: Float = {
		if (this.getAngelHealth > 0.0F)
			this.getAngelHealth / WAOptions.maxAngelHealth.asInstanceOf[Float]
		else 0.0F
	}

	// Morph Compatibility

	def setWatched(isWatched: Boolean): Unit = {
		this.isWatched = isWatched
		this.syncEntity("watched", this.isWatched)
	}

	def isQuantumLocked: Boolean = {
		this.isWatched
	}

	def setIsAttacking(): Unit = {
		this.ticksWhenAttack = this.player.ticksExisted
		this.syncEntity("attack_ticks", this.ticksWhenAttack)
	}

	def setIsAttacked(): Unit = {
		this.ticksWhenAttacked = this.player.ticksExisted
		this.syncEntity("attacked_ticks", this.ticksWhenAttacked)
	}

	def getAngryState: Byte = {
		val ticksSinceLastAttacked: Int = this.player.ticksExisted - this.ticksWhenAttacked
		if (this.ticksWhenAttacked >= 0 && ticksSinceLastAttacked <= WAOptions.morphedAngryTicks)
			1
		else
			0
	}

	def getArmState: Byte = {
		val ticksSinceLastAttack: Int = this.player.ticksExisted - this.ticksWhenAttack
		if (this.ticksWhenAttack >= 0 && ticksSinceLastAttack <= WAOptions.morphedChaseTicks) {
			return 2
		}

		val nearbyAngels: java.util.List[_] = AngelUtility.getNearbyAngels(this.player)

		if (nearbyAngels.size() > 0) {
			return 0
		}

		1
	}

	override def handleSyncPacketData(uniqueIdentifier: String, packet: PacketExtendedSync,
			side: Side): Unit = {
		uniqueIdentifier match {
			case "converting" =>
				this.isConverting = packet.get[Boolean]
			case "health" =>
				this.angelHealth = packet.get[Float]
			case "health_with_ticks" =>
				this.angelHealth = packet.get[Float]
				this.fractionalHealth = packet.get[Float]
			case "regen_ticks" =>
				this.ticksUntilNextRegen = packet.get[Int]
			case "watched" =>
				this.isWatched = packet.get[Boolean]
			case "attack_ticks" =>
				this.ticksWhenAttack = packet.get[Int]
			case "attacked_ticks" =>
				this.ticksWhenAttacked = packet.get[Int]
			case _ =>
		}
	}

}
