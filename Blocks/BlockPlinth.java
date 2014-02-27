package com.countrygamer.weepingangels.Blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.countrygamer.weepingangels.Blocks.TileEnt.TileEntityPlinth;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPlinth extends BlockContainer {
	private Class signEntityClass;
	private Item itemDrop;

	public BlockPlinth(Class class1, Material material) {
		super(material);
		itemDrop = null;
		signEntityClass = class1;
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
	}

	@Override
	public Item getItemDropped(int i, Random random, int j) {
		return itemDrop;
	}

	@Override
	public void onNeighborBlockChange(World world, int i, int j, int k,
			Block block) {
		if (block != null && block.canProvidePower()) {
			boolean flag = world.isBlockIndirectlyGettingPowered(i, j, k)
					|| world.isBlockIndirectlyGettingPowered(i, j + 1, k);
			if (flag) {
				world.scheduleBlockUpdate(i, j, k, block, tickRate(world));
			}
		}
	}

	public boolean canPlaceBlockAt(World world, int i, int j, int k) {
		Block l = world.getBlock(i, j, k);
		Block i1 = world.getBlock(i, j + 1, k);
		Block j1 = world.getBlock(i, j + 2, k);
		return l != null && i1 != null
				&& (j1 != null && j1.getMaterial() == Material.circuits);
	}

	@Override
	public void updateTick(World world, int i, int j, int k, Random random) {
		if (world.isBlockIndirectlyGettingPowered(i, j, k)
				|| world.isBlockIndirectlyGettingPowered(i, j + 1, k)) {
			TileEntity tileEnt = world.getTileEntity(i, j, k);
			if (tileEnt != null && tileEnt instanceof TileEntityPlinth) {
				TileEntityPlinth tileEntP = (TileEntityPlinth) tileEnt;
				// if (CG_Core.DEBUG)
				// WeepingAngelsMod.log.info("Powered");
				tileEntP.ComeToLife(world, i, j, k);
				itemDrop = null;
			}
		}
	}

	// @Override TODO
	// public Item idPicked(World world, int x, int y, int z) {
	// return WeepingAngelsMod.statue;
	// }

	@Override
	public void breakBlock(World world, int i, int j, int k, Block par5,
			int par6) {
		// if (CG_Core.DEBUG)
		// WeepingAngelsMod.log.info("BlockPlinth broken at i: " + i + " j: "
		// + j + " k: " + k);
		TileEntityPlinth tileentityplinth = (TileEntityPlinth) world
				.getTileEntity(i, j, k);
		int var = 0;
		if (tileentityplinth != null)
			var = tileentityplinth.getBlockMetadata();
		if (var == 1) {
			itemDrop = tileentityplinth.statueType;
			// if (CG_Core.DEBUG)
			// WeepingAngelsMod.log.info("Dropped item: " + idDrop);
		} else
			itemDrop = null;
		super.breakBlock(world, i, j, k, par5, par6);
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		try {
			return new TileEntityPlinth();
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		this.blockIcon = par1IconRegister.registerIcon("weepingangels:plinth");
	}
}
