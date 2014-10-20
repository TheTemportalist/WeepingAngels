package com.temportalist.weepingangels.common.tile

import com.temportalist.origin.wrapper.common.tile.TEWrapper
import com.temportalist.weepingangels.common.entity.EntityWeepingAngel
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound

/**
 *
 *
 * @author TheTemportalist
 */
class TileEntityStatue() extends TEWrapper("Statue") {

	private var facialState: Int = 0
	private var armState: Int = 0
	private var rotation: Float = 0.0F
	private var isSpawning: Boolean = false

	// Default Constructor
	{

	}

	// End Constructor

	/**
	 * Triggered when an EntityWeepingAngel touches this statue
	 *
	 */
	def touchedByAngel(): Unit = {
		this.comeToLife()
	}

	override def writeToNBT(tagCom: NBTTagCompound): Unit = {
		super.writeToNBT(tagCom)

		tagCom.setInteger("facialState", this.facialState)
		tagCom.setInteger("armState", this.armState)
		tagCom.setFloat("rotation", this.rotation)
		tagCom.setBoolean("isSpawing", this.isSpawning)

	}

	override def readFromNBT(tagCom: NBTTagCompound): Unit = {
		super.readFromNBT(tagCom)

		this.facialState = tagCom.getInteger("facialState")
		this.armState = tagCom.getInteger("armState")
		this.rotation = tagCom.getFloat("rotation")
		this.isSpawning = tagCom.getBoolean("isSpawning")

	}

	def setFacialState(state: Int): Unit = {
		this.facialState = state
	}

	def getFacialState: Int = {
		this.facialState
	}

	def setArmState(state: Int): Unit = {
		this.armState = state
	}

	def getArmState: Int = {
		this.armState
	}

	def setRotation(rot: Float): Unit = {
		this.rotation = rot
	}

	def getRotation: Float = {
		this.rotation
	}

	/**
	 * Called when the power is not what it was
	 */
	override def onPowerChanged: Unit = {
		if (this.getBlockMetadata == 0 && this.isPowered(checkState = true)) {
			this.comeToLife()
		}
	}

	def comeToLife(): Unit = {
		val angelEntity: EntityWeepingAngel = new EntityWeepingAngel(this.getWorldObj)

		angelEntity.setPositionAndRotation(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5,
			this.getRotation, 0.0F)

		if (!this.getWorldObj.isRemote) {
			this.getWorldObj.spawnEntityInWorld(angelEntity)
		}

		this.isSpawning = true

		this.getWorldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, Blocks.stone_slab)

	}

	def isComingToLife: Boolean = {
		this.isSpawning
	}

}
