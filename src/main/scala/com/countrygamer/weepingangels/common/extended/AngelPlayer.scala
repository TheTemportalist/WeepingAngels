package com.countrygamer.weepingangels.common.extended

import com.countrygamer.cgo.wrapper.common.extended.ExtendedEntity
import com.countrygamer.weepingangels.common.WAOptions
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound

/**
 *
 *
 * @author CountryGamer
 */
class AngelPlayer(player: EntityPlayer) extends ExtendedEntity(player) {

	private var isConverting: Boolean = false
	private var angelHealth: Float = 0.0F
	private var fractionalHealth: Float = 0.0F
	private var ticksUntilNextRegen: Int = 0

	// Default Constructor
	{
		this.ticksUntilNextRegen = this.getMaxTicksPerRegen

	}

	// End Constructor

	override def saveNBTData(tagCom: NBTTagCompound): Unit = {
		tagCom.setBoolean("isConverting", this.isConverting)
		tagCom.setFloat("angelHealth", this.angelHealth)
		tagCom.setFloat("fractionalHealth", this.fractionalHealth)
		tagCom.setInteger("ticksUntilNextRegen", this.ticksUntilNextRegen)

	}

	override def loadNBTData(tagCom: NBTTagCompound): Unit = {
		this.isConverting = tagCom.getBoolean("isConverting")
		this.angelHealth = tagCom.getFloat("angelHealth")
		this.fractionalHealth = tagCom.getFloat("fractionalHealth")
		this.ticksUntilNextRegen = tagCom.getInteger("ticksUntilNextRegen")

	}

	def getMaxTicksPerRegen: Int = {
		val totalTime: Int = (WAOptions.totalConversionTime.asInstanceOf[Double] /
				WAOptions.maxAngelHealth.asInstanceOf[Double]).asInstanceOf[Int]
		totalTime
	}

	def startConversion(): Unit = {
		this.isConverting = true

		this.syncEntity()

	}

	def stopConversion(): Unit = {
		this.isConverting = false

		this.syncEntity()

	}

	def converting(): Boolean = {
		this.isConverting
	}

	def setAngelHealth(newHealth: Float): Unit = {
		this.angelHealth = newHealth

		this.syncEntity()

	}

	def getAngelHealth(): Float = {
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

		this.syncEntity()

	}

	def setTicksUntilNextRegen(newTicks: Int): Unit = {
		this.ticksUntilNextRegen = newTicks

		this.syncEntity()

	}

	def getTicksUntilNextRegen(): Int = {
		this.ticksUntilNextRegen
	}

	def decrementTicksUntilRegen(): Unit = {
		this.ticksUntilNextRegen -= 1

		this.syncEntity()

	}

	def clearRegenTicks(): Unit = {
		this.ticksUntilNextRegen = this.getMaxTicksPerRegen

		this.syncEntity()

	}

	def getOpacityForBlackout(): Float = {
		if (this.getAngelHealth > 0.0F) {
			(this.getAngelHealth / WAOptions.maxAngelHealth.asInstanceOf[Float])
		}
		else {
			0.0F
		}
	}

}
