package com.countrygamer.weepingangels.common.generation

import java.util.Random

import com.countrygamer.cgo.common.lib.LogHelper
import com.countrygamer.weepingangels.common.WeepingAngels
import com.countrygamer.weepingangels.common.init.WABlocks
import com.countrygamer.weepingangels.common.tile.TileEntityStatue
import cpw.mods.fml.common.IWorldGenerator
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.tileentity.{TileEntityChest, TileEntityMobSpawner}
import net.minecraft.util.{AxisAlignedBB, WeightedRandomChestContent}
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import net.minecraftforge.common.ChestGenHooks

/**
 *
 *
 * @author CountryGamer
 */
object VaultGenerator extends IWorldGenerator {

	val rarity: Int = 2500
	val lowestY: Int = 30
	val highestY: Int = 5
	val ladderRarity: Int = 10

	override def generate(random: Random, chunkX: Int, chunkZ: Int, world: World,
			chunkGenerator: IChunkProvider, chunkProvider: IChunkProvider): Unit = {

		if (random.nextInt(this.rarity) != 0) {
			return
		}

		val centerX: Int = chunkX * random.nextInt(16)
		val centerZ: Int = chunkZ * random.nextInt(16)
		val topY: Int = this.getTopY(world, centerX, centerZ)
		if (topY < 0) {
			return
		}
		val tubeLength: Int = random.nextInt(lowestY - highestY + 1) + lowestY
		val centerY: Int = topY - 6 - tubeLength

		this.clearArea(world, centerX, centerY, centerZ)
		this.makeWalls(world, centerX, centerY, centerZ, random)
		this.makeEntrance(world, centerX, centerY, centerZ, random)
		this.makeFeatures(world, centerX, centerY, centerZ, random)
		this.makeTube(world, centerX, centerY, centerZ, random, tubeLength)

		//LogHelper.info("", centerX + ":" + centerY + ":" + centerZ)

	}

	def getTopY(world: World, x: Int, z: Int): Int = {
		var y: Int = 128
		while (y >= 20) {
			val block: Block = world.getBlock(x, y, z)
			if (block != Blocks.air) {
				val box: AxisAlignedBB = block.getCollisionBoundingBoxFromPool(world, x, y, z)
				if (box != null && this.isSameScaleAABB(
					box, AxisAlignedBB.getBoundingBox(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)
				)) {
					return y
				}
			}
			y -= 1
		}
		-1
	}

	def isSameScaleAABB(box1: AxisAlignedBB, box2: AxisAlignedBB): Boolean = {
		box1.maxX - box1.minX == box2.maxX - box2.minX &&
				box1.maxY - box1.minY == box2.maxY - box2.minY &&
				box1.maxZ - box1.minZ == box2.maxZ - box2.minZ
	}

	def clearArea(world: World, centerX: Int, centerY: Int, centerZ: Int): Unit = {
		for (x <- centerX - 4 to centerX + 4) {
			for (z <- centerZ - 4 to centerZ + 10) {
				for (y <- centerY + 1 to centerY + 6) {
					world.setBlockToAir(x, y, z)
				}
			}
		}
	}

	def makeWalls(world: World, centerX: Int, centerY: Int, centerZ: Int, random: Random): Unit = {
		for (z <- centerZ - 4 to centerZ + 10) {
			for (y <- centerY + 1 to centerY + 6) {
				this.setBlock(world, centerX - 4, y, z, random)
				this.setBlock(world, centerX + 4, y, z, random)
			}
		}
		for (x <- centerX - 4 to centerX + 4) {
			for (y <- centerY + 1 to centerY + 6) {
				this.setBlock(world, x, y, centerZ - 4, random)
				this.setBlock(world, x, y, centerZ + 10, random)
			}
		}
		for (x <- centerX - 4 to centerX + 4) {
			for (z <- centerZ - 4 to centerZ + 10) {
				this.setBlock(world, x, centerY + 1, z, random)
				this.setBlock(world, x, centerY + 6, z, random)
			}
		}
		for (x <- centerX - 4 to centerX + 4) {
			for (y <- centerY + 2 to centerY + 5) {
				this.setBlock(world, x, y, centerZ + 2, random)
			}
		}
	}

