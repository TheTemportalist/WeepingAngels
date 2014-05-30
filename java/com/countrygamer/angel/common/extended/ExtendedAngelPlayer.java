package com.countrygamer.angel.common.extended;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.countrygamer.angel.client.gui.HUDOverlay;
import com.countrygamer.angel.common.WAOptions;
import com.countrygamer.core.Base.Plugin.extended.ExtendedEntity;

public class ExtendedAngelPlayer extends ExtendedEntity {
	
	private boolean			isConvertActive		= false;
	private float			angelHealth			= 0.0F;
	private int				ticks				= 0;
	
	public static final int	ticksPerHalfHeart	= (int) (WAOptions.totalPoisonTicks / HUDOverlay.maxHUDHealth);
	
	public ExtendedAngelPlayer(EntityPlayer player) {
		super(player);
	}
	
	@Override
	public void saveNBTData(NBTTagCompound compound) {
		compound.setBoolean("isConvertActive", this.isConvertActive);
		compound.setFloat("angelHealth", this.angelHealth);
		compound.setInteger("ticks", this.ticks);
		
	}
	
	@Override
	public void loadNBTData(NBTTagCompound compound) {
		this.isConvertActive = compound.getBoolean("isConvertActive");
		this.angelHealth = compound.getFloat("angelHealth");
		this.ticks = compound.getInteger("ticks");
		
	}
	
	@Override
	public void init(Entity entity, World world) {
		this.syncEntity();
	}
	
	public void setConvertting(boolean val) {
		this.isConvertActive = val;
		this.syncEntity();
	}
	
	public boolean isConvertting() {
		return this.isConvertActive;
	}
	
	public void setAngelHealth(float val) {
		this.angelHealth = val;
		this.syncEntity();
	}
	
	public void resetAngelTicks() {
		this.ticks = ExtendedAngelPlayer.ticksPerHalfHeart;
		this.syncEntity();
	}
	
	public void clearAngelTicks() {
		this.ticks = 0;
		this.syncEntity();
	}
	
	public float getAngelHealth() {
		return this.angelHealth;
	}
	
	public int getTicksTillAngelHeal() {
		return this.ticks;
	}
	
	public void decrementTicks() {
		this.ticks -= 1;
		this.syncEntity();
	}
	
}
