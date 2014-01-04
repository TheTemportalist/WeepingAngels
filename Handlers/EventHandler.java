package WeepingAngels.Handlers;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import WeepingAngels.WeepingAngelsMod;
import WeepingAngels.Entity.EntityWeepingAngel;
import cpw.mods.fml.common.IPickupNotifier;

public class EventHandler implements IPickupNotifier {

	@ForgeSubscribe
	public void entityJoinEvent(EntityJoinWorldEvent event) {
		Entity ent = event.entity;
		if (ent instanceof EntityPainting) {
			EntityPainting painting = (EntityPainting) ent;

			// if(painting.art.title.equalsIgnoreCase(WeepingAngelsMod.wapNAME)
			// ) {
			if (!ent.worldObj.isRemote) {
				if (WeepingAngelsMod.DEBUG) {
					WeepingAngelsMod.log.info("Spawn Weeping Angel Painting");
					WeepingAngelsMod.log.info("Direction: "
							+ painting.hangingDirection);
				}
				int r = 2;
				List rEntities = ent.worldObj.getEntitiesWithinAABB(
						EntityWeepingAngel.class, AxisAlignedBB.getBoundingBox(
								ent.posX - r, ent.posY - 1.0D, ent.posZ - r,
								ent.posX + r, ent.posY + 1.0D, ent.posZ + r));

				if (rEntities.size() != 0) {
					if (WeepingAngelsMod.DEBUG && !painting.worldObj.isRemote)
						WeepingAngelsMod.log.info("There are "
								+ rEntities.size() + " angels nearby.");
				} else {
					// side to +x -x +z -z
					/*
					 * side = meta = direction 0 = 2 = +z 1 = 5 = -x 2 = 3 = -z
					 * 3 = 4 = +x
					 */
					double x = painting.posX, y = painting.posY, z = painting.posZ;
					double i = x, k = z;
					switch (painting.hangingDirection) {
					case 0: // +z
						k += 1.0D;
						break;
					case 1: // -x
						i -= 1.0D;
						break;
					case 2: // -z
						k -= 1.0D;
						break;
					case 3: // +x
						i += 1.0D;
						break;
					default:
						// null
						return;
					}

					// for(double i = x-1; i < x+1; i++) {
					// for(double k = z-1; i < z+1; k++) {
					if (WeepingAngelsMod.DEBUG)
						WeepingAngelsMod.log.info("Checking Spots");
					if (ent.worldObj.getBlockId((int) i, (int) y, (int) k) == 0
							&& ent.worldObj.getBlockId((int) i, (int) (y + 1),
									(int) k) == 0) {
						if (WeepingAngelsMod.DEBUG)
							WeepingAngelsMod.log.info("Found Decent Spot");

						EntityWeepingAngel angel = new EntityWeepingAngel(
								ent.worldObj);
						angel.setPosition(i, y, k);
						// ent.worldObj.spawnEntityInWorld(angel);
						// int i1 = Direction.facingToDirection[];
						// EntityPainting newPaint = painting;
						// newPaint.art = EnumArt.Kebab;
						painting.setDead();
						// painting.worldObj.spawnEntityInWorld(newPaint);
						return;
					}

					// }
					// }
				}
			}
			// }
		}
	}

	public void entityDeathEvent(LivingDeathEvent event) {
		if (event.entityLiving != null) {
			EntityLivingBase ent = event.entityLiving;
			if (ent.isPotionActive(WeepingAngelsMod.angelConvert)) {
				EntityWeepingAngel angel = new EntityWeepingAngel(ent.worldObj);
				angel.setPosition(ent.posX, ent.posY, ent.posZ);
				ent.worldObj.spawnEntityInWorld(angel);
			}

		}
	}

	@ForgeSubscribe
	public void onEntityUpdate(LivingUpdateEvent event) {
		EntityLivingBase targetEntity = event.entityLiving;

		if (targetEntity.isPotionActive(WeepingAngelsMod.angelConvert)) {

			int duration = targetEntity.getActivePotionEffect(
					WeepingAngelsMod.angelConvert).getDuration();

			// id, duration, strength
			targetEntity.removePotionEffect(Potion.blindness.id);
			targetEntity.addPotionEffect(new PotionEffect(Potion.blindness.id,
					duration, 1));
			targetEntity.removePotionEffect(Potion.moveSlowdown.id);
			targetEntity.addPotionEffect(new PotionEffect(
					Potion.moveSlowdown.id, duration, 1));

			if (duration == 0) {
				targetEntity
						.removePotionEffect(WeepingAngelsMod.angelConvert.id);
				return;
			}
		}
	}

	//@ForgeSubscribe
	public void onItemPickup(EntityItemPickupEvent event) {
		if (WeepingAngelsMod.DEBUG)
			WeepingAngelsMod.log.info(event.item.getEntityItem().itemID + ":"
					+ WeepingAngelsMod.statue.itemID);
		if (event.item.getEntityItem().itemID == WeepingAngelsMod.statue.itemID) {
			event.entityPlayer
					.triggerAchievement(WeepingAngelsMod.angelAchieve);
		}
	}

	@Override
	public void notifyPickup(EntityItem item, EntityPlayer player) {
		if (WeepingAngelsMod.DEBUG)
			WeepingAngelsMod.log.info(item.getEntityItem().itemID + ":"
					+ WeepingAngelsMod.statue.itemID);
		if (item.getEntityItem().itemID == WeepingAngelsMod.statue.itemID) {
			player.addStat(WeepingAngelsMod.angelAchieve, 1);
		}
	}

}