	def makeEntrance(world: World, centerX: Int, centerY: Int, centerZ: Int,
			random: Random): Unit = {
		//this.setBlock(centerX + 0, centerY + 0, centerZ + 0, random)

		// top middle
		this.setBlock(world, centerX + 0, centerY + 2, centerZ + 0, random)
		// stairs to path
		this.setBlock(world, centerX - 1, centerY + 2, centerZ + 0, Blocks.stone_brick_stairs, 0)
		this.setBlock(world, centerX + 1, centerY + 2, centerZ + 0, Blocks.stone_brick_stairs, 1)
		this.setBlock(world, centerX + 0, centerY + 2, centerZ - 1, Blocks.stone_brick_stairs, 2)
		// path start into vault
		this.setBlock(world, centerX + 0, centerY + 2, centerZ + 1, random)
		// make hole into vault
		this.setBlock(world, centerX + 0, centerY + 3, centerZ + 2, Blocks.iron_bars, 0)
		this.setBlock(world, centerX + 0, centerY + 4, centerZ + 2, Blocks.iron_bars, 0)
		// make 3 stairs into vault (post bars)
		this.setBlock(world, centerX - 1, centerY + 2, centerZ + 3, Blocks.stone_brick_stairs, 0)
		this.setBlock(world, centerX + 1, centerY + 2, centerZ + 3, Blocks.stone_brick_stairs, 1)
		this.setBlock(world, centerX + 0, centerY + 2, centerZ + 3, Blocks.stone_brick_stairs, 3)
		// entrance pillars
		this.makePillar(world, centerX + 1, centerY + 2, centerZ + 1, random)
		this.makePillar(world, centerX + 1, centerY + 2, centerZ - 1, random)
		this.makePillar(world, centerX - 1, centerY + 2, centerZ + 1, random)
		this.makePillar(world, centerX - 1, centerY + 2, centerZ - 1, random)
		// walling in the back (excess)
		this.makePillar(world, centerX - 3, centerY + 2, centerZ - 2, random)
		this.makePillar(world, centerX - 3, centerY + 2, centerZ - 3, random)
		this.makePillar(world, centerX - 2, centerY + 2, centerZ - 2, random)
		this.makePillar(world, centerX - 2, centerY + 2, centerZ - 3, random)
		// ^
		this.makePillar(world, centerX + 3, centerY + 2, centerZ - 2, random)
		this.makePillar(world, centerX + 3, centerY + 2, centerZ - 3, random)
		this.makePillar(world, centerX + 2, centerY + 2, centerZ - 2, random)
		this.makePillar(world, centerX + 2, centerY + 2, centerZ - 3, random)

	}

	def makePillar(world: World, x: Int, y: Int, z: Int, random: Random): Unit = {
		this.setBlock(world, x, y + 0, z, random)
		this.setBlock(world, x, y + 1, z, random)
		this.setBlock(world, x, y + 2, z, random)
		this.setBlock(world, x, y + 3, z, random)
	}

	def makeFeatures(world: World, centerX: Int, centerY: Int, centerZ: Int,
			random: Random): Unit = {
		// 7 Statues
		val centerZ1: Int = centerZ + 6
		this.setStatue(world, centerX + 0, centerY + 2, centerZ1 + 3, 0.0F)
		this.setStatue(world, centerX - 3, centerY + 2, centerZ1 + 3, 45.0F)
		this.setStatue(world, centerX + 3, centerY + 2, centerZ1 + 3, 315.0F)
		this.setStatue(world, centerX - 3, centerY + 2, centerZ1 + 0, 90.0F)
		this.setStatue(world, centerX + 3, centerY + 2, centerZ1 + 0, 270.0F)
		this.setStatue(world, centerX - 3, centerY + 2, centerZ1 - 3, 135.0F)
		this.setStatue(world, centerX + 3, centerY + 2, centerZ1 - 3, 225.0F)

		var special: Array[Int] = null
		var x: Int = centerX
		val y: Int = centerY + 1
		var z: Int = centerZ1

		special = this.getSpecialPlacement(random)
		x += special(0)
		z += special(1)
		// 1 Spawner
		this.setBlock(world, x, y, z, Blocks.mob_spawner, 0)
		val teMob: TileEntityMobSpawner = world.getTileEntity(x, y, z)
				.asInstanceOf[TileEntityMobSpawner]
		if (teMob != null) {
			teMob.func_145881_a().setEntityName("Weeping Angel")
		}
		else {
			LogHelper.info(WeepingAngels.pluginName,
				"Failed to fetch mob spawner entity at (" + x + ", " + y + ", " + z + ")"
			)
		}

		// 2 Chests
		this.setChest(world, centerX, y, centerZ1, random)
		this.setChest(world, centerX, y, centerZ1, random)

	}

	def setChest(world: World, x: Int, y: Int, z: Int, random: Random): Unit = {
		val special: Array[Int] = this.getSpecialPlacement(random)
		val x1: Int = x + special(0)
		val z1: Int = z + special(1)

		if (x1 == x && z1 == z)
			return

		val block: Block = world.getBlock(x1, y, z1)
		if (block != Blocks.mob_spawner && block != Blocks.chest) {
			this.setBlock(world, x1, y, z1, Blocks.chest, 0)
			val teChest: TileEntityChest = world.getTileEntity(x1, y, z1)
					.asInstanceOf[TileEntityChest]
			if (teChest != null) {
				WeightedRandomChestContent.generateChestContents(random,
					ChestGenHooks.getItems(ChestGenHooks.DUNGEON_CHEST, random), teChest,
					ChestGenHooks.getCount(ChestGenHooks.DUNGEON_CHEST, random))
			}
		}
	}

