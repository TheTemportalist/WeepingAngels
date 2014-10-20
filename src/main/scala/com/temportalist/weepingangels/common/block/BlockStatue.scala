package com.temportalist.weepingangels.common.block

import java.util

import com.temportalist.origin.library.common.utility.Drops
import com.temportalist.origin.wrapper.common.block.BlockWrapperTE
import com.temportalist.weepingangels.common.tile.TileEntityStatue
import com.temportalist.weepingangels.common.{WAOptions, WeepingAngels}
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.{IBlockAccess, World}

/**
 *
 *
 * @author TheTemportalist
 */
class BlockStatue(material: Material, pluginID: String, name: String,
		tileEntityClass: Class[_ <: TileEntity])
		extends BlockWrapperTE(material, pluginID, name, tileEntityClass) {

	// Default Constructor
	{
		this.setHardness(2.0F)
		this.setResistance(10.0F)
		this.setStepSound(Block.soundTypeStone)

	}

	// End Constructor

	// Other Constructors

	// End Constructors

	/**
	 * Check to see if this block has a tile entity
	 * @param metadata
	 * @return
	 */
	override def hasTileEntity(metadata: Int): Boolean = {
		metadata == 0
	}

	/**
	 * Called to create a new tile entity instance for this block
	 * @param world
	 * @param metadata
	 * @return
	 */
	override def createTileEntity(world: World, metadata: Int): TileEntity = {
		if (metadata == 0) {
			return super.createTileEntity(world, metadata)
		}
		null
	}

	override def renderAsNormalBlock(): Boolean = {
		false
	}

	override def isOpaqueCube: Boolean = {
		false
	}

	override def getRenderType: Int = {
		-1
	}

	override def canPlaceBlockAt(world: World, x: Int, y: Int, z: Int): Boolean = {
		world.getBlock(x, y, z) == Blocks.air && world.getBlock(x, y + 1, z) == Blocks.air
	}

	override def onBlockAdded(world: World, x: Int, y: Int, z: Int): Unit = {
		val meta: Int = world.getBlockMetadata(x, y, z)
		if (meta < 2) {
			world.setBlock(x, y + 1, z, this, meta + 1, 3)
		}

	}

	override def setBlockBoundsBasedOnState(world: IBlockAccess, x: Int, y: Int, z: Int): Unit = {
		val meta: Int = world.getBlockMetadata(x, y, z)
		this.setBlockBounds(0.0F, 0.0F - meta, 0.0F, 1.0F, 3.0F - meta, 1.0F)
		super.setBlockBoundsBasedOnState(world, x, y, z)

	}

	override def breakBlock(world: World, x: Int, y: Int, z: Int, block: Block,
			meta: Int): Unit = {
		super.breakBlock(world, x, y, z, block, meta)

		var nextY: Int = y - 1
		var nextBlock: Block = world.getBlock(x, nextY, z)
		var nextMeta: Int = world.getBlockMetadata(x, nextY, z)
		while (nextBlock == this && nextMeta >= 0) {
			world.setBlockToAir(x, nextY, z)
			nextY = nextY - 1
			nextBlock = world.getBlock(x, nextY, z)
			nextMeta = world.getBlockMetadata(x, nextY, z)

		}

		nextY = y + 1
		nextBlock = world.getBlock(x, nextY, z)
		while (nextBlock == this) {
			world.setBlockToAir(x, nextY, z)
			nextY = nextY + 1
			nextBlock = world.getBlock(x, nextY, z)

		}

		if (!world.isRemote && meta == 0)
			Drops.spawnItemStack(
				world, x + 0.5, y, z + 0.5, new ItemStack(this, 1, 0), world.rand, 10
			)

	}

	override def getDrops(world: World, x: Int, y: Int, z: Int, metadata: Int,
			fortune: Int): util.ArrayList[ItemStack] = {
		new util.ArrayList[ItemStack]()
	}

	override def onBlockPlacedBy(world: World, x: Int, y: Int, z: Int, entity: EntityLivingBase,
			itemStack: ItemStack): Unit = {
		val tileEntity: TileEntity = world.getTileEntity(x, y, z)

		tileEntity match {
			case statue: TileEntityStatue =>

				var rotation: Float = entity.rotationYaw

				if (entity.isSneaking) {
					rotation = -rotation
				}

				statue.setRotation(rotation)

			case _ =>
		}

	}

	override def onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer,
			side: Int, offsetX: Float, offsetY: Float, offsetZ: Float): Boolean = {
		var y1: Int = y

		var meta: Int = world.getBlockMetadata(x, y1, z)
		while (meta > 0) {
			y1 -= 1
			meta = world.getBlockMetadata(x, y1, z)

		}

		if (!player.isSneaking) {
			player.openGui(WeepingAngels, WAOptions.statueGui, world, x, y1, z)
			return true
		}

		false
	}

}
