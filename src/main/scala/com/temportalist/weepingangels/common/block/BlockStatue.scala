package com.temportalist.weepingangels.common.block

import com.temportalist.origin.library.common.lib.vec.V3O
import com.temportalist.origin.library.common.utility.Stacks
import com.temportalist.origin.wrapper.common.block.BlockWrapperTE
import com.temportalist.weepingangels.common.tile.TEStatue
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
class BlockStatue(material: Material, name: String, teclass: Class[_ <: TileEntity])
		extends BlockWrapperTE(material, WeepingAngels.MODID, name, teclass) {

	val maxHeight: Int = 3
	val maxHeight_Bound: Float = 2.5F

	this.setHardness(2.0F)
	this.setResistance(10.0F)
	this.setStepSound(Block.soundTypeStone)

	override def isOpaqueCube: Boolean = false

	override def getRenderType: Int = -1

	override def canPlaceBlockAt(world: World, x: Int, y: Int, z: Int): Boolean = {
		for (y1 <- 0 until this.maxHeight)
			if (world.getBlock(x, y + y1, z) != Blocks.air) return false
		true
	}

	override def onBlockAdded(worldIn: World, x: Int, y: Int, z: Int): Unit = {
		if (worldIn.getBlockMetadata(x, y, z) == 0) {
			for (y1: Int <- 1 until this.maxHeight) {
				worldIn.setBlock(x, y + y1, z, this, y1, 3)
			}
			if (worldIn.isRemote)
				worldIn.getTileEntity(x, y, z).asInstanceOf[TEStatue].refreshTexture()
		}
	}

	override def setBlockBoundsBasedOnState(world: IBlockAccess, x: Int, y: Int, z: Int): Unit = {
		val meta: Int = world.getBlockMetadata(x, y, z)
		this.setBlockBounds(0F, 0F - meta, 0F, 1F, this.maxHeight_Bound - meta, 1F)
	}

	override def breakBlock(world: World, x: Int, y: Int, z: Int, block: Block, meta: Int): Unit = {
		for (y1 <- 1 to meta) world.setBlockToAir(x, y - y1, z)
		for (y1 <- 1 until this.maxHeight - meta) world.setBlockToAir(x, y + y1, z)
		if (meta == 0) {
			world.removeTileEntity(x, y, z)
			if (this.isServer()) Stacks.spawnItemStack(
				world, new V3O(x, y, z) + V3O.CENTER, new ItemStack(this), world.rand, 10
			)
		}
	}

	override def onBlockPlacedBy(world: World, x: Int, y: Int, z: Int, placer: EntityLivingBase,
			stack: ItemStack): Unit = {
		world.getTileEntity(x, y, z) match {
			case tile: TEStatue =>
				var rotation: Float = placer.rotationYaw
				if (placer.isSneaking) rotation = 360 - rotation
				tile.setRotation(rotation)
			case _ =>
		}
	}

	override def onBlockActivated(worldIn: World, x: Int, y: Int, z: Int, player: EntityPlayer,
			side: Int, subX: Float, subY: Float, subZ: Float): Boolean = {
		if (!player.isSneaking) {
			new V3O(x, y, z).down(worldIn.getBlockMetadata(x, y, z))
					.openGui(WeepingAngels, WAOptions.statueGui, player)
			true
		}
		else
			false
	}

}
