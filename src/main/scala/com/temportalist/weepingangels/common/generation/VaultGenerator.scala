package com.temportalist.weepingangels.common.generation

import java.util.Random

import com.temportalist.origin.library.common.lib.LogHelper
import com.temportalist.origin.library.common.lib.vec.V3O
import com.temportalist.weepingangels.common.WeepingAngels
import com.temportalist.weepingangels.common.init.WABlocks
import com.temportalist.weepingangels.common.tile.TEStatue
import net.minecraft.block._
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.tileentity.{TileEntityChest, TileEntityMobSpawner}
import net.minecraft.util.{EnumFacing, BlockPos, AxisAlignedBB, WeightedRandomChestContent}
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import net.minecraftforge.common.ChestGenHooks
import net.minecraftforge.fml.common.IWorldGenerator

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
		val centerPos: BlockPos = new BlockPos(x, y, z)

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
			val state: IBlockState = pos.getBlockState(world)
			val block: Block = state.getBlock
			if (block != Blocks.air) {
				val box: AxisAlignedBB = block
						.getCollisionBoundingBox(world, pos.toBlockPos(), state)
				if (box != null && this.isSameScaleAABB(
					box, AxisAlignedBB.fromBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)
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

	def clearArea(world: World, pos: BlockPos): Unit = {
		for (x <- pos.getX - 4 to pos.getX + 4) {
			for (z <- pos.getZ - 4 to pos.getZ + 10) {
				for (y <- pos.getY + 1 to pos.getY + 6) {
					world.setBlockToAir(new BlockPos(x, y, z))
				}
			}
		}
	}

	def makeWalls(world: World, pos: BlockPos, random: Random): Unit = {
		for (z <- pos.getZ - 4 to pos.getZ + 10) {
			for (y <- pos.getY + 1 to pos.getY + 6) {
				this.setBlock(world, random, new BlockPos(pos.getX - 4, y, z))
				this.setBlock(world, random, new BlockPos(pos.getX + 4, y, z))
			}
		}
		for (x <- pos.getX - 4 to pos.getX + 4) {
			for (y <- pos.getY + 1 to pos.getY + 6) {
				this.setBlock(world, random, new BlockPos(x, y, pos.getZ - 4))
				this.setBlock(world, random, new BlockPos(x, y, pos.getZ + 10))
			}
		}
		for (x <- pos.getX - 4 to pos.getX + 4) {
			for (z <- pos.getY - 4 to pos.getY + 10) {
				this.setBlock(world, random, new BlockPos(x, pos.getY + 1, z))
				this.setBlock(world, random, new BlockPos(x, pos.getY + 6, z))
			}
		}
		for (x <- pos.getX - 4 to pos.getX + 4) {
			for (y <- pos.getY + 2 to pos.getY + 5) {
				this.setBlock(world, random, new BlockPos(x, y, pos.getZ + 2))
			}
		}
	}

	def makeEntrance(world: World, centerPos: BlockPos, random: Random): Unit = {
		//this.setBlock(centerX + 0, centerY + 0, centerZ + 0, random)

		val pos: BlockPos = centerPos.up(2)
		// top middle
		this.setBlock(world, random, pos)
		// stairs to path
		this.setBlock(world, this.getStairs(EnumFacing.EAST), pos.west())
		this.setBlock(world, this.getStairs(EnumFacing.WEST), pos.east())
		this.setBlock(world, this.getStairs(EnumFacing.SOUTH), pos.north())
		// path start into vault
		this.setBlock(world, random, pos.south(1))
		// make hole into vault
		this.setBlock(world, pos.up(1).south(2), Blocks.iron_bars.getDefaultState)
		this.setBlock(world, pos.up(2).south(2), Blocks.iron_bars.getDefaultState)
		// make 3 stairs into vault (post bars)
		this.setBlock(world, this.getStairs(EnumFacing.EAST), pos.west(1).south(3))
		this.setBlock(world, this.getStairs(EnumFacing.WEST), pos.east(1).south(3))
		this.setBlock(world, this.getStairs(EnumFacing.NORTH), pos.east(1).south(3))
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

	def makePillar(world: World, random: Random, pos: BlockPos): Unit = {
		this.setBlock(world, pos, random)
		this.setBlock(world, pos.up(), random)
		this.setBlock(world, pos.up(2), random)
		this.setBlock(world, pos.up(3), random)
	}

	def makeFeatures(world: World, centerPos: BlockPos, random: Random): Unit = {
		val pos: BlockPos = centerPos.south(6)
		val statuePos: BlockPos = pos.up(2)
		val radius: Int = 3 // radius
		// 7 Statues
		this.setStatue(world, 0, statuePos.south(radius))
		this.setStatue(world, 45, statuePos.west(radius).south(radius))
		this.setStatue(world, 315, statuePos.east(radius).south(radius))
		this.setStatue(world, 90, statuePos.west(radius))
		this.setStatue(world, 270, statuePos.east(radius))
		this.setStatue(world, 135, statuePos.west(radius).north(radius))
		this.setStatue(world, 225, statuePos.east(radius).north(radius))

		val spawnerVec: V3O = new V3O(centerPos) + EnumFacing.UP
		this.getLootOffsetPos(spawnerVec, random, radius)
		val spawnerPos: BlockPos = spawnerVec.toBlockPos()

		// 1 Spawner
		this.setBlock(world, spawnerPos, Blocks.mob_spawner.getDefaultState)
		world.getTileEntity(spawnerPos) match {
			case spawner: TileEntityMobSpawner =>
				spawner.getSpawnerBaseLogic.setEntityName("Weeping Angel")
			case _ =>
				LogHelper.info(WeepingAngels.MODNAME,
					"Failed to fetch mob spawner entity at (" + spawnerPos.getX + ", " +
							spawnerPos.getY + ", " + spawnerPos.getZ + ")"
				)
		}

		// 2 Chests
		this.setChest(world, pos, random, radius)
		this.setChest(world, pos, random, radius)

	}

	def setChest(world: World, pos: BlockPos, random: Random, radius: Int): Unit = {
		val chestVec: V3O = new V3O(pos)
		this.getLootOffsetPos(chestVec, random, radius)
		if (chestVec.toBlockPos() == pos) return
		val chestPos: BlockPos = chestVec.toBlockPos()

		val state: IBlockState = world.getBlockState(chestPos)
		if (state.getBlock != Blocks.mob_spawner && state.getBlock != Blocks.chest) {
			this.setBlock(world, chestPos, Blocks.chest.getDefaultState)
			val teChest: TileEntityChest = world.getTileEntity(chestPos)
					.asInstanceOf[TileEntityChest]
			if (teChest != null) {
				WeightedRandomChestContent.generateChestContents(random,
					ChestGenHooks.getItems(ChestGenHooks.DUNGEON_CHEST, random), teChest,
					ChestGenHooks.getCount(ChestGenHooks.DUNGEON_CHEST, random))
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

	def makeTube(world: World, centerPos: BlockPos, random: Random,
			height: Int): Unit = {
		var pos: BlockPos = centerPos.up(7)

		// 0 = down
		// 1 = up
		// 2 = north +Z
		// 3 = south -Z
		// 4 = west +X
		// 5 = east -X
		var ladderFacing: EnumFacing = EnumFacing.NORTH

		random.nextInt(3) match {
			case 0 =>
				pos = pos.north(3)
				ladderFacing = EnumFacing.SOUTH
			case 1 =>
				pos = pos.east(3)
				ladderFacing = EnumFacing.WEST
			case 2 =>
				pos = pos.west(3)
				ladderFacing = EnumFacing.EAST
			case _ =>
				return
		}

		this.setBlock(world, pos.down(), Blocks.air.getDefaultState)
		for (y <- pos.getY to pos.getY + height) {
			val pos2: BlockPos = pos.up(y)
			this.setBlock(world, random, pos2.west())
			this.setBlock(world, random, pos2.west().north())
			this.setBlock(world, random, pos2.west().south())
			this.setBlock(world, Blocks.air.getDefaultState, pos2)
			this.setBlock(world, random, pos2.north())
			this.setBlock(world, random, pos2.south())
			this.setBlock(world, random, pos2.east())
			this.setBlock(world, random, pos2.east().north())
			this.setBlock(world, random, pos2.east().south())
		}

		// ~~~~~~~~~~~~~~
		// Make ladder
		for (y <- pos.getY - 4 to pos.getY + height) {
			if (random.nextInt(this.ladderRarity) != 0)
				this.setBlock(world, pos.up(y),
					Blocks.ladder.getDefaultState.withProperty(BlockLadder.FACING, ladderFacing)
				)
		}

		this.makeTubeEntrance(world, pos.up(height), random)

	}

	def makeTubeEntrance(world: World, pos: BlockPos, random: Random): Unit = {

		this.setBlock(world, this.getStairs(EnumFacing.NORTH), pos.up().south())
		this.setBlock(world, this.getStairs(EnumFacing.NORTH), pos.west().up().south())
		this.setBlock(world, this.getStairs(EnumFacing.EAST), pos.west().up())
		this.setBlock(world, this.getStairs(EnumFacing.EAST), pos.west().up().north())
		this.setBlock(world, this.getStairs(EnumFacing.SOUTH), pos.up().north())
		this.setBlock(world, this.getStairs(EnumFacing.SOUTH), pos.east().up().north())
		this.setBlock(world, this.getStairs(EnumFacing.WEST), pos.east().up())
		this.setBlock(world, this.getStairs(EnumFacing.WEST), pos.east().up().south())

		this.setBlock(world, pos.up(), Blocks.trapdoor.getDefaultState.
				withProperty(BlockTrapDoor.FACING, EnumFacing.NORTH).
				withProperty(BlockTrapDoor.OPEN, false).
				withProperty(BlockTrapDoor.HALF, BlockTrapDoor.DoorHalf.TOP)
		)
		this.setStatue(world, random.nextFloat() * 360, pos.up(2))

	}

	def getStairs(enumFacing: EnumFacing): IBlockState = {
		Blocks.stone_brick_stairs.getDefaultState.withProperty(
			BlockStairs.FACING, EnumFacing.NORTH
		)
	}

	def setStatue(world: World, rot: Float, pos: BlockPos): Unit = {
		this.setBlock(world, pos, WABlocks.statue.getDefaultState)
		world.getTileEntity(pos) match {
			case te: TEStatue =>
				te.setRotation(rot)
			case _ =>
				LogHelper.info(WeepingAngels.MODNAME,
					"Failed to fetch statue entity at (" + pos.getX + ", " + pos.getY + ", " +
							pos.getZ + ")"
				)
		}
	}

	def setBlock(world: World, random: Random, pos: BlockPos): Unit = {
		this.setBlock(world, pos, random)
	}

	def setBlock(world: World, pos: BlockPos, random: Random): Unit = {
		this.setBlock(world, pos, this.getBlock(random))
	}

	def setBlock(world: World, state: IBlockState, pos: BlockPos): Unit = {
		this.setBlock(world, pos, state)
	}

	def setBlock(world: World, pos: BlockPos, state: IBlockState): Unit = {
		world.setBlockState(pos, state, 2)
	}

	def getBlock(random: Random): IBlockState = {
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
			Blocks.stonebrick.getDefaultState.withProperty(
				BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.DEFAULT
			)
		}
		else if (chance <= 75) {
			// 51 - 75 (25%)
			Blocks.stonebrick.getDefaultState.withProperty(
				BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.MOSSY
			)
		}
		else if (chance <= 87) {
			// 76 - 87 (12%)
			Blocks.cobblestone.getDefaultState
		}
		else if (chance <= 99) {
			// 88 - 99 (12%)
			Blocks.stonebrick.getDefaultState.withProperty(
				BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CRACKED
			)
		}
		else {
			Blocks.air.getDefaultState
		}
	}

}
