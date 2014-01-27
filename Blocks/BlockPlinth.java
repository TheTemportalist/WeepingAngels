package WeepingAngels.Blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import CountryGamer_Core.CG_Core;
import WeepingAngels.WeepingAngelsMod;
import WeepingAngels.Blocks.TileEnt.TileEntityPlinth;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPlinth extends BlockContainer {
	private Class signEntityClass;
	private int idDrop;

	public BlockPlinth(int i, Class class1, Material material) {
		super(i, material);
		idDrop = 0;
		signEntityClass = class1;
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
	}

	public int idDropped(int i, Random random, int j) {
		return idDrop;
	}

	public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
		if (l > 0 && Block.blocksList[l].canProvidePower()) {
			boolean flag = world.isBlockIndirectlyGettingPowered(i, j, k)
					|| world.isBlockIndirectlyGettingPowered(i, j + 1, k);
			if (flag) {
				world.scheduleBlockUpdate(i, j, k, blockID, tickRate(world));
			}
		}
	}

	public boolean canPlaceBlockAt(World world, int i, int j, int k) {
		int l = world.getBlockId(i, j, k);
		int i1 = world.getBlockId(i, j + 1, k);
		int j1 = world.getBlockId(i, j + 2, k);
		return l == 0
				&& i1 == 0
				&& (j1 == 0 || world.getBlockMaterial(i, j + 2, k) == Material.circuits);
	}

	@Override
	public void updateTick(World world, int i, int j, int k, Random random) {
		if (world.isBlockIndirectlyGettingPowered(i, j, k)
				|| world.isBlockIndirectlyGettingPowered(i, j + 1, k)) {
			TileEntity tileEnt = world.getBlockTileEntity(i, j, k);
			if (tileEnt != null && tileEnt instanceof TileEntityPlinth) {
				TileEntityPlinth tileEntP = (TileEntityPlinth) tileEnt;
				//if (CG_Core.DEBUG)
				//	WeepingAngelsMod.log.info("Powered");
				tileEntP.ComeToLife(world, i, j, k);
				idDrop = 0;
			}
		}
	}

	public int idPicked(World world, int x, int y, int z) {
		return WeepingAngelsMod.statue.itemID;
	}

	@Override
	public void breakBlock(World world, int i, int j, int k, int par5, int par6) {
		//if (CG_Core.DEBUG)
		//	WeepingAngelsMod.log.info("BlockPlinth broken at i: " + i + " j: "
		//			+ j + " k: " + k);
		TileEntityPlinth tileentityplinth = (TileEntityPlinth) world
				.getBlockTileEntity(i, j, k);
		int var = 0;
		if (tileentityplinth != null)
			var = tileentityplinth.getBlockMetadata();
		if (var == 1) {
			idDrop = tileentityplinth.statueType;
			//if (CG_Core.DEBUG)
			//	WeepingAngelsMod.log.info("Dropped item: " + idDrop);
		} else
			idDrop = 0;
		super.breakBlock(world, i, j, k, par5, par6);
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World war1) {
		try {
			return new TileEntityPlinth();
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.blockIcon = par1IconRegister.registerIcon("weepingangels:plinth");
	}
}
