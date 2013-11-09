package WeepingAngels.Proxy;

import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.registry.GameRegistry;

public class ServerProxy {

	public void registerRenderThings()
	{
		//GameRegistry.registerTileEntity(
		//		WeepingAngels.Blocks.TileEnt.TileEntityPlinth.class,
		//		"TileEntityPlinth");
		TileEntity.addMapping(
				WeepingAngels.Blocks.TileEnt.TileEntityPlinth.class,
				"TileEntityPlinth");
	}

	public void preInit()
	{
	}
}
