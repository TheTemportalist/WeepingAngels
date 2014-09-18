package com.countrygamer.weepingangels.common.entity

import java.util

import com.countrygamer.cgo.common.lib.util.{UtilDrops, UtilVector}
import com.countrygamer.cgo.wrapper.common.extended.ExtendedEntityHandler
import com.countrygamer.weepingangels.common.extended.AngelPlayer
import com.countrygamer.weepingangels.common.init.WABlocks
import com.countrygamer.weepingangels.common.lib.AngelUtility
import com.countrygamer.weepingangels.common.{WAOptions, WeepingAngels}
import net.minecraft.block.Block
import net.minecraft.entity._
import net.minecraft.entity.ai._
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.util._
import net.minecraft.world.{EnumDifficulty, EnumSkyBlock, World}

/**
 *
 *
 * @author CountryGamer
 */
class EntityWeepingAngel(world: World) extends EntityCreature(world) {

	val lightSourceKillDelay_Max: Int = 20 * 10
	var stolenInventory: Array[ItemStack] = null

	// Default Constructor
	{
		this.experienceValue = 50
		this.isImmuneToFire = true

		this.getNavigator.setBreakDoors(true)
		this.tasks.addTask(2, new EntityAIAttackOnCollide(this, classOf[EntityPlayer], 1.0D, false))
		this.tasks.addTask(3, new EntityAIWatchClosest(this, classOf[EntityPlayer], 8.0F))
		this.tasks.addTask(4, new EntityAILookIdle(this))
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true))
		this.targetTasks.addTask(2,
			new EntityAINearestAttackableTarget(this, classOf[EntityPlayer], 0, true))

		this.setSize(0.6F, 2.0F)
		this.stepHeight = 1.0F

	}

	// End Constructor

	override def entityInit(): Unit = {
		super.entityInit()

		this.dataWatcher.addObject(16, 0.asInstanceOf[Byte])
		this.dataWatcher.addObject(17, 0.asInstanceOf[Byte])
		this.dataWatcher.addObject(18, this.lightSourceKillDelay_Max)

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

	override def applyEntityAttributes(): Unit = {
		super.applyEntityAttributes()

		this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(64.0D)
		this.setSpeed(WAOptions.angelMaxSpeed)
		this.getAttributeMap.registerAttribute(SharedMonsterAttributes.attackDamage)
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(10.0D)

	}

	override def getLivingSound: String = {
		WeepingAngels.pluginID + ":stone"
	}

	override def getHurtSound: String = {
		WeepingAngels.pluginID + ":light"
	}

	override def getDeathSound: String = {
		WeepingAngels.pluginID + ":crumble"
	}

	override def isAIEnabled: Boolean = {
		true
	}

	override def onUpdate(): Unit = {
		// Kill if neccessary
		if (!this.worldObj.isRemote && this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL) {
			this.setDead()
		}

		// Check movement variable which shouldnt change
		//if (this.moveStrafing != 0.0F)
		//	this.moveStrafing = 0.0F
		//if (this.moveForward != 0.0F)
		//	this.moveForward = 0.0F
		if (this.isJumping)
			this.isJumping = false

		// Get whether angel can be seen
		val canBeSeen: Boolean = AngelUtility
				.canBeSeen_Multiplayer(this.worldObj, this, this.boundingBox, 64D)

		if (canBeSeen) {
			// Angel is quantum locked

			this.setSpeed(0.0D)
			this.rotationPitch = this.prevRotationPitch
			this.rotationYaw = this.prevRotationYaw
			this.rotationYawHead = this.prevRotationYawHead

		}
		else {
			// Angel can move
			this.setSpeed(WAOptions.angelMaxSpeed)

		}

		if (WAOptions.angelsLookForTorches && canBeSeen) {
			if (!(this.worldObj.isDaytime && this.isInSkylight)) {

				val coordsOfSource: Vec3 = this.findNearestTorch()

				if (coordsOfSource != null) {
					val ticksUntilSourceBreak: Int = this.getLightSourceKillDelay
					if (ticksUntilSourceBreak <= 0) {

						this.breakLightSource(coordsOfSource)

						this.setLightSourceKillDelay(this.lightSourceKillDelay_Max)

					}
					else {
						this.setLightSourceKillDelay(ticksUntilSourceBreak - 1)

					}

				}

			}
		}

		if (!canBeSeen)
			this.changeAngelMovement()

		super.onUpdate()

	}

	override def onLivingUpdate(): Unit = {
		this.updateArmSwingProgress()

		val brightness: Float = this.getBrightness(1.0F)
		if (brightness > 0.5F) {
			this.entityAge += 2
		}

		super.onLivingUpdate()
	}

	def setSpeed(speed: Double): Unit = {
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(speed)
	}

	override def findPlayerToAttack(): Entity = {
		this.worldObj.getClosestPlayerToEntity(this, 64.0D) // range
	}

	def isInSkylight: Boolean = {
		this.worldObj.canBlockSeeTheSky(
			MathHelper.floor_double(this.posX),
			MathHelper.floor_double(this.posY),
			MathHelper.floor_double(this.posZ)
		)
	}

	def findNearestTorch(): Vec3 = {
		val radius: Double = 16.0D
		val maxDistance: Double = radius * radius * radius

		val minX: Int = (MathHelper.floor_double(this.posX) - radius).asInstanceOf[Int]
		val minY: Int = (MathHelper.floor_double(this.boundingBox.minY) - radius).asInstanceOf[Int]
		val minZ: Int = (MathHelper.floor_double(this.posZ) - radius).asInstanceOf[Int]
		val maxX: Int = (MathHelper.floor_double(this.posX) + radius).asInstanceOf[Int]
		val maxY: Int = (MathHelper.floor_double(this.boundingBox.minY) + radius).asInstanceOf[Int]
		val maxZ: Int = (MathHelper.floor_double(this.posZ) + radius).asInstanceOf[Int]

		val validSources: java.util.List[Vec3] = new util.ArrayList[Vec3]()
		for (y <- minY to maxY) {
			for (x <- minX to maxX) {
				for (z <- minZ to maxZ) {
					if (this.isValidLightSource(x, y, z, maxDistance)) {
						validSources.add(Vec3.createVectorHelper(x, y, z))
					}
				}
			}
		}

		var closest: Vec3 = null
		var closestDistance: Double = maxDistance
		for (i <- 0 until validSources.size()) {
			val coords: Vec3 = validSources.get(i)

			val horizontalDistance: Double =
				MathHelper.sqrt_double(
					Math.pow(this.posX - coords.xCoord, 2) +
							Math.pow(this.posZ - coords.zCoord, 2))
			val distance: Double =
				MathHelper.sqrt_double(
					Math.pow(this.boundingBox.minY - coords.yCoord, 2) +
							Math.pow(horizontalDistance, 2))

			if (distance < closestDistance) {
				closest = coords
				closestDistance = distance
			}

		}

		closest
	}

	def isValidLightSource(x: Int, y: Int, z: Int, maxDistance: Double): Boolean = {

		val horizontalDistance: Double =
			MathHelper.sqrt_double(
				Math.pow(this.posX - x, 2) + Math.pow(this.posZ - z, 2))
		val distance: Double =
			MathHelper.sqrt_double(
				Math.pow(this.boundingBox.minY - y, 2) + Math.pow(horizontalDistance, 2))

		if (distance <= maxDistance) {

			val block: Block = this.worldObj.getBlock(x, y, z)

			if (block.getLightValue > 0) {
				return true
			}

		}

		false
	}

	def breakLightSource(coords: Vec3): Unit = {

		val x: Int = coords.xCoord.asInstanceOf[Int]
		val y: Int = coords.yCoord.asInstanceOf[Int]
		val z: Int = coords.zCoord.asInstanceOf[Int]

		val block: Block = this.worldObj.getBlock(x, y, z)
		val meta: Int = this.worldObj.getBlockMetadata(x, y, z)

		this.worldObj.setBlockToAir(x, y, z)

		UtilDrops.spawnItemStack(this.worldObj, x, y, z, new ItemStack(block, 1, meta), this.rand,
			10)

	}

	def changeAngelMovement(): Unit = {
		if (this.getAttackTarget != null) {
			val difX: Double = Math.abs(this.posX - this.getAttackTarget.posX)
			val difY: Double = Math.abs(this.posY - this.getAttackTarget.posY)
			val difZ: Double = Math.abs(this.posZ - this.getAttackTarget.posZ)

			if (difX <= 5.0D && difY <= 5.0D && difZ <= 5.0D) {
				this.setAngryState(1)
				this.setArmState(2)
			}
			else {
				this.setAngryState(0)
				if (difX <= 15.0D && difY <= 15.0D && difZ <= 15.0D) {
					this.setArmState(1)
				}
				else {
					this.setArmState(0)
				}
			}
		}

	}

	override def attackEntityAsMob(entity: Entity): Boolean = {

		if (entity != null &&
				!AngelUtility.canBeSeen_Multiplayer(this.worldObj, this, this.boundingBox, 64D)) {
			var didAlternateAction: Boolean = false
			var entityIsConvertting: Boolean = false
			entity match {
				case player: EntityPlayer =>
					if (WAOptions.angelsCanConvertPlayers &&
							this.rand.nextInt(100) < WAOptions.conversionChance) {
						val angelPlayer: AngelPlayer = ExtendedEntityHandler
								.getExtended(player, classOf[AngelPlayer]).asInstanceOf[AngelPlayer]

						if (!angelPlayer.converting()) {
							angelPlayer.startConversion()
							angelPlayer.setAngelHealth(0.0F)
							angelPlayer.clearRegenTicks()

						}

						didAlternateAction = true
						entityIsConvertting = true

					}

					if (!didAlternateAction && WAOptions.angelsCanTeleportPlayers &&
							this.rand.nextInt(100) < WAOptions.teleportationChance) {
						UtilVector.teleportPlayer(player, WAOptions.teleportationMinRange,
							WAOptions.teleportationMaxRange, player.posX, player.posZ, true, true)
						this.worldObj.playSoundAtEntity(player,
							WeepingAngels.pluginID + ":teleport_activate", 1.0F, 1.0F)
						didAlternateAction = true

					}

				case _ =>
			}

			if (!didAlternateAction && !entityIsConvertting) {
				val damage: Float = this.getEntityAttribute(SharedMonsterAttributes.attackDamage)
						.getAttributeValue.asInstanceOf[Float]
				if (WAOptions.angelOverridesPlayerArmor) {
					entity match {
						case living: EntityLivingBase =>
							living.setHealth(living.getHealth - damage)
							return true
						case _ =>
					}
				}
				return entity.attackEntityFrom(DamageSource.causeMobDamage(this), damage)
			}

			return true
		}

		false
	}

	override def attackEntity(entity: Entity, distanceToEntity: Float): Unit = {

		if (entity != null && distanceToEntity < 2.0D) {
			this.attackEntityAsMob(entity)
		}

	}

	override def onDeath(source: DamageSource): Unit = {
		super.onDeath(source)

		if (this.hasStolenInventory) {
			this.dropStolenInventory()
		}

	}

	def hasStolenInventory: Boolean = {
		this.stolenInventory != null
	}

	def dropStolenInventory(): Unit = {
		if (this.hasStolenInventory) {
			for (i <- 0 until this.stolenInventory.length) {
				UtilDrops.spawnItemStack(this.worldObj, this.posX, this.posY, this.posZ,
					this.stolenInventory(i), this.rand, 10)
			}

			this.stolenInventory = null

		}

	}

	def setStolenInventory(inventory: Array[ItemStack]): Unit = {
		this.stolenInventory = inventory

	}

	override def attackEntityFrom(source: DamageSource, damage: Float): Boolean = {
		if (AngelUtility.canAttackEntityFrom(this.worldObj, source, damage)) {
			super.attackEntityFrom(source, damage)
			true
		}
		else {
			false
		}
	}

	override def dropRareDrop(par1: Int): Unit = {
		this.dropItem(Item.getItemFromBlock(WABlocks.statue), 1)
	}

	override def getCanSpawnHere: Boolean = {
		this.worldObj.difficultySetting != EnumDifficulty.PEACEFUL &&
				this.boundingBox.minY <= WAOptions.maximumSpawnHeight &&
				this.isValidLightLevel && super.getCanSpawnHere()
	}

	def isValidLightLevel: Boolean = {
		val x: Int = MathHelper.floor_double(this.posX)
		val y: Int = MathHelper.floor_double(this.boundingBox.minY)
		val z: Int = MathHelper.floor_double(this.posZ)

		if (this.worldObj.getSavedLightValue(EnumSkyBlock.Sky, x, y, z) > this.rand.nextInt(32)) {
			false
		}
		else {
			var lightLevel: Int = this.worldObj.getBlockLightValue(x, y, z)

			if (this.worldObj.isThundering()) {
				val savedSkylightSubtracted: Int = this.worldObj.skylightSubtracted
				this.worldObj.skylightSubtracted = 10
				lightLevel = this.worldObj.getBlockLightValue(x, y, z)
				this.worldObj.skylightSubtracted = savedSkylightSubtracted
			}

			lightLevel <= this.rand.nextInt(WAOptions.maxLightLevelForSpawn)
		}
	}

}
