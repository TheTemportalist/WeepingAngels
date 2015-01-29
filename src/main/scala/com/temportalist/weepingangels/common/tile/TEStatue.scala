package com.temportalist.weepingangels.common.tile

import com.temportalist.origin.library.common.lib.vec.V3O
import com.temportalist.origin.wrapper.common.tile.TEWrapper
import com.temportalist.weepingangels.common.entity.EntityAngel
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound

/**
 *
 *
 * @author TheTemportalist
 */
class TEStatue() extends TEWrapper("Statue") {

	private var facialState: Int = 0
	private var armState: Int = 0
	private var rotation: Float = 0.0F
	private var isSpawning: Boolean = false

	// Default Constructor
	{

	}

	// End Constructor

	/**
	 * Triggered when an EntityAngel touches this statue
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
		val angelEntity: EntityAngel = new EntityAngel(this.getWorld)

		val pos: V3O = new V3O(this).add(V3O.CENTER)
		angelEntity.setPositionAndRotation(pos.x, pos.y, pos.z, this.getRotation, 0.0F)

		if (!this.getWorld.isRemote) {
			this.getWorld.spawnEntityInWorld(angelEntity)
		}

		this.isSpawning = true

		this.getWorld.setBlockState(this.getPos, Blocks.stone_slab.getDefaultState)

	}

	def isComingToLife: Boolean = {
		this.isSpawning
	}

}