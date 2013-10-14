package WeepingAngels.Client.Sounds;

import java.util.logging.Level;

import cpw.mods.fml.common.FMLLog;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;

public class WeepingAngelsMod_EventSounds {

	@ForgeSubscribe
	public void onSound(SoundLoadEvent event)
	{
		try
		{
			
			event.manager.addSound("weepingangels:crumble.ogg");
			event.manager.addSound("weepingangels:light.ogg");
			event.manager.addSound("weepingangels:stone1.ogg");
			event.manager.addSound("weepingangels:stone2.ogg");
			event.manager.addSound("weepingangels:stone3.ogg");
			event.manager.addSound("weepingangels:stone4.ogg");
			event.manager.addSound("weepingangels:teleport_activate.ogg");
		}
		catch(Exception e)
		{
			FMLLog.log(Level.SEVERE, e, "Weeping Angels Mod failed to register one or more sounds.");
			FMLLog.severe(e.getMessage());
		}
	}
}
