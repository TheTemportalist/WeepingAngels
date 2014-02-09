package com.countrygamer.weepingangels.Handlers;

import net.minecraftforge.client.event.sound.SoundLoadEvent;

import com.countrygamer.weepingangels.lib.Reference;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class SoundEventHandler {
	// Sound Events
	/**
	 * Tutorial on adding sounds for 1.6.X:
	 * http://www.minecraftforum.net/topic/1886370
	 * -forge16x-mazs-tutorials-custom-sounds-fluids190713/
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public void onSound(SoundLoadEvent event) {
		// TODO
		// event.manager.addSound(Reference.BASE_TEX + "crumble.ogg");
		// event.manager.addSound(Reference.BASE_TEX + "light.ogg");
		// event.manager.addSound(Reference.BASE_TEX + "stone1.ogg");
		// event.manager.addSound(Reference.BASE_TEX + "stone2.ogg");
		// event.manager.addSound(Reference.BASE_TEX + "stone3.ogg");
		// event.manager.addSound(Reference.BASE_TEX + "stone4.ogg");
		// event.manager.addSound(Reference.BASE_TEX + "teleport_activate.ogg");
		/*
		 * // Args: entity, sound, volume (relative to 1.0), and frequency (or
		 * pitch, also relative to 1.0)
		 * WorldObject.playSoundAtEntity(EntityPlayerObject, "mod_id:hit", 1.0F,
		 * 1.0F); // if you have file names which ends with an number, exclude
		 * the number when using playSound!
		 */
	}

}
