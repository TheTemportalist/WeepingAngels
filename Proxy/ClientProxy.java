package WeepingAngels.Proxy;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import WeepingAngels.Blocks.TileEnt.TileEntityPlinth;
import WeepingAngels.Client.Render.RenderWeepingAngel;
import WeepingAngels.Client.Render.RenderWeepingAngelStatue;
import WeepingAngels.Client.Render.TileEntityPlinthRenderer;
import WeepingAngels.Entity.EntityStatue;
import WeepingAngels.Entity.EntityWeepingAngel;
import WeepingAngels.Handlers.ClientTickHandler;
import WeepingAngels.Handlers.HUDOverlay;
import WeepingAngels.Handlers.SoundEventHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends ServerProxy {

	@Override
	public void registerRenderThings() {
		TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);

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
