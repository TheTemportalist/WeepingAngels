package WeepingAngels.Proxy;

import WeepingAngels.WeepingAngelsMod;
import WeepingAngels.Blocks.TileEnt.TileEntityPlinth;
import WeepingAngels.Client.Render.RenderWeepingAngel;
import WeepingAngels.Client.Render.RenderWeepingAngelStatue;
import WeepingAngels.Client.Render.TileEntityPlinthRenderer;
import WeepingAngels.Client.Sounds.WeepingAngelsMod_EventSounds;
import WeepingAngels.Entity.EntityStatue;
import WeepingAngels.Entity.EntityWeepingAngel;
import net.minecraft.src.ModLoader;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends ServerProxy{

	@Override
	public void registerRenderThings()
	{
		TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);

		RenderingRegistry.registerEntityRenderingHandler(EntityWeepingAngel.class, new RenderWeepingAngel(0.5f));
		RenderingRegistry.registerEntityRenderingHandler(EntityStatue.class, new RenderWeepingAngelStatue());

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPlinth.class, new TileEntityPlinthRenderer());
		ModLoader.registerTileEntity(TileEntityPlinth.class, "TileEntityPlinth", new TileEntityPlinthRenderer());
		
	}

	public void preInit()
	{
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new WeepingAngelsMod_EventSounds());
	}
}
