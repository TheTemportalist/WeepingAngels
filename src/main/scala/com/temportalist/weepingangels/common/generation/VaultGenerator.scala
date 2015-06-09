package com.temportalist.weepingangels.common.generation

import java.util.Random

import com.temportalist.origin.api.common.lib.{V3O, BlockState, LogHelper}
import com.temportalist.weepingangels.common.WeepingAngels
import com.temportalist.weepingangels.common.init.WABlocks
import com.temportalist.weepingangels.common.tile.TEStatue
import cpw.mods.fml.common.IWorldGenerator
import net.minecraft.block._
import net.minecraft.init.Blocks
import net.minecraft.tileentity.{TileEntityChest, TileEntityMobSpawner}
import net.minecraft.util.{AxisAlignedBB, WeightedRandomChestContent}
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import net.minecraftforge.common.ChestGenHooks
import net.minecraftforge.common.util.ForgeDirection

/**
 *
 *
 * @author TheTemportalist
 */
object VaultGenerator extends IWorldGenerator {

	val rarity: Int = 2500
	val lowestY: Int = 30
	val highestY: Int = 5
	val ladderRarity: Int = 10

	override def generate(random: Random, chunkX: Int, chunkZ: Int, world: World,
			chunkGenerator: IChunkProvider, chunkProvider: IChunkProvider): Unit = {

		if (world.provider.dimensionId != 0) return

		if (random.nextInt(this.rarity) != 0) {
			return
		}

		val x: Int = chunkX * random.nextInt(16)
		val z: Int = chunkZ * random.nextInt(16)
		val topY: Int = this.getTopY(world, x, z)
		if (topY < 0) {
			return
		}
		val tubeLength: Int = random.nextInt(lowestY - highestY + 1) + lowestY
		val y: Int = topY - 6 - tubeLength
		val centerPos: V3O = new V3O(x, y, z)

		this.clearArea(world, centerPos)
		this.makeWalls(world, centerPos, random)
		this.makeEntrance(world, centerPos, random)
		this.makeFeatures(world, centerPos, random)
		this.makeTube(world, centerPos, random, tubeLength)

		//LogHelper.info("", centerX + ":" + centerY + ":" + centerZ)

	}

	def getTopY(world: World, x: Int, z: Int): Int = {
		val pos: V3O = new V3O(x, 128, z)
		while (pos.y >= 20) {
			val block: Block = pos.getBlock(world)
			if (block != Blocks.air) {
				val box: AxisAlignedBB = AxisAlignedBB.getBoundingBox(
					block.getBlockBoundsMinX,
					block.getBlockBoundsMinY,
					block.getBlockBoundsMinZ,
					block.getBlockBoundsMaxX,
					block.getBlockBoundsMaxY,
					block.getBlockBoundsMaxZ
				)
				if (box != null && this.isSameScaleAABB(
					box, AxisAlignedBB.getBoundingBox(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)
				)) {
					return pos.y_i()
				}
			}
			pos.down()
		}
		-1
	}

	def isSameScaleAABB(box1: AxisAlignedBB, box2: AxisAlignedBB): Boolean = {
		V3O.from(box1) == V3O.from(box2)
	}

	def clearArea(world: World, pos: V3O): Unit = {
		for (x <- pos.x_i() - 4 to pos.x_i() + 4) {
			for (z <- pos.z_i() - 4 to pos.z_i() + 10) {
				for (y <- pos.y_i() + 1 to pos.y_i() + 6) {
					world.setBlockToAir(x, y, z)
				}
			}
		}
	}

	def makeWalls(world: World, pos: V3O, random: Random): Unit = {
		for (z <- pos.z_i() - 4 to pos.z_i() + 10) {
			for (y <- pos.y_i() + 1 to pos.y_i() + 6) {
				this.setBlock(world, random, new V3O(pos.x_i() - 4, y, z))
				this.setBlock(world, random, new V3O(pos.x_i() + 4, y, z))
			}
		}
		for (x <- pos.x_i() - 4 to pos.x_i() + 4) {
			for (y <- pos.y_i() + 1 to pos.y_i() + 6) {
				this.setBlock(world, random, new V3O(x, y, pos.z_i() - 4))
				this.setBlock(world, random, new V3O(x, y, pos.z_i() + 10))
			}
		}
		for (x <- pos.x_i() - 4 to pos.x_i() + 4) {
			for (z <- pos.y_i() - 4 to pos.y_i() + 10) {
				this.setBlock(world, random, new V3O(x, pos.y_i() + 1, z))
				this.setBlock(world, random, new V3O(x, pos.y_i() + 6, z))
			}
		}
		for (x <- pos.x_i() - 4 to pos.x_i() + 4) {
			for (y <- pos.y_i() + 2 to pos.y_i() + 5) {
				this.setBlock(world, random, new V3O(x, y, pos.z_i() + 2))
			}
		}
	}

