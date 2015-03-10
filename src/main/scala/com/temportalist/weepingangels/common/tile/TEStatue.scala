package com.temportalist.weepingangels.common.tile

import com.temportalist.origin.api.tile.{IPowerable, ITileSaver}
import com.temportalist.origin.library.common.lib.vec.V3O
import com.temportalist.weepingangels.common.entity.EntityAngel
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity

/**
 *
 *
 * @author TheTemportalist
 */
class TEStatue() extends TileEntity with ITileSaver with IPowerable {

	private var facialState: Int = 0
	private var armState: Int = 0
	private var rotation: Float = 0.0F
	private var corruption: Int = 0
	private var isSpawning: Boolean = false

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
		tagCom.setInteger("corruption", this.corruption)
		tagCom.setBoolean("isSpawing", this.isSpawning)

		//println ("writing rotation " + this.rotation)

	}

	override def readFromNBT(tagCom: NBTTagCompound): Unit = {
		super.readFromNBT(tagCom)

		this.facialState = tagCom.getInteger("facialState")
		this.armState = tagCom.getInteger("armState")
		this.rotation = tagCom.getFloat("rotation")
		this.corruption = tagCom.getInteger("corruption")
		this.isSpawning = tagCom.getBoolean("isSpawning")

		//println ("Loading rotation " + tagCom.hasKey("rotation") + " " + tagCom.getFloat("rotation"))

	}

	def setFacialState(state: Int): Unit = {
		this.facialState = state
		this.markDirty()
	}

	def getFacialState: Int = {
		this.facialState
	}

	def setArmState(state: Int): Unit = {
		this.armState = state
		this.markDirty()
	}

	def getArmState: Int = {
		this.armState
	}

	def setRotation(rot: Float): Unit = {
		this.rotation = rot
		this.markDirty()
	}

	def getRotation: Float = {
		//println (this.rotation)
		this.rotation
	}

	def setCorruption(corr: Int): Unit = {
		this.corruption = corr
		this.markDirty()
	}

	def getCorruption(): Int = {
		this.corruption
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

		val pos: V3O = new V3O(this) + V3O.CENTER
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
