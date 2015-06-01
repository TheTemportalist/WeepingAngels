package com.temportalist.weepingangels.common.tile

import com.temportalist.origin.api.common.lib.V3O
import com.temportalist.origin.api.common.tile.{IPowerable, ITileSaver}
import com.temportalist.weepingangels.common.entity.EntityAngel
import com.temportalist.weepingangels.common.lib.AngelUtility
import cpw.mods.fml.relauncher.{SideOnly, Side}
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
	var texIDs: Array[Int] = Array[Int](-1, -1)
	private var hashLocal: Int = 0
	private var isSpawning: Boolean = false

	@SideOnly(Side.CLIENT)
	private def createTexIDs(): Unit = {
		this.texIDs(0) = AngelUtility.getTextureIDFromCorruption(
			false, this.corruption, this.hashLocal)
		this.texIDs(1) = AngelUtility.getTextureIDFromCorruption(
			true, this.corruption, this.hashLocal)
	}

	def hash(): Int = {
		val state = Seq(facialState, armState)
		var out: Int = 1
		for (i: Int <- state) out = out * 31 + i
		out
	}

	def refreshTexture(): Unit = {
		this.hashLocal = this.hash()
		if (this.getWorldObj.isRemote)
			this.createTexIDs()
	}

	def getEntityTex(isAngry: Boolean): Int = {
		val id: Int = this.texIDs(if (isAngry) 1 else 0)
		if (id == -1) {
			this.createTexIDs()
			this.getEntityTex(isAngry)
		}
		else id
	}

	override def canUpdate: Boolean = false

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
		tagCom.setIntArray("texIDs", this.texIDs)
		tagCom.setBoolean("isSpawing", this.isSpawning)

		//println ("writing rotation " + this.rotation)

	}

	override def readFromNBT(tagCom: NBTTagCompound): Unit = {
		super.readFromNBT(tagCom)

		this.facialState = tagCom.getInteger("facialState")
		this.armState = tagCom.getInteger("armState")
		this.rotation = tagCom.getFloat("rotation")
		this.corruption = tagCom.getInteger("corruption")
		this.texIDs = tagCom.getIntArray("texIDs")
		this.isSpawning = tagCom.getBoolean("isSpawning")

		//println ("Loading rotation " + tagCom.hasKey("rotation") + " " + tagCom.getFloat("rotation"))

	}

	def setFacialState(state: Int): Unit = {
		this.facialState = state
		this.refreshTexture()
		this.markDirty()
	}

	def getFacialState: Int = {
		this.facialState
	}

	def setArmState(state: Int): Unit = {
		this.armState = state
		this.refreshTexture()
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
		this.refreshTexture()
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
		val angelEntity: EntityAngel = new EntityAngel(this.getWorldObj)

		val pos: V3O = new V3O(this) + V3O.CENTER
		angelEntity.setPositionAndRotation(pos.x, pos.y, pos.z, this.getRotation, 0.0F)

		if (!this.getWorldObj.isRemote) {
			this.getWorldObj.spawnEntityInWorld(angelEntity)
		}

		this.isSpawning = true

		new V3O(this).setBlock(this.getWorldObj, Blocks.stone_slab, 0)

	}

	def isComingToLife: Boolean = {
		this.isSpawning
	}

	def canEqual(other: Any): Boolean = other.isInstanceOf[TEStatue]

}
