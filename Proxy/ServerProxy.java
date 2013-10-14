package WeepingAngels.Proxy;

import WeepingAngels.Blocks.TileEnt.TileEntityPlinth;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class ServerProxy {

	public void registerRenderThings()
	{
		GameRegistry.registerTileEntity(TileEntityPlinth.class, "TileEntityPlinth");
	}

	public void preInit()
	{
	}
}
