package com.temportalist.weepingangels.common.lib

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.InputStream
import java.util
import java.util.Random
import javax.imageio.ImageIO

import com.temportalist.origin.api.client.utility.Rendering
import com.temportalist.origin.api.common.lib.{LogHelper, V3O}
import com.temportalist.weepingangels.common.entity.EntityAngel
import com.temportalist.weepingangels.common.{WAOptions, WeepingAngels}
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.block.Block
import net.minecraft.client.renderer.texture.{SimpleTexture, TextureUtil}
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util._
import net.minecraft.world.{EnumDifficulty, EnumSkyBlock, World}
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.living.EnderTeleportEvent
import org.apache.commons.io.IOUtils

/**
 *
 *
 * @author TheTemportalist
 */
object AngelUtility {

	def getVectorForEntity(entity: Entity): V3O =
		new V3O(entity.posX, entity.boundingBox.minY, entity.posZ)

	def getLightLevel(entity: Entity): Int =
		this.getLightLevel(entity.worldObj, this.getVectorForEntity(entity))

	def getLightLevel(world: World, pos: V3O): Int = {
		val isThundering = world.isThundering
		val skylightSubtracted = world.skylightSubtracted
		if (isThundering) world.skylightSubtracted = 10
		// todo move this to V3O
		val blockLightLevel = world.getBlockLightValue(pos.x_i(), pos.y_i(), pos.z_i())
		if (isThundering) world.skylightSubtracted = skylightSubtracted
		blockLightLevel
	}

	def isValidLightLevelForMobSpawn(entity: Entity, minLightLevel: Int): Boolean =
		this.isValidLightLevelForMobSpawn(
			entity.worldObj, this.getVectorForEntity(entity), minLightLevel)

	def isValidLightLevelForMobSpawn(world: World, pos: V3O, minLightLevel: Int): Boolean = {
		if (pos.getSavedLightValue(world, EnumSkyBlock.Sky) > world.rand.nextInt(32)) false
		else {
			this.getLightLevel(world, pos) <= world.rand.nextInt(minLightLevel)
		}
	}

	def canBeSeen_Multiplayer(world: World, entity: EntityLivingBase, boundingBox: AxisAlignedBB,
			radius: Double): Boolean = {

		if (this.getLightLevel(entity) <= 1) {
			return false
		}

		if (boundingBox == null) {
			LogHelper.warn(WeepingAngels.MODID,
				"Error: null bounding box on " + entity.getCommandSenderName)
			return true
		}

		this.canBeSeen(world, entity, boundingBox, radius, classOf[EntityPlayer]) ||
				this.canBeSeen(world, entity, boundingBox, radius, classOf[EntityAngel])
	}

	def canBeSeen(world: World, entity: EntityLivingBase, boundingBox: AxisAlignedBB,
			radius: Double, clazz: Class[_ <: EntityLivingBase]): Boolean = {
		/*
		val entityList: java.util.List[_] = world
				.getEntitiesWithinAABB(clazz, boundingBox.expand(radius, radius, radius))
		for (i <- 0 until entityList.size()) {
			val e: EntityLivingBase = entityList.get(i).asInstanceOf[EntityLivingBase]
			if (this.isInFieldOfViewOf(e, entity)) {
				e match {
					case angel: EntityAngel =>
						if (angel.getArmState > 0)
							return true
					case _ =>
						return true
				}
			}
		}
		false
		*/
		!this.getLookingList(world, entity, boundingBox, radius, clazz).isEmpty
	}

	def getLookingList(world: World, entity: EntityLivingBase, boundingBox: AxisAlignedBB,
			radius: Double, clazz: Class[_ <: EntityLivingBase]): util.List[EntityLivingBase] = {
		val entityList: util.List[_] = world.getEntitiesWithinAABB(
			clazz, boundingBox.expand(radius, radius, radius)
		)
		val lookingList: util.List[EntityLivingBase] = new util.ArrayList[EntityLivingBase]()

		for (i <- 0 until entityList.size()) {
			val e: EntityLivingBase = entityList.get(i).asInstanceOf[EntityLivingBase]
			if (this.isInFieldOfViewOf(e, entity)) {
				e match {
					case angel: EntityAngel =>
						if (angel.getArmState > 0)
							lookingList.add(angel)
					case _ =>
						lookingList.add(e)
				}
			}
		}
		lookingList
	}

