package com.countrygamer.angel.common;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import com.countrygamer.angel.common.entity.EntityWeepingAngel;
import com.countrygamer.angel.common.extended.ExtendedAngelPlayer;
import com.countrygamer.angel.common.item.WAItems;
import com.countrygamer.core.Base.Plugin.extended.ExtendedEntity;
import com.countrygamer.countrygamercore.common.Core;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;

public class EventHandler {
	
	@SubscribeEvent
	public void entityDeathEvent(LivingDeathEvent event) {
		if (event.entityLiving != null) {
			EntityLivingBase ent = event.entityLiving;
			if (ent instanceof EntityPlayer
					&& event.source.getEntity() instanceof EntityWeepingAngel) {
				EntityPlayer player = (EntityPlayer) ent;
				ExtendedAngelPlayer playerProps = (ExtendedAngelPlayer) ExtendedEntity.getExtended(
						player, ExtendedAngelPlayer.class);
				if (playerProps.isConvertting()) {
					if (!player.worldObj.isRemote) {
						EntityWeepingAngel angel = new EntityWeepingAngel(player.worldObj);
						angel.setPositionAndRotation(player.posX, player.posY, player.posZ,
								player.rotationYaw, player.rotationPitch);
						angel.setHealth(playerProps.getAngelHealth());
						player.worldObj.spawnEntityInWorld(angel);
						player.addStat(WeepingAngels.angelAchieve3, 1);
					}
					playerProps.setConvertting(false);
					playerProps.setAngelHealth(0.0F);
					playerProps.clearAngelTicks();
				}
				else {
					// player.addStat(WeepingAngels.angelAchieve2, 1);
				}
			}
			//
			if (ent instanceof EntityWeepingAngel
					&& event.source.getEntity() instanceof EntityPlayer) {
				EntityPlayer player = ((EntityPlayer) event.source.getEntity());
				if (!player.worldObj.isRemote) {
					player.addStat(WeepingAngels.angelAchieve1, 1);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void playerTickEvent(PlayerTickEvent event) {
		// if (Core.DEBUG) WeepingAngelsMod.log.info("Tick Event");
		Side side = event.side;
		if (side == Side.CLIENT) {
			
		}
		if (side == Side.SERVER) {
			// if (Core.DEBUG) WeepingAngelsMod.log.info("Server Tick Event");
			if (event.phase == TickEvent.Phase.START) {
				// if (Core.DEBUG)
				// WeepingAngelsMod.log.info("Server Start Tick Event");
				EntityPlayer player = (EntityPlayer) event.player;
				ExtendedAngelPlayer playerProperties = (ExtendedAngelPlayer) ExtendedEntity
						.getExtended(player, ExtendedAngelPlayer.class);
				if (player.capabilities.isCreativeMode) {
					playerProperties.setConvertting(false);
					;
					playerProperties.setAngelHealth(0.0F);
					playerProperties.clearAngelTicks();
					;
				}
				
				if (playerProperties.isConvertting()) {
					// if (Core.DEBUG) WeepingAngelsMod.log.info("Has Effect");
					if (playerProperties.getAngelHealth() >= WAOptions.maxHealth_Angel) {
						// WeepingAngelsMod.log.info("Kill Player now");
						if (!player.capabilities.disableDamage
								|| !player.capabilities.isCreativeMode) {
							EntityWeepingAngel angel = new EntityWeepingAngel(player.worldObj);
							angel.setPositionAndRotation(player.posX, player.posY, player.posZ,
									player.rotationYaw, player.rotationPitch);
							player.attackEntityFrom(DamageSource.causeMobDamage(angel),
									player.getMaxHealth());
						}
					}
					else if (playerProperties.getTicksTillAngelHeal() <= 0) {
						playerProperties.setAngelHealth(playerProperties.getAngelHealth() + 1);
						playerProperties.resetAngelTicks();
					}
					else {
						playerProperties.decrementTicks();
					}
					WeepingAngels.logger.info("Angel Convert Health: "
							+ playerProperties.getAngelHealth());
				}
				else {
					// WeepingAngelsMod.log.info("angelConvertHealth == false");
					
				}
			}
		}
	}
	
	@SubscribeEvent
	public void itemPickUp(ItemPickupEvent event) {
		EntityItem item = event.pickedUp;
		if (Core.DEBUG)
			WeepingAngels.logger.info(item.getEntityItem().getItem().getUnlocalizedName() + ":"
					+ WAItems.statue.getUnlocalizedName());
		if (item.getEntityItem().getItem() == WAItems.statue) {
			// event.player.addStat(WeepingAngelsMod.angelAchieve, 1);
		}
	}
	
}
