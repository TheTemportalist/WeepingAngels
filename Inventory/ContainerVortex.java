package WeepingAngels.Inventory;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import WeepingAngels.WeepingAngelsMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;


public class ContainerVortex extends Container {
	
	private final EntityPlayer player;
	public final InventoryVortex inventory;
	
	public ContainerVortex(EntityPlayer player, InventoryVortex inventoryItem) {
		this.player = player;
		this.inventory = inventoryItem;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}
	
	
}
