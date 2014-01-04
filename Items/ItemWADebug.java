package WeepingAngels.Items;

import java.util.Random;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import CountryGamer_Core.ItemBase;
import WeepingAngels.WeepingAngelsMod;
import WeepingAngels.lib.Util;

public class ItemWADebug extends ItemBase {

	public ItemWADebug(int id, String modid, String name) {
		super(id, modid, name);
		this.setCreativeTab(CreativeTabs.tabMisc);
	}

	public ItemStack onItemRightClick(ItemStack itemStack, World world,
			EntityPlayer player) {
		Util.teleportPlayer(world, player, 0, 60, false, true);
		return itemStack;
	}

}
