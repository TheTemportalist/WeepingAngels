package com.countrygamer.weepingangels.common.tile

import com.countrygamer.cgo.wrapper.common.tile.TEWrapper
import net.minecraft.nbt.NBTTagCompound

/**
 *
 *
 * @author CountryGamer
 */
class TileEntityStatue() extends TEWrapper("Statue") {

	private var facialState: Int = 0
	private var armState: Int = 0
	private var rotation: Float = 0.0F

	// Default Constructor
	{

	}

	// End Constructor

	/**
	 * Triggered when an EntityWeepingAngel touches this statue
	 *
	 */
	// TODO
	def touchedByAngel(): Unit = {
		this.comeToLife()
	}

	override def writeToNBT(tagCom: NBTTagCompound): Unit = {
		super.writeToNBT(tagCom)

		tagCom.setInteger("facialState", this.facialState)
		tagCom.setInteger("armState", this.armState)
		tagCom.setFloat("rotation", this.rotation)

	}

	override def readFromNBT(tagCom: NBTTagCompound): Unit = {
		super.readFromNBT(tagCom)

		this.facialState = tagCom.getInteger("facialState")
		this.armState = tagCom.getInteger("armState")
		this.rotation = tagCom.getFloat("rotation")

	}

	def setFacialState(state: Int): Unit = {
		this.facialState = state
	}

	def getFacialState(): Int = {
		return this.facialState
	}

	def setArmState(state: Int): Unit = {
		this.armState = state
	}

	def getArmState(): Int = {
		return this.armState
	}

	def setRotation(rot: Float): Unit = {
		this.rotation = rot
	}

	def getRotation(): Float = {
		return this.rotation
	}

	/**
	 * Called when the power is not what it was
	 */
	override def onPowerChanged: Unit = {
		if (this.getBlockMetadata == 0 && this.isPowered(true)) {
			this.comeToLife()
		}
	}

	def comeToLife(): Unit = {
		// TODO
	}

}