	def makeEntrance(world: World, centerPos: V3O, random: Random): Unit = {
		//this.setBlock(centerX + 0, centerY + 0, centerZ + 0, random)

		val pos: V3O = centerPos + V3O.UP
		// top middle
		this.setBlock(world, random, pos)
		// stairs to path
		this.setBlock(world, this.getStairs(ForgeDirection.EAST), pos.west())
		this.setBlock(world, this.getStairs(ForgeDirection.WEST), pos.east())
		this.setBlock(world, this.getStairs(ForgeDirection.SOUTH), pos.north())
		// path start into vault
		this.setBlock(world, random, pos.south(1))
		// make hole into vault
		this.setBlock(world, pos.up(1).south(2), Blocks.iron_bars)
		this.setBlock(world, pos.up(2).south(2), Blocks.iron_bars)
		// make 3 stairs into vault (post bars)
		this.setBlock(world, this.getStairs(ForgeDirection.EAST), pos.west(1).south(3))
		this.setBlock(world, this.getStairs(ForgeDirection.WEST), pos.east(1).south(3))
		this.setBlock(world, this.getStairs(ForgeDirection.NORTH), pos.east(1).south(3))
		// entrance pillars
		this.makePillar(world, random, pos.east().south())
		this.makePillar(world, random, pos.east().north())
		this.makePillar(world, random, pos.west().south())
		this.makePillar(world, random, pos.west().north())
		// walling in the back (excess)
		this.makePillar(world, random, pos.west(3).north(2))
		this.makePillar(world, random, pos.west(3).north(3))
		this.makePillar(world, random, pos.west(2).north(2))
		this.makePillar(world, random, pos.west(2).north(3))
		// ^
		this.makePillar(world, random, pos.east(3).north(2))
		this.makePillar(world, random, pos.east(3).north(3))
		this.makePillar(world, random, pos.east(2).north(2))
		this.makePillar(world, random, pos.east(2).north(3))

	}

	def makePillar(world: World, random: Random, pos: V3O): Unit = {
		this.setBlock(world, pos, random)
		this.setBlock(world, pos.up(), random)
		this.setBlock(world, pos.up(2), random)
		this.setBlock(world, pos.up(3), random)
	}

	def makeFeatures(world: World, centerPos: V3O, random: Random): Unit = {
		val pos: V3O = centerPos.south(6)
		val statuePos: V3O = pos.up(2)
		val radius: Int = 3 // radius
		// 7 Statues
		this.setStatue(world, 0, statuePos.south(radius))
		this.setStatue(world, 45, statuePos.west(radius).south(radius))
		this.setStatue(world, 315, statuePos.east(radius).south(radius))
		this.setStatue(world, 90, statuePos.west(radius))
		this.setStatue(world, 270, statuePos.east(radius))
		this.setStatue(world, 135, statuePos.west(radius).north(radius))
		this.setStatue(world, 225, statuePos.east(radius).north(radius))

		val spawnerVec: V3O = centerPos + ForgeDirection.UP
		this.getLootOffsetPos(spawnerVec, random, radius)

		// 1 Spawner
		this.setBlock(world, spawnerVec, Blocks.mob_spawner)
		spawnerVec.getTile(world) match {
			case spawner: TileEntityMobSpawner =>
				spawner.func_145881_a().setEntityName("Weeping Angel")
			case _ =>
				LogHelper.info(WeepingAngels.MODNAME,
					"Failed to fetch mob spawner entity at (" + spawnerVec.x_i() + ", " +
							spawnerVec.y_i() + ", " + spawnerVec.z_i() + ")"
				)
		}

		// 2 Chests
		this.setChest(world, pos, random, radius)
		this.setChest(world, pos, random, radius)

	}

	def setChest(world: World, pos: V3O, random: Random, radius: Int): Unit = {
		val chestPos: V3O = pos.copy()
		this.getLootOffsetPos(chestPos, random, radius)
		if (chestPos == pos) return

		val block: Block = chestPos.getBlock(world)
		println(block.getClass.getCanonicalName)
		if (block != Blocks.mob_spawner && block != Blocks.chest) {
			this.setBlock(world, chestPos, Blocks.chest)
			chestPos.getTile(world) match {
				case teChest: TileEntityChest =>
					WeightedRandomChestContent.generateChestContents(random,
						ChestGenHooks.getItems(ChestGenHooks.DUNGEON_CHEST, random), teChest,
						ChestGenHooks.getCount(ChestGenHooks.DUNGEON_CHEST, random))
				case _ =>
			}
		}
	}

	def getLootOffsetPos(vec: V3O, random: Random, radius: Int): Unit = {
		random.nextInt(64) / 8 match {
			case 0 =>
				vec.south(radius)
			case 1 =>
				vec.west(radius)
				vec.south(radius)
			case 2 =>
				vec.east(radius)
				vec.south(radius)
			case 3 =>
				vec.west(radius)
			case 4 =>
				vec.east(radius)
			case 5 =>
				vec.west(radius)
				vec.north(radius)
			case 6 =>
				vec.east(radius)
				vec.north(radius)
			case _ =>
		}
	}

