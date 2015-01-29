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
import net.minecraft.util.{EnumWorldBlockLayer, BlockPos, EnumFacing}
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.fml.relauncher.{SideOnly, Side}

/**
 *
 *
 * @author TheTemportalist
 */
class BlockStatue(material: Material, name: String, teclass: Class[_ <: TileEntity])
		extends BlockWrapperTE(material, WeepingAngels.MODID, name, teclass) {

	this.setHardness(2.0F)
	this.setResistance(10.0F)
	this.setStepSound(Block.soundTypeStone)

	override def createBlockState(): BlockState = {
		new BlockState(this, States.STATUE_BOTTOM)
	}

	override def getActualState(state: IBlockState, worldIn: IBlockAccess,
			pos: BlockPos): IBlockState = {
		if (worldIn.getBlockState(pos.down()).getBlock == this &&
				worldIn.getTileEntity(pos.down()) != null)
			state.withProperty(States.STATUE_BOTTOM, false)
		else
			state.withProperty(States.STATUE_BOTTOM, true)
	}

	override def getMetaFromState(state: IBlockState): Int = {
		if (state.getValue(States.STATUE_BOTTOM).asInstanceOf[Boolean]) 0
		else 1
	}

	override def getStateFromMeta(meta: Int): IBlockState = {
		meta match {
			case 0 =>
				this.getDefaultState.withProperty(States.STATUE_BOTTOM, true)
			case 1 =>
				this.getDefaultState.withProperty(States.STATUE_BOTTOM, false)
			case _ => this.getDefaultState
		}
	}

	override def hasTileEntity(state: IBlockState): Boolean = {
		state.getValue(States.STATUE_BOTTOM).asInstanceOf[Boolean]
	}

	override def isOpaqueCube: Boolean = false

	override def getRenderType: Int = -1

	override def isFullCube: Boolean = false

	@SideOnly(Side.CLIENT)
	override def getBlockLayer: EnumWorldBlockLayer = EnumWorldBlockLayer.CUTOUT_MIPPED

	override def canPlaceBlockAt(worldIn: World, pos: BlockPos): Boolean = {
		worldIn.getBlockState(pos).getBlock == Blocks.air &&
				worldIn.getBlockState(pos.up()).getBlock == Blocks.air
	}

	override def onBlockAdded(worldIn: World, pos: BlockPos, state: IBlockState): Unit = {
		if (state.getValue(States.STATUE_BOTTOM).asInstanceOf[Boolean])
			worldIn.setBlockState(pos.up(), state.withProperty(States.STATUE_BOTTOM, false))
	}

	override def setBlockBoundsBasedOnState(worldIn: IBlockAccess, pos: BlockPos): Unit = {
		val meta: Int = this.getMetaFromState(worldIn.getBlockState(pos))
		this.setBlockBounds(0.0F, 0.0F - meta, 0.0F, 1F, 3F - meta, 1F)
	}

	override def breakBlock(worldIn: World, pos: BlockPos, state: IBlockState): Unit = {
		worldIn.removeTileEntity(pos)

		var nextPos: BlockPos = pos
		var nextState: IBlockState = null

		do {
			nextPos = nextPos.up()
			nextState = worldIn.getBlockState(nextPos)
			if (nextState.getBlock == this)
				worldIn.setBlockToAir(nextPos)
		} while(nextState.getBlock == this)

		nextPos = pos
		do {
			nextPos.down()
			nextState = worldIn.getBlockState(nextPos)
			if (nextState.getBlock == this)
				worldIn.setBlockToAir(nextPos)
		} while(nextState.getBlock == this)

		if (this.isServer() && state.getValue(States.STATUE_BOTTOM) == true)
			Drops.spawnItemStack(
				worldIn, pos.add(.5, 0, .5), new ItemStack(this, 1, 0), worldIn.rand, 10
			)

	}

	override def onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState,
			placer: EntityLivingBase, stack: ItemStack): Unit = {
		val tile: TileEntity = worldIn.getTileEntity(pos)
		tile match {
			case statue: TEStatue =>
				var rotation: Float = placer.rotationYaw
				if (placer.isSneaking) rotation = -rotation
				statue.setRotation(rotation)
			case _ =>
		}
	}

	override def onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState,
			playerIn: EntityPlayer, side: EnumFacing, hitX: Float, hitY: Float,
			hitZ: Float): Boolean = {
		if (!playerIn.isSneaking) {
			var pos2: BlockPos = pos
			if (state.getValue(States.STATUE_BOTTOM) == false)
				pos2 = pos2.down()
			playerIn.openGui(
				WeepingAngels, WAOptions.statueGui, worldIn, pos2.getX, pos2.getY, pos2.getZ
			)
			return true
		}
		false
	}

}
