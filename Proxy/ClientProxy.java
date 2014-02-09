package com.countrygamer.weepingangels.proxy;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

import com.countrygamer.weepingangels.Blocks.TileEnt.TileEntityPlinth;
import com.countrygamer.weepingangels.Client.Render.RenderWeepingAngel;
import com.countrygamer.weepingangels.Client.Render.RenderWeepingAngelStatue;
import com.countrygamer.weepingangels.Client.Render.TileEntityPlinthRenderer;
import com.countrygamer.weepingangels.Entity.EntityStatue;
import com.countrygamer.weepingangels.Entity.EntityWeepingAngel;
import com.countrygamer.weepingangels.Handlers.HUDOverlay;
import com.countrygamer.weepingangels.Handlers.SoundEventHandler;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends ServerProxy {

	@Override
	public void registerRenderThings() {
		//TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);

		RenderingRegistry.registerEntityRenderingHandler(
				EntityWeepingAngel.class, new RenderWeepingAngel(0.5f));
		RenderingRegistry.registerEntityRenderingHandler(EntityStatue.class,
				new RenderWeepingAngelStatue());

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPlinth.class,
				new TileEntityPlinthRenderer());
		// ModLoader.registerTileEntity(TileEntityPlinth.class,
		// "TileEntityPlinth", new TileEntityPlinthRenderer());

		MinecraftForge.EVENT_BUS.register(new SoundEventHandler());
		MinecraftForge.EVENT_BUS.register(new HUDOverlay(Minecraft
				.getMinecraft()));

	}

	public void preInit() {

	}
}
