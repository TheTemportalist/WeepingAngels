package com.countrygamer.weepingangels.Handlers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import com.countrygamer.core.Core;
import com.countrygamer.weepingangels.WeepingAngelsMod;
import com.countrygamer.weepingangels.Entity.EntityWeepingAngel;
import com.countrygamer.weepingangels.Handlers.Player.ExtendedPlayer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;

public class EventHandler {// implements IPickupNotifier {

	// General Events
	@SubscribeEvent
	public void entityDeathEvent(LivingDeathEvent event) {
		if (event.entityLiving != null) {
			EntityLivingBase ent = event.entityLiving;
			if (ent instanceof EntityPlayer
					&& event.source.getEntity() instanceof EntityWeepingAngel) {
				EntityPlayer player = (EntityPlayer) ent;
				ExtendedPlayer playerProps = ExtendedPlayer.get(player);
				if (playerProps.isConvertActive() == 1) {
					if (!player.worldObj.isRemote) {
						EntityWeepingAngel angel = new EntityWeepingAngel(
								player.worldObj);
						angel.setPositionAndRotation(player.posX, player.posY,
								player.posZ, player.rotationYaw,
								player.rotationPitch);
						player.worldObj.spawnEntityInWorld(angel);
						player.addStat(WeepingAngelsMod.angelAchieve3, 1);
					}
					playerProps.setConvert(0);
					playerProps.setAngelHealth(0.0F);
					playerProps.setTicksTillAngelHeal(0);
				} else {
					player.addStat(WeepingAngelsMod.angelAchieve2, 1);
				}
			}
			//
			if (ent instanceof EntityWeepingAngel
					&& event.source.getEntity() instanceof EntityPlayer) {
				EntityPlayer player = ((EntityPlayer) event.source.getEntity());
				if (!player.worldObj.isRemote) {
					player.addStat(WeepingAngelsMod.angelAchieve1, 1);
				}
			}
		}
	}

	@SubscribeEvent
	public void hurtEvent(LivingHurtEvent event) {
		if (Core.isMorphLoaded()) {
			if (event.entityLiving instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) event.entityLiving;
				// TODO
				// boolean angelMorphed =
				// morph.api.Api.hasMorph(player.username,
				// false);
				// if (angelMorphed) {
				// if (event.isCancelable())
				// event.setCanceled(true);
				// }
			}
		}

	}

	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event) {
		if (event.entity instanceof EntityPlayer
				&& ExtendedPlayer.get((EntityPlayer) event.entity) == null) {
			ExtendedPlayer.register((EntityPlayer) event.entity);
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
				ExtendedPlayer playerProperties = ExtendedPlayer.get(player);
				if (playerProperties == null) {
					ExtendedPlayer.register(player);
					playerProperties = ExtendedPlayer.get(player);
				}
				if (player.capabilities.isCreativeMode) {
					playerProperties.setConvert(0);
					playerProperties.setAngelHealth(0.0F);
					playerProperties.setTicksTillAngelHeal(0);
				}

				if (playerProperties.isConvertActive() == 1) {
					// if (Core.DEBUG) WeepingAngelsMod.log.info("Has Effect");
					if (playerProperties.getAngelHealth() >= 20) {
						// WeepingAngelsMod.log.info("Kill Player now");
						if (!player.capabilities.disableDamage
								|| !player.capabilities.isCreativeMode) {
							EntityWeepingAngel angel = new EntityWeepingAngel(
									player.worldObj);
							angel.setPositionAndRotation(player.posX,
									player.posY, player.posZ,
									player.rotationYaw, player.rotationPitch);
							player.attackEntityFrom(
									DamageSource.causeMobDamage(angel),
									player.getMaxHealth());
						}
					} else if (playerProperties.getTicksTillAngelHeal() <= 0) {
						playerProperties.setAngelHealth(playerProperties
								.getAngelHealth() + 1);
						playerProperties
								.setTicksTillAngelHeal(ExtendedPlayer.ticksPerHalfHeart);
					} else {
						playerProperties.setTicksTillAngelHeal(playerProperties
								.getTicksTillAngelHeal() - 1);
					}
					WeepingAngelsMod.log.info("Angel Convert Health: "
							+ playerProperties.getAngelHealth());
				} else {
					// WeepingAngelsMod.log.info("angelConvertHealth == false");

				}
			}
		}
	}

	@SubscribeEvent
	public void itemPickUp(ItemPickupEvent event) {
		EntityItem item = event.pickedUp;
		if (Core.DEBUG)
			WeepingAngelsMod.log.info(item.getEntityItem().getItem()
					.getUnlocalizedName()
					+ ":" + WeepingAngelsMod.statue.getUnlocalizedName());
		if (item.getEntityItem().getItem() == WeepingAngelsMod.statue) {
			// event.player.addStat(WeepingAngelsMod.angelAchieve, 1);
		}
	}

}
