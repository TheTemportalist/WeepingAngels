package WeepingAngels.Inventory;

import WeepingAngels.WeepingAngelsMod;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class InventoryVortex implements IInventory {

	public final ItemStack stack;

	public InventoryVortex(ItemStack itemstack) {
		this.stack = itemstack;

		// Create a new NBT Tag Compound if one doesn't already exist, or you
		// will crash
		if (!this.stack.hasTagCompound()) {
			this.stack.setTagCompound(new NBTTagCompound());
		}
		
	}
	
	public void test() {
		if (WeepingAngelsMod.DEBUG) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
			WeepingAngelsMod.log.info("Server");
		else
			WeepingAngelsMod.log.info("Client");
		}
	}
	
	
	@Override
	public int getSizeInventory() {
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {

	}

	@Override
	public String getInvName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isInvNameLocalized() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onInventoryChanged() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void openChest() {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeChest() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return false;
	}

}
