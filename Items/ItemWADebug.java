package WeepingAngels.Items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import CountryGamer_Core.Items.ItemBase;
import WeepingAngels.Handlers.Player.ExtendedPlayer;

public class ItemWADebug extends ItemBase {

	public ItemWADebug(int id, String modid, String name) {
		super(id, modid, name);
		this.setCreativeTab(CreativeTabs.tabMisc);
	}

	public ItemStack onItemRightClick(ItemStack itemStack, World world,
			EntityPlayer player) {
		// Util.teleportPlayer(world, player, 0, 60, false, true);
		ExtendedPlayer playerProps = ExtendedPlayer.get(player);
		playerProps.setConvert(1);
		playerProps.setAngelHealth(0.0F);
		playerProps.setTicksTillAngelHeal(ExtendedPlayer.ticksPerHalfHeart);

		return itemStack;
	}

	public boolean onItemUse(ItemStack itemStack, EntityPlayer player,
			World world, int x, int y, int z, int side, float par8,
			float par9, float par10) {

		return false;
	}

}
