package com.countrygamer.weepingangels.Blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.countrygamer.weepingangels.Entity.EntityWeepingAngel;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockWeepingAngelSpawn extends Block {

	public BlockWeepingAngelSpawn() {
		super(Material.rock);
	}

	/*
	 * public int idDropped(int i, Random random, int j) { return
	 * mod_WeepingAngel.statue.shiftedIndex; }
	 */

	public void onNeighborBlockChange(World world, int i, int j, int k, Block l) {
		if (l != null && l.canProvidePower()
				&& world.isBlockIndirectlyGettingPowered(i, j, k)) {
			int a1 = world.getEntitiesWithinAABB(
					EntityWeepingAngel.class,
					AxisAlignedBB.getBoundingBox(i, j, k, i + 1, j + 1, k + 1)
							.expand(2D, 2D, 2D)).size();
			if (a1 < 1) {
				spawnWeepingAngel(world, i, j, k);
			}
		}
	}

	public void onBlockPlacedBy(World world, int i, int j, int k,
			EntityLiving entityliving) {
		int l = MathHelper
				.floor_double((double) ((entityliving.rotationYaw * 4F) / 360F) + 2.5D) & 3;
		world.setBlockMetadataWithNotify(i, j, k, l, 3);
	}

	public void spawnWeepingAngel(World world, double i, double j, double k) {
		Random rand = new Random();
		//int i1 = world.getBlockMetadata((int) i, (int) j, (int) k);
		EntityWeepingAngel ewp = new EntityWeepingAngel(world);
		ewp.setLocationAndAngles(i + 0.5, j + 1, k + 0.5,
				(float) (rand.nextInt(15) * 360) / 16f, 0f);
		world.spawnEntityInWorld(ewp);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		this.blockIcon = par1IconRegister.registerIcon("stone");
	}
	
	/*
	public void onEntityCollidedWithBlock(World world, int x, int y, int z,
			Entity entity) {
		WeepingAngelsMod.log.info("collided with something");
		if (entity instanceof EntityItem) {
			EntityItem entItem = (EntityItem) entity;
			if (entItem.getEntityItem() != null) {
				ItemStack entityStack = entItem.getEntityItem();
				if (entityStack.getItem().itemID == WeepingAngelsMod.chronon.itemID) {
					entItem.setEntityItemStack(new ItemStack(Block.cobblestone));
				}
			}
		}
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World,
			int par2, int par3, int par4) {
		return null;
	}
	*/

}