package com.countrygamer.weepingangels.Morph;

import javax.media.j3d.Morph;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import com.countrygamer.weepingangels.lib.Util;

public class MorphAbilityTimeLock extends Ability {

	private double moveSpeed;
	private final double basePlayerSpeed = 0.10000000149011612D;

	@Override
	public String getType() {
		return "timelock";
	}

	@Override
	public void tick() {
		MorphInfo info = null;
		if (this.getParent() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) this.getParent();
			if (!player.worldObj.isRemote) {
				info = Morph.proxy.tickHandlerServer.playerMorphInfo
						.get(player.username);
			} else {
				info = Morph.proxy.tickHandlerClient.playerMorphInfo
						.get(player.username);
			}
		}
		//
		boolean isWatched = false;
		// Get watchers
		isWatched = Util.canBeSeenMulti(this.getParent().worldObj,
				this.getParent().boundingBox, 64D, this.getParent());
		// Set
		if (isWatched) {
			this.moveSpeed = 0.0D;
		} else {
			//this.moveSpeed = 1.0F;
			this.moveSpeed = 0.2D;
		}
		this.getParent()
				.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
				.setAttribute(this.moveSpeed);
		
	}

	@Override
	public void kill() {
		this.getParent()
				.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
				.setAttribute(
						SharedMonsterAttributes.movementSpeed.getDefaultValue());
	}

	@Override
	public Ability clone() {
		return new MorphAbilityTimeLock();
	}

	@Override
	public void save(NBTTagCompound tag) {
	}

	@Override
	public void load(NBTTagCompound tag) {
	}

	@Override
	public void postRender() {
	}

	@Override
	public ResourceLocation getIcon() {
		return new ResourceLocation("morph", "textures/icon/fireImmunity.png");
	}

}