	/**
	 * Will return the closest entity to the passed entity that can see the passed entity
	 * @param world
	 * @param entity
	 * @param boundingBox
	 * @param radius
	 * @param clazz
	 * @return
	 */
	def getEntityLooking(world: World, entity: EntityLivingBase, boundingBox: AxisAlignedBB,
			radius: Double, clazz: Class[_ <: EntityLivingBase]): EntityLivingBase = {
		if (boundingBox == null) return null
		val lookingList: util.List[EntityLivingBase] = this.getLookingList(
			world, entity, boundingBox, radius, clazz
		)

		if (!lookingList.isEmpty) {
			var retEntity: EntityLivingBase = null
			var retDistance: Float = radius.asInstanceOf[Float]
			var lookingDistance: Float = 0.0F
			for (i <- 0 until lookingList.size()) {
				lookingList.get(i) match {
					case player: EntityPlayer =>
						lookingDistance = entity.getDistanceToEntity(lookingList.get(i))
						if (lookingDistance < retDistance) {
							retDistance = lookingDistance
							retEntity = lookingList.get(i)
						}
					case _ =>
				}
			}
			retEntity
		}
		else {
			null
		}
	}

	def isInFieldOfViewOf(viewer: EntityLivingBase, viewed: EntityLivingBase): Boolean = {
		val entityLookVec: V3O = new V3O(viewer.getLook(1.0F))
		val thisEntityPos: V3O = new V3O(viewed) + new V3O(0, viewed.height, 0)
		val entityPos: V3O = new V3O(viewer) + new V3O(0, viewer.getEyeHeight, 0)
		val differenceVec: V3O = thisEntityPos - entityPos

		val lengthVec: Double = differenceVec.magnitude()

		val differenceVec_normal = differenceVec.normalize()

		val d1: Double = entityLookVec.dotProduct(differenceVec_normal)

		if (d1 > (1.0D - 0.025D) / lengthVec && viewed.canEntityBeSeen(viewer)) {
			true
		}
		else {
			false
		}
	}

	def getNearbyAngels(entity: EntityLivingBase): util.List[_] = {
		entity.worldObj.getEntitiesWithinAABB(classOf[EntityAngel],
			entity.getBoundingBox.expand(20D, 20D, 20D))
	}

	def canAttackEntityFrom(world: World, source: DamageSource, damage: Float): Boolean = {
		if (source != null) {
			val validSources: Boolean =
				source == DamageSource.generic ||
						source == DamageSource.magic ||
						source.damageType.equals("player")

			if (!validSources) {
				return false
			}



			source.getSourceOfDamage match {
				case player: EntityPlayer =>
					var canDamage: Boolean = false
					val heldStack: ItemStack = player.inventory.getCurrentItem

					if (WAOptions.angelsOnlyHurtWithPickaxe) {
						if (heldStack != null) {

							var blockLevel: Block = Blocks.dirt

							if (world.difficultySetting == EnumDifficulty.PEACEFUL) {
								blockLevel = Blocks.dirt // anything
							}
							else if (world.difficultySetting == EnumDifficulty.EASY) {
								blockLevel = Blocks.iron_ore // Stone or higher
							}
							else if (world.difficultySetting == EnumDifficulty.NORMAL) {
								blockLevel = Blocks.diamond_ore // Iron or higher
							}
							else if (world.difficultySetting == EnumDifficulty.HARD) {
								blockLevel = Blocks.obsidian // Diamond or higher
							}


							canDamage = heldStack.getItem.canHarvestBlock(blockLevel, heldStack) ||
									heldStack.getItem.canHarvestBlock(blockLevel, heldStack)

						}
					}
					else {
						canDamage = true
					}

					if (canDamage) {
						return true
					}

					return false

				case _ =>
					return true
			}

		}
		false
	}

	def getDecrepitation(age: Int): Int = {
		if (age <= 0) {
			return 0
		}
		// f(age) = -age + 6000
		// where age is in terms of 6000 -> 0 (can divide by 1200, otherwise known as ticks per decrepitation)
		WAOptions.maxDecrepitation_amount - (age / WAOptions.ticksPerDecrepitation)
	}

	@SideOnly(Side.CLIENT)
	def getTextureIDFromCorruption(isAngry: Boolean, corruption: Int, hash: Int): Int = {
		val image: BufferedImage = this.decrepitize(
			if (isAngry) WAOptions.weepingAngel2 else WAOptions.weepingAngel1,
			corruption, hash
		)

		try {
			val obj: SimpleTexture = new SimpleTexture(null)
			obj.deleteGlTexture()
			TextureUtil.uploadTextureImageAllocate(obj.getGlTextureId, image, false, false)
			obj.getGlTextureId
		}
		catch {
			case e: Exception => -1
		}
	}

