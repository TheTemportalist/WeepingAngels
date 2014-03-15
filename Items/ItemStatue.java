package com.countrygamer.weepingangels.Items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.countrygamer.core.Items.ItemBase;
import com.countrygamer.weepingangels.WeepingAngelsMod;
import com.countrygamer.weepingangels.Blocks.TileEnt.TileEntityPlinth;
import com.countrygamer.weepingangels.Entity.EntityStatue;
import com.countrygamer.weepingangels.lib.Util;

public class ItemStatue extends ItemBase {
	private Class statue;
	private int armorId;
	public int statueYaw = 0;

	public ItemStatue(String modid, String name, Class class1) {
		super(modid, name);
		statue = class1;
	}

	public ItemStatue(String modid, String name, Class class1, int j) {
		this(modid, name, class1);
		armorId = j;
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer,
	                         World world, int i, int j, int k, int l, float par8, float par9,
	                         float par10) {
		if (l == 0) {
			//WeepingAngelsMod.log.info("0");
			return false;
		}
		if (!world.getBlock(i, j, k).getMaterial().isSolid()) {
			//WeepingAngelsMod.log.info("Not Solid");
			return false;
		}
		if (l == 1) {
			j++;
		}
		if (l == 2) {
			k--;
		}
		if (l == 3) {
			k++;
		}
		if (l == 4) {
			i--;
		}
		if (l == 5) {
			i++;
		}
		if (!WeepingAngelsMod.plinthBlock.canPlaceBlockAt(world, i, j, k)) {
			//WeepingAngelsMod.log.info("Cannot Place");
			return false;
		}
		if (l == 1) {
			statueYaw = MathHelper
					.floor_double((double) ((entityplayer.rotationYaw + 180f) * 16.0F / 360.0F) + 0.5D) & 15;
			world.setBlock(i, j, k, WeepingAngelsMod.plinthBlock, 1, 3);
		} else {
			world.setBlock(i, j, k, WeepingAngelsMod.plinthBlock, l, 3);
		}
		EntityStatue entitystatue = null;
		entitystatue = Util.getEntityStatue(world, statue);
		if (entitystatue != null) {
			//WeepingAngelsMod.log.info("Setting Statue");
			TileEntityPlinth tileentityplinth = (TileEntityPlinth) world
					.getTileEntity(i, j, k);
			tileentityplinth.setRotation(statueYaw);
			tileentityplinth.statueType = entitystatue.dropId;
			itemstack.stackSize--;
			/*
			if (CG_Core.DEBUG)
				WeepingAngelsMod.log.info("l: " + l + " yaw: " + statueYaw
						+ " playerYaw: " + entityplayer.rotationYaw);
			*/
		}
		/*
		 * if(tileentityplinth != null) {
		 * ModLoader.getMinecraftInstance().displayGuiScreen(new
		 * GuiEditPlinth(tileentityplinth)); }
		 */
		return true;
	}

}