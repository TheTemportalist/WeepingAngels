package com.countrygamer.weepingangels.Items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import com.countrygamer.core.Base.item.ItemBase;
import com.countrygamer.core.lib.CoreUtil;
import com.countrygamer.weepingangels.WeepingAngelsMod;

public class ItemVortex extends ItemBase {

	public static String modeTag = "MODE";

	public ItemVortex(String modid, String name) {
		super(modid, name);
		this.setMaxStackSize(1);
	}

	public ItemStack onItemRightClick(ItemStack itemStack, World world,
			EntityPlayer player) {

		// prevents null pointers if player scrolls too fast
		if (itemStack.getItem() == this) {

			NBTTagCompound tagCom = itemStack.getTagCompound();
			boolean mode = tagCom.getBoolean(ItemVortex.modeTag);
			if (player.isSneaking()) {
				boolean newMode = !mode;

				tagCom.setBoolean(ItemVortex.modeTag, newMode);
				itemStack.setTagCompound(tagCom);

				String message = "The Vortex Manipulator is now in ";
				if (!world.isRemote)
					if (newMode) {
						player.addChatComponentMessage(new ChatComponentText(
								message + "RANDOM mode"));
					} else {
						player.addChatComponentMessage(new ChatComponentText(
								message + "CONTROLLED mode"));
					}
			} else {
				// TODO teleportation types
				if (mode) { // Random
					CoreUtil.teleportPlayer(player, 10, 100, player.posX,
							player.posZ, true, true);
				} else { // Controlled
					// Open GUI
					player.openGui(WeepingAngelsMod.instance, 0, world,
							(int) player.posX, (int) player.posY,
							(int) player.posZ);
				}
			}

		}
		return itemStack;
	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity,
			int par4, boolean isCurrentItem) {
		if (!world.isRemote) {
			if (!itemStack.hasTagCompound()) {
				NBTTagCompound tagCom = new NBTTagCompound();
				tagCom.setBoolean(ItemVortex.modeTag, true);
				itemStack.setTagCompound(tagCom);
			}
		}
	}

}
