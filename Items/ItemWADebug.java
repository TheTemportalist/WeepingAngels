package WeepingAngels.Items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import CountryGamer_Core.ItemBase;
import WeepingAngels.Handlers.Player.ExtendedPlayer;

public class ItemWADebug extends ItemBase {

	public ItemWADebug(int id, String modid, String name) {
		super(id, modid, name);
		this.setCreativeTab(CreativeTabs.tabMisc);
	}

	public ItemStack onItemRightClick(ItemStack itemStack, World world,
			EntityPlayer player) {
		//Util.teleportPlayer(world, player, 0, 60, false, true);
		ExtendedPlayer playerProps = ExtendedPlayer.get(player);
		playerProps.setConvert(1);
		playerProps.setAngelHealth(0.0F);
		playerProps.setTicksTillAngelHeal(ExtendedPlayer.ticksPerHalfHeart);
		
		if (player.isInvisible()) {
			player.setInvisible(false);
		}else
			player.setInvisible(true);
		
		return itemStack;
	}

}
