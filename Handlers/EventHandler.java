package WeepingAngels.Handlers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import WeepingAngels.WeepingAngelsMod;
import WeepingAngels.Entity.EntityWeepingAngel;
import WeepingAngels.Handlers.Player.ExtendedPlayer;
import cpw.mods.fml.common.IPickupNotifier;
import cpw.mods.fml.common.IPlayerTracker;

public class EventHandler implements IPickupNotifier, IPlayerTracker {

	// General Events
	@ForgeSubscribe
	public void entityDeathEvent(LivingDeathEvent event) {
		if (event.entityLiving != null) {
			EntityLivingBase ent = event.entityLiving;
			if (ent instanceof EntityPlayer) {
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
						player.addStat(WeepingAngelsMod.angelAchieve2, 1);
					}
					playerProps.setConvert(0);
					playerProps.setAngelHealth(0.0F);
					playerProps.setTicksTillAngelHeal(0);
				}
			}
		}
	}

	@ForgeSubscribe
	public void hurtEvent(LivingHurtEvent event) {
		if (WeepingAngelsMod.morphLoaded) {
			if (event.entityLiving instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer)event.entityLiving;
				boolean angelMorphed = morph.api.Api.hasMorph(player.username, false);
				if (angelMorphed) {
					if (event.isCancelable())
						event.setCanceled(true);
				}
			}
		}

	}

	// Achivements on Item Pickup
	@Override
	public void notifyPickup(EntityItem item, EntityPlayer player) {
		if (WeepingAngelsMod.DEBUG)
			WeepingAngelsMod.log.info(item.getEntityItem().itemID + ":"
					+ WeepingAngelsMod.statue.itemID);
		if (item.getEntityItem().itemID == WeepingAngelsMod.statue.itemID) {
			player.addStat(WeepingAngelsMod.angelAchieve, 1);
		}
	}

	// Player tracker
	@Override
	public void onPlayerLogin(EntityPlayer player) {
		/*
		 * ExtendedPlayer playerProps = ExtendedPlayer.get(player); boolean
		 * hasAllKeys = player.getEntityData() .hasKey("angelConvertActive") &&
		 * player.getEntityData().hasKey("angelHealth") &&
		 * player.getEntityData().hasKey("angelHealTick"); if (!hasAllKeys) {
		 * player.getEntityData().setBoolean("angelConvertActive", false);
		 * player.getEntityData().setInteger("angelHealth", 0);
		 * player.getEntityData().setInteger("angelHealTick", 0); }
		 */
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {
	}

	@ForgeSubscribe
	public void onEntityConstructing(EntityConstructing event) {
		if (event.entity instanceof EntityPlayer
				&& ExtendedPlayer.get((EntityPlayer) event.entity) == null) {
			ExtendedPlayer.register((EntityPlayer) event.entity);
		}
	}

}
