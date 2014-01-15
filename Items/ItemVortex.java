package WeepingAngels.Items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import CountryGamer_Core.Client.ParticleEffects;
import CountryGamer_Core.Items.ItemBase;
import CountryGamer_Core.lib.CoreUtil;
import WeepingAngels.WeepingAngelsMod;

public class ItemVortex extends ItemBase {

	public static String modeTag = "MODE";

	public ItemVortex(int id, String modid, String name) {
		super(id, modid, name);
		this.setMaxStackSize(1);
	}

	public ItemStack onItemRightClick(ItemStack itemStack, World world,
			EntityPlayer player) {

		// prevents null pointers if player scrolls too fast
		if (itemStack.itemID == this.itemID) {

			NBTTagCompound tagCom = itemStack.getTagCompound();
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
					CoreUtil.teleportPlayer(player, 10, 100, true, true);
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
				tagCom.setBoolean(this.modeTag, true);
				itemStack.setTagCompound(tagCom);
			}
		}
	}

}
