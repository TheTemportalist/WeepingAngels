package WeepingAngels.Items;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import CountryGamer_Core.Items.ItemBase;
import WeepingAngels.WeepingAngelsMod;
import WeepingAngels.Blocks.TileEnt.TileEntityPlinth;
import WeepingAngels.Entity.EntityStatue;
import WeepingAngels.lib.Reference;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemStatue extends ItemBase {
	private Class statue;
	private int armorId;
	public int statueYaw = 0;

	public ItemStatue(int id, String modid, String name, Class class1) {
		super(id, modid, name);
		statue = class1;
	}

	public ItemStatue(int i, String modid, String name, Class class1, int j) {
		this(i, modid, name, class1);
		armorId = j;
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer,
			World world, int i, int j, int k, int l, float par8, float par9,
			float par10) {
		if (l == 0) {
			return false;
		}
		if (!world.getBlockMaterial(i, j, k).isSolid()) {
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
			return false;
		}
		if (l == 1) {
			statueYaw = MathHelper
					.floor_double((double) ((entityplayer.rotationYaw + 180f) * 16.0F / 360.0F) + 0.5D) & 15;
			world.setBlock(i, j, k, WeepingAngelsMod.plinthBlock.blockID, 1, 3);
		} else {
			world.setBlock(i, j, k, WeepingAngelsMod.plinthBlock.blockID, l, 3);
		}
		EntityStatue entitystatue = null;
		try {
			entitystatue = (EntityStatue) statue.getDeclaredConstructors()[0]
					.newInstance(new Object[] { world });
		} catch (InstantiationException instantiationexception) {
			FMLLog.log(Level.SEVERE, instantiationexception.getMessage());
		} catch (IllegalAccessException illegalaccessexception) {
			FMLLog.getLogger().log(Level.SEVERE,
					illegalaccessexception.getMessage());
		} catch (IllegalArgumentException illegalargumentexception) {
			FMLLog.getLogger().log(Level.SEVERE,
					illegalargumentexception.getMessage());
		} catch (InvocationTargetException invocationtargetexception) {
			FMLLog.getLogger().log(Level.SEVERE,
					invocationtargetexception.getMessage());
		} catch (SecurityException securityexception) {
			FMLLog.getLogger()
					.log(Level.SEVERE, securityexception.getMessage());
		}
		TileEntityPlinth tileentityplinth = (TileEntityPlinth) world
				.getBlockTileEntity(i, j, k);
		tileentityplinth.setRotation(statueYaw);
		tileentityplinth.statueType = entitystatue.dropId;
		itemstack.stackSize--;
		if (WeepingAngelsMod.DEBUG)
			WeepingAngelsMod.log.info("l: " + l + " yaw: " + statueYaw
					+ " playerYaw: " + entityplayer.rotationYaw);
		/*
		 * if(tileentityplinth != null) {
		 * ModLoader.getMinecraftInstance().displayGuiScreen(new
		 * GuiEditPlinth(tileentityplinth)); }
		 */
		return true;
	}

}