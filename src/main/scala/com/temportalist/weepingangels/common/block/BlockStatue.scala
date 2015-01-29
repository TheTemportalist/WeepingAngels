package com.temportalist.weepingangels.common.block

import com.temportalist.origin.library.common.utility.Drops
import com.temportalist.origin.wrapper.common.block.BlockWrapperTE
import com.temportalist.weepingangels.common.lib.States
import com.temportalist.weepingangels.common.tile.TEStatue
import com.temportalist.weepingangels.common.{WAOptions, WeepingAngels}
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.{BlockState, IBlockState}
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{BlockPos, EnumFacing, EnumWorldBlockLayer}
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

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

	override def createBlockState(): BlockState = {
		/*
		new ExtendedBlockState(this,
			Array[IProperty](States.STATUE_VERT),
			Array[IUnlistedProperty[_]](States.HAS_RENDERD)
		)
		*/
		new BlockState(this, States.STATUE_VERT)
	}

	override def getActualState(state: IBlockState, worldIn: IBlockAccess,
			pos: BlockPos): IBlockState = {
		var pos2: BlockPos = pos
		do {
			if (worldIn.getBlockState(pos2).getBlock == this && worldIn.getTileEntity(pos2) != null)
				return state.withProperty(States.STATUE_VERT, pos.getY - pos2.getY)
			pos2 = pos2.down()
		}
		while (worldIn.getBlockState(pos2).getBlock == this && worldIn.getTileEntity(pos2) == null)
		state
	}

	override def getMetaFromState(state: IBlockState): Int = {
		state.getValue(States.STATUE_VERT).asInstanceOf[Int]
	}

	override def getStateFromMeta(meta: Int): IBlockState = {
		this.getDefaultState.withProperty(States.STATUE_VERT, meta)
	}

	override def hasTileEntity(state: IBlockState): Boolean = {
		state.getValue(States.STATUE_VERT) == 0
	}

	override def isOpaqueCube: Boolean = false

	override def getRenderType: Int = -1

	override def isFullCube: Boolean = false

	@SideOnly(Side.CLIENT)
	override def getBlockLayer: EnumWorldBlockLayer = EnumWorldBlockLayer.CUTOUT_MIPPED

	override def canPlaceBlockAt(worldIn: World, pos: BlockPos): Boolean = {
		for (y <- 0 until this.maxHeight)
			if (worldIn.getBlockState(pos.up(y)).getBlock != Blocks.air)
				return false
		true
	}

	override def onBlockAdded(worldIn: World, pos: BlockPos, state: IBlockState): Unit = {
		if (state.getValue(States.STATUE_VERT) == 0) {
			for (y <- 1 until this.maxHeight) {
				worldIn.setBlockState(pos.up(y), state.withProperty(States.STATUE_VERT, y))
			}
		}
	}

	override def setBlockBoundsBasedOnState(worldIn: IBlockAccess, pos: BlockPos): Unit = {
		val height: Int = worldIn.getBlockState(pos).getValue(States.STATUE_VERT).asInstanceOf[Int]
		this.setBlockBounds(0.0F, 0 - height, 0.0F, 1F, this.maxHeight_Bound - height, 1F)
	}

	override def breakBlock(worldIn: World, pos: BlockPos, state: IBlockState): Unit = {
		val height: Int = state.getValue(States.STATUE_VERT).asInstanceOf[Int]

		for (y <- 1 to height) worldIn.setBlockToAir(pos.down(y))
		for (y <- 1 until this.maxHeight - height) worldIn.setBlockToAir(pos.up(y))

		if (state.getValue(States.STATUE_VERT) == 0) {
			worldIn.removeTileEntity(pos)
			if (this.isServer()) Drops.spawnItemStack(
				worldIn, pos.add(.5, 0, .5), new ItemStack(this, 1, 0), worldIn.rand, 10
			)
		}

	}

	override def onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState,
			placer: EntityLivingBase, stack: ItemStack): Unit = {
		val tile: TileEntity = worldIn.getTileEntity(pos)
		tile match {
			case statue: TEStatue =>
				var rotation: Float = placer.rotationYaw
				if (placer.isSneaking) rotation = 360 - rotation
				statue.setRotation(rotation)
			case _ =>
		}
	}

	override def onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState,
			playerIn: EntityPlayer, side: EnumFacing, hitX: Float, hitY: Float,
			hitZ: Float): Boolean = {
		if (!playerIn.isSneaking) {
			val pos2: BlockPos = pos.down(state.getValue(States.STATUE_VERT).asInstanceOf[Int])
			playerIn.openGui(
				WeepingAngels, WAOptions.statueGui, worldIn, pos2.getX, pos2.getY, pos2.getZ
			)
			return true
		}
		false
	}

}