	def makeTube(world: World, centerPos: V3O, random: Random, height: Int): Unit = {
		var pos: V3O = centerPos.up(7)

		// 0 = down
		// 1 = up
		// 2 = north +Z
		// 3 = south -Z
		// 4 = west +X
		// 5 = east -X
		var ladderFacing: ForgeDirection = ForgeDirection.NORTH

		random.nextInt(3) match {
			case 0 =>
				pos = pos.north(3)
				ladderFacing = ForgeDirection.SOUTH
			case 1 =>
				pos = pos.east(3)
				ladderFacing = ForgeDirection.WEST
			case 2 =>
				pos = pos.west(3)
				ladderFacing = ForgeDirection.EAST
			case _ =>
				return
		}

		this.setBlock(world, pos.down(), Blocks.air)
		for (y <- pos.y_i() to pos.y_i() + height) {
			val pos2: V3O = pos.up(y)
			this.setBlock(world, random, pos2.west())
			this.setBlock(world, random, pos2.west().north())
			this.setBlock(world, random, pos2.west().south())
			this.setBlock(world, Blocks.air, pos2)
			this.setBlock(world, random, pos2.north())
			this.setBlock(world, random, pos2.south())
			this.setBlock(world, random, pos2.east())
			this.setBlock(world, random, pos2.east().north())
			this.setBlock(world, random, pos2.east().south())
		}

		// ~~~~~~~~~~~~~~
		// Make ladder
		for (y <- pos.y_i() - 4 to pos.y_i() + height) {
			if (random.nextInt(this.ladderRarity) != 0)
				this.setBlock(world, pos.up(y),
					Blocks.ladder, ladderFacing.ordinal()
				)
		}

		this.makeTubeEntrance(world, pos.up(height), random)

	}

	def makeTubeEntrance(world: World, pos: V3O, random: Random): Unit = {

		this.setBlock(world, this.getStairs(ForgeDirection.NORTH), pos.up().south())
		this.setBlock(world, this.getStairs(ForgeDirection.NORTH), pos.west().up().south())
		this.setBlock(world, this.getStairs(ForgeDirection.EAST), pos.west().up())
		this.setBlock(world, this.getStairs(ForgeDirection.EAST), pos.west().up().north())
		this.setBlock(world, this.getStairs(ForgeDirection.SOUTH), pos.up().north())
		this.setBlock(world, this.getStairs(ForgeDirection.SOUTH), pos.east().up().north())
		this.setBlock(world, this.getStairs(ForgeDirection.WEST), pos.east().up())
		this.setBlock(world, this.getStairs(ForgeDirection.WEST), pos.east().up().south())

		/* todo set trapdoor, experiement with metadata
		this.setBlock(world, pos.up(), Blocks.trapdoor.
				withProperty(BlockTrapDoor.FACING, EnumFacing.NORTH).
				withProperty(BlockTrapDoor.OPEN, false).
				withProperty(BlockTrapDoor.HALF, BlockTrapDoor.DoorHalf.TOP)
		)
		*/
		this.setStatue(world, random.nextFloat() * 360, pos.up(2))

	}

	def getStairs(dir: ForgeDirection): BlockState = {
		new BlockState(Blocks.stone_brick_stairs, dir.ordinal()) // todo check the metadata
	}

	def setStatue(world: World, rot: Float, pos: V3O): Unit = {
		this.setBlock(world, pos, WABlocks.statue)
		pos.getTile(world) match {
			case te: TEStatue =>
				te.setRotation(rot)
			case _ =>
				LogHelper.info(WeepingAngels.MODNAME,
					"Failed to fetch statue entity at (" + pos.x_i() + ", " + pos.y_i() + ", " +
							pos.z_i() + ")"
				)
		}
	}

	def setBlock(world: World, random: Random, pos: V3O): Unit = {
		this.setBlock(world, pos, random)
	}

	def setBlock(world: World, pos: V3O, random: Random): Unit = {
		val state: BlockState = this.getBlock(random)
		this.setBlock(world, pos, state.getBlock, state.getMeta)
	}

	def setBlock(world: World, block: Block, pos: V3O): Unit = {
		this.setBlock(world, pos, block)
	}

	def setBlock(world: World, state: BlockState, pos: V3O): Unit =
		this.setBlock(world, pos, state.getBlock, state.getMeta)

	def setBlock(world: World, pos: V3O, block: Block): Unit = {
		this.setBlock(world, pos, block, 0)
	}

	def setBlock(world: World, pos:V3O, block: Block, meta: Int): Unit = {
		pos.setBlock(world, block, meta, 2)
	}

	def getBlock(random: Random): BlockState = {
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
			new BlockState(Blocks.stonebrick, 0)
		}
		else if (chance <= 75) {
			// 51 - 75 (25%)
			new BlockState(Blocks.stonebrick, 1)
		}
		else if (chance <= 87) {
			// 76 - 87 (12%)
			new BlockState(Blocks.cobblestone, 0)
		}
		else if (chance <= 99) {
			// 88 - 99 (12%)
			new BlockState(Blocks.stonebrick, 1)
		}
		else {
			new BlockState(Blocks.air, 0)
		}
	}

}
