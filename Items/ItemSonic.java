package com.countrygamer.weepingangels.Items;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.countrygamer.core.Items.ItemBase;
import com.countrygamer.core.lib.CoreUtil;
import com.countrygamer.weepingangels.WeepingAngelsMod;
import com.countrygamer.weepingangels.Blocks.TileEnt.TileEntityPlinth;
import com.countrygamer.weepingangels.Entity.EntityWeepingAngel;

public class ItemSonic extends ItemBase {

	public ItemSonic(String modid, String name) {
		super(modid, name);
	}

	public ItemStack onItemRightClick(ItemStack itemStack, World world,
			EntityPlayer player) {
		this.onUse(world, player, (int) player.posX, (int) player.posY,
				(int) player.posZ);
		return itemStack;
	}

	public boolean onItemUse(ItemStack itemStack, EntityPlayer player,
			World world, int x, int y, int z, int side, float par8, float par9,
			float par10) {

		Block block = world.getBlock(x, y, z);
		if (block == WeepingAngelsMod.plinthBlock) {
			TileEntity tileEnt = world.getTileEntity(x, y, z);
			if (tileEnt instanceof TileEntityPlinth) {
				TileEntityPlinth plinth = (TileEntityPlinth) tileEnt;
				plinth.ComeToLife(world, x, y, z);
			}
		}
		if (block == Blocks.iron_door) {
			if (world.getBlock(x, y - 1, z) == Blocks.iron_door) {
				y -= 1;
			}
			// 0 & 4, 1 & 5, 2 & 6, 3 & 7
			ArrayList<Integer> metadata = new ArrayList();
			for (int i = 0; i <= 3; i++) {
				metadata.add(i + 4);
			}
			/*
			 * if (CG_Core.DEBUG) { WeepingAngelsMod.log.info("0:" +
			 * metadata.get(0) + "|" + "1:" + metadata.get(1) + "|" + "2:" +
			 * metadata.get(2) + "|" + "3:" + metadata.get(3) + "|"); }
			 */

			BlockDoor door = (BlockDoor) Blocks.iron_door;
			int meta = door.func_150012_g(world, x, y, z);
			if (meta < 0 || meta > 7)
				meta = 0;
			int newMeta = 0;
			if (meta <= 3) {
				newMeta = metadata.get(meta);
			} else {
				newMeta = metadata.indexOf(meta);
			}
			world.setBlockMetadataWithNotify(x, y, z, newMeta, 2);
			world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
			return true;
		}
		if (block == Blocks.redstone_lamp) {
			world.setBlock(x, y, z, Blocks.lit_redstone_lamp);
		} else if (block == Blocks.lit_redstone_lamp) {
			world.setBlock(x, y, z, Blocks.redstone_lamp);
		}

		this.onUse(world, player, (int) (player.posX), (int) (player.posY),
				(int) (player.posZ));
		return false;
	}

	private void onUse(World world, EntityPlayer player, int x, int y, int z) {
		int r = 3;
		for (int x1 = x - r; x1 <= x + r; x1++) {
			for (int z1 = z - r; z1 <= z + r; z1++) {
				for (int y1 = y - r; y1 <= y + r; y1++) {
					Block block = world.getBlock(x1, y1, z1);
					if (block == Blocks.glass || block == Blocks.glass_pane)
						if (CoreUtil.breakBlockAsPlayer(world, player, x1, y1,
								z1, block)) {
							int amount = 0;
							if (block == Blocks.glass) {
								amount = (new Random()).nextInt(5);
								if (amount == 4) {
									EntityItem ent = new EntityItem(world, x1,
											y1, z1, new ItemStack(Blocks.glass));
									if (!world.isRemote)
										world.spawnEntityInWorld(ent);
									return;
								}
							} else if (block == Blocks.glass_pane) {
								amount = (new Random()).nextInt(100);
								if (amount < 30)
									amount = 1;
								else
									amount = 0;
							}
							if (amount > 0) {
								EntityItem ent = new EntityItem(world, x1, y1,
										z1, new ItemStack(Blocks.glass_pane,
												amount));
								if (!world.isRemote)
									world.spawnEntityInWorld(ent);
							}
						}
				}
			}
		}
	}

	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player,
			Entity entity) {
		if (entity instanceof EntityWeepingAngel)
			return false;
		return true;
	}

}
