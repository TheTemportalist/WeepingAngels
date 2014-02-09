package WeepingAngels.Items;

import java.util.ArrayList;
import java.util.List;

import WeepingAngels.WeepingAngelsMod;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import CountryGamer_Core.CG_Core;
import CountryGamer_Core.Items.ItemBase;
import CountryGamer_Core.lib.CoreUtil;

public class ItemWADebug extends ItemBase {
	
	public ItemWADebug(int id, String modid, String name) {
		super(id, modid, name);
		this.setCreativeTab(CreativeTabs.tabMisc);
	}
	
	public ItemStack onItemRightClick(ItemStack itemStack, World world,
			EntityPlayer player) {
		// Util.teleportPlayer(world, player, 0, 60, false, true);
		/*
		 * ExtendedPlayer playerProps = ExtendedPlayer.get(player);
		 * playerProps.setConvert(1); playerProps.setAngelHealth(0.0F);
		 * playerProps.setTicksTillAngelHeal(ExtendedPlayer.ticksPerHalfHeart);
		 */
		
		// Goal, print the items it takes to craft a stone pickaxe
		if (!world.isRemote) {
			List recipes = CoreUtil.getInversedRecipies();
			CG_Core.log.info(recipes.size() + ":"
					+ CraftingManager.getInstance().getRecipeList().size());
			for (int rIndex = 0; rIndex < recipes.size(); rIndex++) {
				CoreUtil.InversedRecipe recipe = (CoreUtil.InversedRecipe) recipes
						.get(rIndex);
				if (recipe.input.itemID == Item.pickaxeIron.itemID) {
					CG_Core.log.info("Found Iron Pickaxe Recipe");
					String str = "";
					for (int i = 0; i < recipe.itemsOutput.length; i++) {
						String name = "";
						if (recipe.itemsOutput[i] != null)
							name = recipe.itemsOutput[i].getUnlocalizedName();
						str += " " + i + ";" + name;
					}
					CG_Core.log.info(str);
				}
			}
			
		}
		return itemStack;
	}
	
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player,
			World world, int x, int y, int z, int side, float par8, float par9,
			float par10) {
		
		// int dir = CoreUtil.getDirection(player);
		// Util.generateAngelDungeon(world, null, null, x, y, z);
		
		return false;
	}
}
