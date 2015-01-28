package com.temportalist.weepingangels.common.entity

import com.temportalist.origin.library.common.utility.Drops
import com.temportalist.weepingangels.common.{WeepingAngels, WAOptions}
import net.minecraft.entity.{SharedMonsterAttributes, EntityAgeable}
import net.minecraft.entity.ai._
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.{NBTTagList, NBTTagCompound}
import net.minecraft.pathfinding.PathNavigateGround
import net.minecraft.world.World

/**
 *
 *
 * @author TheTemportalist 1/27/15
 */
class EntityAngel(world: World) extends EntityAgeable(world) {

	//val lightSourceKillDelay_Max: Int = 20 * 10
	var stolenInventory: Array[ItemStack] = null
	var hasProcreated: Boolean = false

	this.getNavigator.asInstanceOf[PathNavigateGround].setBreakDoors(true)
	this.tasks.addTask(2, new EntityAIAttackOnCollide(this, classOf[EntityPlayer], 1.0D, false))
	this.tasks.addTask(3, new EntityAIWatchClosest(this, classOf[EntityPlayer], 8.0F))
	this.tasks.addTask(4, new EntityAILookIdle(this))
	this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true))
	this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(
		this, classOf[EntityPlayer], true
	))

	this.experienceValue = 50
	this.isImmuneToFire = true
	this.setSize(0.6F, 2.0F)
	this.stepHeight = 2.0F
	this.setGrowingAge((WAOptions.decrepitationAge_max * 1.25).asInstanceOf[Int])

	override def createChild(ageable: EntityAgeable): EntityAgeable = {
		new EntityAngel(this.getEntityWorld)
	}

	override def entityInit(): Unit = {
		super.entityInit()

		// angry state
		this.dataWatcher.addObject(16, 0.asInstanceOf[Byte])
		// arm state
		this.dataWatcher.addObject(17, 0.asInstanceOf[Byte])
		// decrepetation texture id
		this.dataWatcher.addObject(18, -1)
		// voice throw delay
		this.dataWatcher.addObject(19, WAOptions.throwVoiceDelay_Max.asInstanceOf[Byte])

	}

	override def writeEntityToNBT(tagCom: NBTTagCompound): Unit = {
		super.writeEntityToNBT(tagCom)

		tagCom.setBoolean("hasInventory", this.hasStolenInventory)
		if (this.hasStolenInventory) {
			val tagList: NBTTagList = new NBTTagList()

			for (slotID <- 0 until this.stolenInventory.length) {
				if (this.stolenInventory(slotID) != null) {
					val stackTagCom: NBTTagCompound = new NBTTagCompound()
					stackTagCom.setInteger("slot", slotID.asInstanceOf[Byte])
					this.stolenInventory(slotID).writeToNBT(stackTagCom)
					tagList.appendTag(stackTagCom)
				}
			}

			tagCom.setTag("inventory", tagList)
			tagCom.setInteger("inventorySize", this.stolenInventory.length)
		}

		tagCom.setBoolean("hasProcreated", this.hasProcreated)

	}

	override def readEntityFromNBT(tagCom: NBTTagCompound): Unit = {
		super.readEntityFromNBT(tagCom)

		if (tagCom.getBoolean("hasInventory")) {
			this.stolenInventory = new Array[ItemStack](tagCom.getInteger("inventorySize"))

			val tagList: NBTTagList = tagCom.getTagList("inventory", 10)
			for (tagIndex <- 0 until tagList.tagCount()) {
				val stackTagCom: NBTTagCompound = tagList.getCompoundTagAt(tagIndex)
				val slotID: Int = stackTagCom.getInteger("slot") & 255
				if (slotID >= 0 && slotID < this.stolenInventory.length) {
					this.stolenInventory(slotID) = ItemStack.loadItemStackFromNBT(stackTagCom)
				}
			}

		}
		else {
			this.stolenInventory = null
		}

		this.hasProcreated = tagCom.getBoolean("hasProcreated")

	}

	def setAngryState(state: Byte): Unit = {
		this.dataWatcher.updateObject(16, state)
	}

	def getAngryState: Byte = {
		this.dataWatcher.getWatchableObjectByte(16)
	}

	def setArmState(state: Byte): Unit = {
		this.dataWatcher.updateObject(17, state)
	}

	def getArmState: Byte = {
		this.dataWatcher.getWatchableObjectByte(17)
	}

	def setLightSourceKillDelay(delay: Int): Unit = {
		this.dataWatcher.updateObject(18, delay)
	}

	def getLightSourceKillDelay: Int = {
		this.dataWatcher.getWatchableObjectInt(18)
	}

	def getVoiceThrowDelay(): Int = {
		this.dataWatcher.getWatchableObjectByte(19).asInstanceOf[Int]
	}

	def setVoiceThrowDelay(delay: Int): Unit = {
		this.dataWatcher.updateObject(19, delay.asInstanceOf[Byte])
	}

	def decrementVoiceThrowDelay(): Unit = {
		this.setVoiceThrowDelay(this.getVoiceThrowDelay() - 1)
	}

	def getNewThrowVoiceDelay(): Int = {
		this.rand.nextInt(WAOptions.throwVoiceDelay_Max - WAOptions.throwVoiceDelay_Min) +
				WAOptions.throwVoiceDelay_Min
	}

	def tryThrowVoice(target: EntityPlayer): Unit = {
		if (this.worldObj.isRemote) {
			val soundX: Double = (2 * target.posX) - this.posX
			val soundY: Double = this.posY //(2 * target.posY) - this.posY
			val soundZ: Double = (2 * target.posZ) - this.posZ
			this.worldObj.playSoundEffect(
				soundX, soundY, soundZ,
				this.getRandomMobSound(), this.getSoundVolume, this.getSoundPitch
			)
		}
	}

	def getRandomMobSound(): String = {
		WAOptions.mobSounds(this.rand.nextInt(WAOptions.mobSounds.length))
	}

	override def applyEntityAttributes(): Unit = {
		super.applyEntityAttributes()

		this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(64.0D)
		this.setSpeed(WAOptions.angelMaxSpeed)
		this.getAttributeMap.registerAttribute(SharedMonsterAttributes.attackDamage)
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(10.0D)

	}

	def setSpeed(speed: Double): Unit = {
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(speed)
	}

	override def getLivingSound: String = {
		WeepingAngels.MODID + ":stone"
	}

	override def getHurtSound: String = {
		WeepingAngels.MODID + ":light"
	}

	override def getDeathSound: String = {
		WeepingAngels.MODID + ":crumble"
	}

	def hasStolenInventory: Boolean = {
		this.stolenInventory != null
	}

	def dropStolenInventory(): Unit = {
		if (this.hasStolenInventory) {
			for (i <- 0 until this.stolenInventory.length) {
				Drops.spawnItemStack(
					this.worldObj, this.getPosition, this.stolenInventory(i), this.rand, 10
				)
			}

			this.stolenInventory = null

		}

	}

}
