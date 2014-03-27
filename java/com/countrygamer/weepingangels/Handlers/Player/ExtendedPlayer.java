package com.countrygamer.weepingangels.Handlers.Player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

import com.countrygamer.weepingangels.WeepingAngelsMod;
import com.countrygamer.weepingangels.Client.Gui.HUDOverlay;

public class ExtendedPlayer implements IExtendedEntityProperties {

	public static final String extendedPropertyName = "ExtendedPlayer";
	private final EntityPlayer player;

	public static final int isConvertActiveiD = 20;
	public static final int angelHealthiD = 21;
	public static final int ticksiD = 22;
	public static final int ticksPerHalfHeart = (int) (WeepingAngelsMod.totalConvertTicks / HUDOverlay.maxHUDHealth);

	public ExtendedPlayer(EntityPlayer player) {
		this.player = player;

		this.player.getDataWatcher().addObject(
				ExtendedPlayer.isConvertActiveiD, 0);
		this.player.getDataWatcher().addObject(ExtendedPlayer.angelHealthiD,
				0.0F);
		this.player.getDataWatcher().addObject(ExtendedPlayer.ticksiD, 0);

	}

	public static final void register(EntityPlayer player) {
		player.registerExtendedProperties(ExtendedPlayer.extendedPropertyName,
				new ExtendedPlayer(player));
	}

	public static final ExtendedPlayer get(EntityPlayer player) {
		return (ExtendedPlayer) player
				.getExtendedProperties(extendedPropertyName);
	}

	@Override
	public void saveNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = new NBTTagCompound();
		properties.setInteger("isConvertActive", this.player.getDataWatcher()
				.getWatchableObjectInt(ExtendedPlayer.isConvertActiveiD));
		properties.setFloat("angelHealth", this.player.getDataWatcher()
				.getWatchableObjectFloat(ExtendedPlayer.angelHealthiD));
		properties
				.setInteger("ticksTillAngelHeal", this.player.getDataWatcher()
						.getWatchableObjectInt(ExtendedPlayer.ticksiD));
		compound.setTag(ExtendedPlayer.extendedPropertyName, properties);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = (NBTTagCompound) compound
				.getTag(ExtendedPlayer.extendedPropertyName);
		if (this.player != null) {
			this.player.getDataWatcher().updateObject(
					ExtendedPlayer.isConvertActiveiD,
					properties.getInteger("isConvertActive"));
			this.player.getDataWatcher().updateObject(ExtendedPlayer.angelHealthiD,
					properties.getFloat("angelHealth"));
			this.player.getDataWatcher().updateObject(ExtendedPlayer.ticksiD,
					properties.getInteger("ticksTillAngelHeal"));
		}
	}

	@Override
	public void init(Entity entity, World world) {

	}

	// Other Variable Methods
	public void setConvert(int convert) {
		this.player.getDataWatcher().updateObject(
				ExtendedPlayer.isConvertActiveiD, convert);
	}

	public void setAngelHealth(float newHealth) {
		this.player.getDataWatcher().updateObject(ExtendedPlayer.angelHealthiD,
				newHealth);
	}

	public void setTicksTillAngelHeal(int ticks) {
		this.player.getDataWatcher()
				.updateObject(ExtendedPlayer.ticksiD, ticks);
	}

	public int isConvertActive() {
		return this.player.getDataWatcher().getWatchableObjectInt(
				ExtendedPlayer.isConvertActiveiD);
	}

	public float getAngelHealth() {
		return this.player.getDataWatcher().getWatchableObjectFloat(
				ExtendedPlayer.angelHealthiD);
	}

	public int getTicksTillAngelHeal() {
		return this.player.getDataWatcher().getWatchableObjectInt(
				ExtendedPlayer.ticksiD);
	}

}