	def getSpecialPlacement(random: Random): Array[Int] = {
		// (0 -> 8) / 7 = 0
		random.nextInt(64) / 8 match {
			case 0 =>
				Array[Int](0, 3)
			case 1 =>
				Array[Int](-3, 3)
			case 2 =>
				Array[Int](3, 3)
			case 3 =>
				Array[Int](-3, 0)
			case 4 =>
				Array[Int](3, 0)
			case 5 =>
				Array[Int](-3, -3)
			case 6 =>
				Array[Int](3, -3)
			case _ => // 1/8 chance not to spawn a spawner
				Array[Int](0, 0)
		}
	}

	def makeTube(world: World, centerX: Int, centerY: Int, centerZ: Int, random: Random,
			height: Int): Unit = {
		var x: Int = centerX
		val y: Int = centerY + 7
		var z: Int = centerZ

		// 0 = down
		// 1 = up
		// 2 = north +Z
		// 3 = south -Z
		// 4 = west +X
		// 5 = east -X
		var ladderMeta: Int = -1

		random.nextInt(3) match {
			case 0 =>
				z -= 3
				ladderMeta = 3
			case 1 =>
				x += 3
				ladderMeta = 4
			case 2 =>
				x -= 3
				ladderMeta = 5
			case _ =>
				return
		}

		this.setBlock(world, x, y - 1, z, Blocks.air, 0)
		for (y1 <- y to y + height) {
			this.setBlock(world, x - 1, y1, z + 0, random)
			this.setBlock(world, x - 1, y1, z - 1, random)
			this.setBlock(world, x - 1, y1, z + 1, random)
			this.setBlock(world, x + 0, y1, z + 0, Blocks.air, 0)
			this.setBlock(world, x + 0, y1, z - 1, random)
			this.setBlock(world, x + 0, y1, z + 1, random)
			this.setBlock(world, x + 1, y1, z + 0, random)
			this.setBlock(world, x + 1, y1, z - 1, random)
			this.setBlock(world, x + 1, y1, z + 1, random)
		}

		// ~~~~~~~~~~~~~~
		// Make ladder
		for (y1 <- y - 4 to y + height) {
			if (random.nextInt(this.ladderRarity) != 0)
				this.setBlock(world, x, y1, z, Blocks.ladder, ladderMeta)
		}

		this.makeTubeEntrance(world, x, y + height, z, random)

	}

	def makeTubeEntrance(world: World, x: Int, y: Int, z: Int, random: Random): Unit = {
		// 8 1 2
		// 7 - 3
		// 6 5 4
		this.setBlock(world, x + 0, y + 1, z + 1, Blocks.stone_brick_stairs, 3)
		this.setBlock(world, x - 1, y + 1, z + 1, Blocks.stone_brick_stairs, 3)
		this.setBlock(world, x - 1, y + 1, z + 0, Blocks.stone_brick_stairs, 0)
		this.setBlock(world, x - 1, y + 1, z - 1, Blocks.stone_brick_stairs, 0)
		this.setBlock(world, x + 0, y + 1, z - 1, Blocks.stone_brick_stairs, 2)
		this.setBlock(world, x + 1, y + 1, z - 1, Blocks.stone_brick_stairs, 2)
		this.setBlock(world, x + 1, y + 1, z + 0, Blocks.stone_brick_stairs, 1)
		this.setBlock(world, x + 1, y + 1, z + 1, Blocks.stone_brick_stairs, 1)

		this.setBlock(world, x, y + 1, z, Blocks.trapdoor, 8)
		this.setStatue(world, x, y + 2, z, random.nextFloat() * 360)

	}

	def setStatue(world: World, x: Int, y: Int, z: Int, rot: Float): Unit = {
		this.setBlock(world, x, y, z, WABlocks.statue, 0)
		val te: TileEntityStatue = world.getTileEntity(x, y, z).asInstanceOf[TileEntityStatue]
		if (te != null) {
			te.setRotation(rot)
		}
		else {
			LogHelper.info(WeepingAngels.pluginName,
				"Failed to fetch statue entity at (" + x + ", " + y + ", " + z + ")"
			)
		}
	}

	def setBlock(world: World, x: Int, y: Int, z: Int, random: Random): Unit = {
		val blockMeta: Array[Any] = this.getBlock(random)
		this.setBlock(world, x, y, z, blockMeta(0).asInstanceOf[Block],
			blockMeta(1).asInstanceOf[Int])
	}

	def setBlock(world: World, x: Int, y: Int, z: Int, block: Block, meta: Int): Unit = {
		world.setBlock(x, y, z, block, meta, 2)
	}

	def getBlock(random: Random): Array[Any] = {
		val chance: Int = random.nextInt(100) + 1
		/*
		50% brick
		25% mossy brick
		12% cobble
		12% cracked
		1% air
		 */
		if (chance <= 50) {
			// 1 - 50 (50%)
			Array[Any](Blocks.stonebrick, 0)
		}
		else if (chance <= 75) {
			// 51 - 75 (25%)
			Array[Any](Blocks.stonebrick, 1)
		}
		else if (chance <= 87) {
			// 76 - 87 (12%)
			Array[Any](Blocks.cobblestone, 0)
		}
		else if (chance <= 99) {
			// 88 - 99 (12%)
			Array[Any](Blocks.stonebrick, 2)
		}
		else {
			Array[Any](Blocks.air, 0)
		}
	}

}
