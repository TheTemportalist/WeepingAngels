package com.countrygamer.angel.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;

import com.countrygamer.angel.common.WeepingAngels;
import com.countrygamer.angel.common.tile.TileEntityPlinth;
import com.countrygamer.countrygamercore.Base.Plugin.registry.PluginBlockRegistry;
import com.countrygamer.countrygamercore.common.Core;

import cpw.mods.fml.common.registry.GameRegistry;

public class WABlocks implements PluginBlockRegistry {
	
	public static Block plinthBlock;
	public static Block blockWeepingAngelSpawn;
	
	@Override
	public void registryTileEntities() {
		TileEntity.addMapping(TileEntityPlinth.class, WeepingAngels.PLUGIN_ID + "_Plinth");
		
	}
	
	@Override
	public void registerBlocks() {
		plinthBlock = new BlockPlinth(TileEntityPlinth.class, Material.rock);
		plinthBlock.setHardness(2.0F).setResistance(10F);
		plinthBlock.setStepSound(Block.soundTypeStone);
		plinthBlock.setBlockName("Plinth");
		GameRegistry.registerBlock(plinthBlock, "Plinth");
		
		blockWeepingAngelSpawn = new BlockWeepingAngelSpawn().setHardness(0.5F);
		blockWeepingAngelSpawn.setBlockName("weepingangelspawn");
		Core.addBlockToTab(WABlocks.blockWeepingAngelSpawn);
		GameRegistry.registerBlock(blockWeepingAngelSpawn,
				"Weeping Angel Spawn Block");
	}
	
	@Override
	public void registerBlockCraftingRecipes() {
		
	}
	
	@Override
	public void registerBlockSmeltingRecipes() {
		
	}
	
	@Override
	public void registerOtherBlockRecipes() {
		
	}
	
}
