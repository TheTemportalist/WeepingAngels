package com.countrygamer.angel.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import com.countrygamer.angel.client.gui.GuiVortex;
import com.countrygamer.angel.client.gui.HUDOverlay;
import com.countrygamer.angel.client.render.RenderWeepingAngel;
import com.countrygamer.angel.client.render.RenderWeepingAngelStatue;
import com.countrygamer.angel.client.render.TileEntityPlinthRenderer;
import com.countrygamer.angel.common.CommonProxy;
import com.countrygamer.angel.common.entity.EntityStatue;
import com.countrygamer.angel.common.entity.EntityWeepingAngel;
import com.countrygamer.angel.common.tile.TileEntityPlinth;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void registerRender() {
		MinecraftForge.EVENT_BUS.register(new HUDOverlay(Minecraft.getMinecraft()));
		
		RenderingRegistry.registerEntityRenderingHandler(EntityWeepingAngel.class,
				new RenderWeepingAngel(0.5f));
		RenderingRegistry.registerEntityRenderingHandler(EntityStatue.class,
				new RenderWeepingAngelStatue());
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPlinth.class,
				new TileEntityPlinthRenderer());
		
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == 0) return new GuiVortex(player);
		return null;
	}
	
}
