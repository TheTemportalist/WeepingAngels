package com.countrygamer.weepingangels.Items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;

import com.countrygamer.countrygamer_core.Core;
import com.countrygamer.countrygamer_core.Items.ItemBase;
import com.countrygamer.countrygamer_core.lib.CoreUtil;
import com.countrygamer.weepingangels.WeepingAngelsMod;
import com.countrygamer.weepingangels.Handlers.Player.ExtendedPlayer;

public class ItemWADebug extends ItemBase {

	public ItemWADebug(String modid, String name) {
		super(modid, name);
		this.setCreativeTab(CreativeTabs.tabMisc);
	}

	public ItemStack onItemRightClick(ItemStack itemStack, World world,
			EntityPlayer player) {
		// Util.teleportPlayer(world, player, 0, 60, false, true);
		/*
		ExtendedPlayer playerProps = ExtendedPlayer.get(player);
		if (playerProps == null) {
			ExtendedPlayer.register(player);
			playerProps = ExtendedPlayer.get(player);
		}
		playerProps.setConvert(1);
		playerProps.setAngelHealth(0.0F);
		playerProps.setTicksTillAngelHeal(ExtendedPlayer.ticksPerHalfHeart);
		*/
		// Goal, print the items it takes to craft a stone pickaxe
		/*
		if (!world.isRemote) {
			List recipes = CoreUtil.getInversedRecipies();
			Core.log.info(recipes.size() + ":"
					+ CraftingManager.getInstance().getRecipeList().size());
			for (int rIndex = 0; rIndex < recipes.size(); rIndex++) {
				CoreUtil.InversedRecipe recipe = (CoreUtil.InversedRecipe) recipes
						.get(rIndex);
				if (recipe.input.getItem() == Core
						.getItemFromName("iron_pickaxe")) {
					Core.log.info("Found Iron Pickaxe Recipe");
					String str = "";
					for (int i = 0; i < recipe.itemsOutput.length; i++) {
						String name = "";
						if (recipe.itemsOutput[i] != null)
							name = recipe.itemsOutput[i].getUnlocalizedName();
						str += " " + i + ";" + name;
					}
					Core.log.info(str);
				}
			}

		}
		*/
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
