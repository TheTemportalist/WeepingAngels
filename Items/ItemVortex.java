package WeepingAngels.Items;

import WeepingAngels.lib.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import CountryGamer_Core.ItemBase;

public class ItemVortex extends ItemBase {

	public String modeTag = "MODE", teleX = "NEWPOSX", teleY = "NEWPOSY", teleZ = "NEWPOSZ";

	public ItemVortex(int id, String modid, String name) {
		super(id, modid, name);
	}

	public ItemStack onItemRightClick(ItemStack itemStack, World world,
			EntityPlayer player) {

		// prevents null pointers if player scrolls too fast
		if (itemStack.itemID == this.itemID) {
			NBTTagCompound tagCom = this.getTagCom(itemStack);
			boolean mode = tagCom.getBoolean(this.modeTag);
			if (player.isSneaking()) {
				boolean newMode = !mode;

				tagCom.setBoolean(this.modeTag, newMode);
				itemStack.setTagCompound(tagCom);

				String message = "The Vortex Manipulator is now in ";
				if (!world.isRemote)
					if (newMode)
						player.addChatMessage(message + "RANDOM mode");
					else
						player.addChatMessage(message + "CONTROLLED mode");
			} else {
				// TODO teleportation types
				if (mode) { // Random
					Util.teleportPlayer(world, player, 10, 100, true, true);
				} else { // Controlled
					// Open GUI
					
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
				tagCom.setBoolean(this.modeTag, true);
				tagCom.setDouble(this.teleX, 0.0D);
				tagCom.setDouble(this.teleY, 0.0D);
				tagCom.setDouble(this.teleZ, 0.0D);
				itemStack.setTagCompound(tagCom);
			}
		}
	}

	private NBTTagCompound getTagCom(ItemStack itemStack) {
		if (itemStack.hasTagCompound())
			return (NBTTagCompound) itemStack.getTagCompound().copy();
		else
			return null;
	}

}
