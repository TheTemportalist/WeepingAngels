package com.temportalist.weepingangels.common.entity

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.InputStream
import java.util
import java.util.Random
import javax.imageio.ImageIO

import com.temportalist.origin.library.common.utility.{Drops, Teleport}
import com.temportalist.origin.wrapper.common.extended.ExtendedEntityHandler
import com.temportalist.weepingangels.common.extended.AngelPlayer
import com.temportalist.weepingangels.common.init.{WABlocks, WAItems}
import com.temportalist.weepingangels.common.lib.AngelUtility
import com.temportalist.weepingangels.common.{WAOptions, WeepingAngels}
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.{SimpleTexture, TextureUtil}
import net.minecraft.entity._
import net.minecraft.entity.ai._
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.init.Items
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.util._
import net.minecraft.world.{EnumDifficulty, EnumSkyBlock, World}
import org.apache.commons.io.IOUtils

/**
 *
 *
 * @author TheTemportalist
 */
class EntityWeepingAngel(world: World) extends EntityAgeable(world) {

	//val lightSourceKillDelay_Max: Int = 20 * 10
	var stolenInventory: Array[ItemStack] = null
	var hasProcreated: Boolean = false

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
		this.stepHeight = 2.0F
		this.setGrowingAge((WAOptions.decrepitationAge_max * 1.25).asInstanceOf[Int])

	}

	// End Constructor

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

			if (WAOptions.angelThrowsVoice) {
				if (this.getVoiceThrowDelay() <= 0) {
					val lookingPlayer: EntityPlayer = AngelUtility
							.getEntityLooking(this.worldObj, this, this.boundingBox, 64D,
					            classOf[EntityPlayer]).asInstanceOf[EntityPlayer]
					if (lookingPlayer != null) {
						this.tryThrowVoice(lookingPlayer)
					}
					this.setVoiceThrowDelay(this.getNewThrowVoiceDelay())
				}
				else {
					this.decrementVoiceThrowDelay()
				}
			}

		}
		else {
			// Angel can move
			var speed: Double = WAOptions.angelMaxSpeed
			if (this.isChild) speed = speed * 2
			if (this.getSpeed() != speed) {
				this.setSpeed(speed)
			}

		}

		/*
		val canRemoveLight: Boolean = WAOptions.angelsLookForTorches && canBeSeen &&
				(if (this.isInSkylight) !this.worldObj.isDaytime else true)
		if (canRemoveLight) {
			val coordsOfSource: Vec3 = this.findNearestTorch()

			if (coordsOfSource != null) {
				val ticksUntilSourceBreak: Int = this.getLightSourceKillDelay
				if (ticksUntilSourceBreak <= 0) {

					this.breakLightSource(coordsOfSource)

					//this.setLightSourceKillDelay(this.lightSourceKillDelay_Max)

				}
				else {
					this.setLightSourceKillDelay(ticksUntilSourceBreak - 1)

				}

			}

		}
		*/

		if (!canBeSeen) {
			this.changeAngelMovement()
			if (!this.hasProcreated && this.entityToAttack == null &&
					this.findPlayerToAttack() == null) {
				if (this.getGrowingAge >= 24000 * 4 && this.rand.nextInt(50) == 0) {
					this.procreate(null)
					this.hasProcreated = true
				}
			}
		}

		super.onUpdate()

	}

	override def onLivingUpdate(): Unit = {
		this.updateArmSwingProgress()

		val brightness: Float = this.getBrightness(1.0F)
		if (brightness > 0.5F) {
			this.entityAge += 2
		}

		super.onLivingUpdate()

		if (this.getTextureID() < 0) {
			this.onAgeChanged()
		}
	}

	def getSpeed(): Double = {
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getBaseValue
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

		Drops.spawnItemStack(
			this.worldObj, x, y, z, new ItemStack(block, 1, meta), this.rand, 10
		)

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
				case player: EntityPlayerMP =>
					if (WAOptions.angelsCanConvertPlayers &&
							this.rand.nextInt(100) < WAOptions.conversionChance) {
						val angelPlayer: AngelPlayer = ExtendedEntityHandler
								.getExtended(player, classOf[AngelPlayer]).asInstanceOf[AngelPlayer]

						if (!angelPlayer.converting()) {
							angelPlayer.startConversion()
							angelPlayer.setAngelHealth(0.0F)
							angelPlayer.clearRegenTicks()
							this.rejuvinate(1, 0.25D)
						}

						didAlternateAction = true
						entityIsConvertting = true

					}

					if (!didAlternateAction && WAOptions.angelsCanTeleportPlayers &&
							this.rand.nextInt(100) < WAOptions.teleportationChance) {
						Teleport.toPointRandom(
							player, WAOptions.teleportationMinRange, WAOptions.teleportationMaxRange
						)
						this.rejuvinate(1, 1.0D)
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

	def rejuvinate(times: Int, factor: Double): Unit = {
		val crement: Int = (2000 * factor).asInstanceOf[Int]
		for (i <- 0 until times) {
			var nextAge: Int = 0
			if (this.isChild) {
				nextAge = this.getGrowingAge + crement
				if (nextAge > 0) {
					nextAge = 0
				}
			}
			else {
				nextAge = this.getGrowingAge - crement
				if (nextAge < 0) {
					nextAge = 0
				}
			}
			this.setGrowingAge(nextAge)
		}
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
				Drops.spawnItemStack(
					this.worldObj, this.posX, this.posY, this.posZ, this.stolenInventory(i),
					this.rand, 10
				)
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

	override def knockBack(attackingEntity: Entity, amount: Float, x: Double, z: Double): Unit = {
	}

	override def dropFewItems(hitRecentlyByPlayer: Boolean, looting: Int): Unit = {
		if (this.rand.nextInt(100) < 20) {
			val angelTearStack: ItemStack = new ItemStack(WAItems.angelTear, 1, 0)
			val tagCom: NBTTagCompound = new NBTTagCompound
			val tearType: String = if (this.rand.nextBoolean()) "Teleportation"
			else "Time Manipulation"
			if (tearType.equals("Teleportation")) {
				tagCom.setInteger("uses", 5)
			}
			else if (tearType.equals("Time Manipulation")) {
				tagCom.setInteger("uses", 1)
			}
			tagCom.setString("type", tearType)
			angelTearStack.setTagCompound(tagCom)
			this.entityDropItem(angelTearStack, 0.0F)
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

	override def createChild(otherAngel: EntityAgeable): EntityAgeable = {
		new EntityWeepingAngel(this.worldObj)
	}

	def procreate(otherAngel: EntityWeepingAngel): Unit = {
		val ageable: EntityAgeable = this.createChild(otherAngel)

		if (ageable != null) {
			//this.setGrowingAge(6000)
			this.entityToAttack = null
			if (otherAngel != null) {
				//otherAngel.setGrowingAge(6000)
				otherAngel.entityToAttack = null
			}
			ageable.setGrowingAge(-24000 * 4)
			//ageable.setSize(0.3F, 1.0F)

			ageable.setLocationAndAngles(
				this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch
			)

			this.worldObj.spawnEntityInWorld(ageable)

		}

	}

	override def interact(player: EntityPlayer): Boolean = {
		val itemstack: ItemStack = player.inventory.getCurrentItem
		if (itemstack != null && itemstack.getItem == Items.spawn_egg) {
			if (!this.worldObj.isRemote) {
				val oclass: Class[_] = EntityList.getClassFromID(itemstack.getItemDamage)
				if (oclass != null && oclass.isAssignableFrom(this.getClass)) {
					val entityageable: EntityAgeable = this.createChild(this)
					if (entityageable != null) {
						entityageable.setGrowingAge(-24000 * 4)
						entityageable
								.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F)
						this.worldObj.spawnEntityInWorld(entityageable)
						if (itemstack.hasDisplayName) {
							entityageable.setCustomNameTag(itemstack.getDisplayName)
						}
						if (!player.capabilities.isCreativeMode) {
							itemstack.stackSize -= 1
							if (itemstack.stackSize <= 0) {
								player.inventory
										.setInventorySlotContents(player.inventory.currentItem,
								            null.asInstanceOf[ItemStack])
							}
						}
					}
				}
			}
			true
		}
		else {
			false
		}
	}

	override def setGrowingAge(tickAge: Int): Unit = {
		val wasChild: Boolean = this.isChild || tickAge <= -96000

		super.setGrowingAge(tickAge)

		if (this.isChild) {
			this.setSize(0.3F, 1.0F)
		}

		if (!wasChild && this.getGrowingAge <= 0) {
			this.setDead()
		}

		if (wasChild && this.getGrowingAge >= 0) {
			this.setGrowingAge(WAOptions.decrepitationAge_max)
		}

		this.onAgeChanged()

	}

	@SideOnly(Side.CLIENT)
	def onAgeChanged() {
		val image: BufferedImage = this.decrepitize(WAOptions.weepingAngel1)

		try {
			val obj: SimpleTexture = new SimpleTexture(null)
			obj.deleteGlTexture()
			TextureUtil.uploadTextureImageAllocate(obj.getGlTextureId, image, false, false)
			this.setTextureID(obj.getGlTextureId)
		}
		catch {
			case e: Exception =>
		}
	}

	@SideOnly(Side.CLIENT)
	def decrepitize(angelTex: ResourceLocation): BufferedImage = {
		val stream: InputStream = Minecraft.getMinecraft.getResourceManager.getResource(angelTex)
				.getInputStream
		val image: BufferedImage = ImageIO.read(stream)
		IOUtils.closeQuietly(stream)

		val corruption: Int = AngelUtility.getDecrepitation(this.getGrowingAge)
		for (i <- 1 to corruption) {
			val rand: Random = new Random(this.hashCode() * i)
			val x: Int = rand.nextInt(image.getWidth)
			val y: Int = rand.nextInt(image.getHeight)
			image.setRGB(x, y, new Color(image.getRGB(x, y)).darker().getRGB)
		}

		image
	}

	def getTextureID(): Int = {
		this.dataWatcher.getWatchableObjectInt(18)
	}

	def setTextureID(id: Int): Unit = {
		this.dataWatcher.updateObject(18, id)
	}

}