	@SideOnly(Side.CLIENT)
	def decrepitize(angelTex: ResourceLocation, corruption: Int, hash: Int): BufferedImage = {
		val stream: InputStream = Rendering.mc.getResourceManager.getResource(angelTex)
				.getInputStream
		val image: BufferedImage = ImageIO.read(stream)
		IOUtils.closeQuietly(stream)

		for (i <- 1 to corruption) {
			val rand: Random = new Random(hash * i)
			val x: Int = rand.nextInt(image.getWidth)
			val y: Int = rand.nextInt(image.getHeight)
			image.setRGB(x, y, new Color(image.getRGB(x, y)).darker().getRGB)
		}

		image
	}

	/// todo temporary until moved to Teleport class
	def getRandomPoint(rand: Random, minRadius: Int, maxRadius: Int): V3O = {
		new V3O(
			this.getRandomBetweenBounds(rand, minRadius, maxRadius),
			this.getRandomBetweenBounds(rand, minRadius, maxRadius),
			this.getRandomBetweenBounds(rand, minRadius, maxRadius)
		)
	}

	// todo MathFuncs
	def getRandomBetweenBounds(rand: Random, min: Int, max: Int): Int = {
		rand.nextInt(Math.abs(max - min)) + min
	}

	// todo to V3O
	def getEntityCoordinate(entity: Entity): V3O = {
		new V3O(
			MathHelper.floor_double(entity.posX),
			MathHelper.floor_double(entity.posY),
			MathHelper.floor_double(entity.posZ)
		)
	}

	// todo to Teleport class object
	def teleportEntityToRandom(entity: EntityLivingBase, minRadius: Int, maxRadius: Int): Unit = {
		val entityPos = this.getEntityCoordinate(entity)
		var newPosCoordinate: V3O = null
		var centeredNewPos: V3O = null
		var loop: Int = 0
		newPosCoordinate = this.getRandomPoint(
			entity.worldObj.rand, minRadius, maxRadius) + entityPos
		centeredNewPos = newPosCoordinate + V3O.CENTER
		var safePos: (Boolean, Block) = this.isSafePosition(entity.worldObj, newPosCoordinate)
		var isSaveAndValidPos: Boolean = safePos._1 &&
				this.isValidPosition(entity.worldObj, centeredNewPos, entity)
		while (!isSaveAndValidPos) {
			loop += 1
			// world height is 128
			if (loop > 128) {
				this.teleportEntityToRandom(entity, minRadius, maxRadius)
				return
			}

			if (safePos._2 != Blocks.air) {
				newPosCoordinate.up()
				centeredNewPos.up()
			}
			else {
				newPosCoordinate.down()
				centeredNewPos.down()
			}

			safePos = this.isSafePosition(entity.worldObj, newPosCoordinate)
			isSaveAndValidPos = safePos._1 &&
					this.isValidPosition(entity.worldObj, centeredNewPos, entity)

		}

		this.teleportEntityToPoint(entity, centeredNewPos)
	}

	def isSafePosition(world: World, position: V3O): (Boolean, Block) = {
		val block = position.copy().down().getBlock(world)
		(block != Blocks.air, block) // && block.isOpaqueCube
	}

	def isValidPosition(world: World, centeredPos: V3O, entity: EntityLivingBase): Boolean = {
		val entityHalfWidth: Float = entity.width / 2
		val posBoundingBox: AxisAlignedBB = AxisAlignedBB.getBoundingBox(
			centeredPos.x - entityHalfWidth,
			centeredPos.y - entity.yOffset + entity.ySize,
			centeredPos.z - entityHalfWidth,
			centeredPos.x + entityHalfWidth,
			centeredPos.y - entity.yOffset + entity.ySize + entity.height,
			centeredPos.z + entityHalfWidth
		)
		world.getCollidingBoundingBoxes(entity, posBoundingBox).isEmpty &&
				!world.isAnyLiquid(posBoundingBox)
	}

	def teleportEntityToPoint(entity: Entity, point: V3O): Boolean = {
		entity match {
			case player: EntityPlayer =>
				val event: EnderTeleportEvent = new EnderTeleportEvent(
					player, point.x_i(), point.y_i(), point.z_i(), 0.0F
				)
				if (MinecraftForge.EVENT_BUS.post(event)) return false
			case _ =>
		}

		// todo make sure spot is chunk loaded (setup chunkloader for Origin mod?)

		entity match {
			case elb: EntityLivingBase =>
				elb.setPositionAndUpdate(point.x, point.y, point.z)
				elb match {
					case mp: EntityPlayerMP =>
						mp.playerNetServerHandler.setPlayerLocation(point.x, point.y, point.z,
							mp.rotationYaw, mp.rotationPitch)
					case _ =>
				}
			case _ =>
				entity.setPosition(point.x, point.y, point.z)
		}

		// todo optional particles

		true
	}

}
