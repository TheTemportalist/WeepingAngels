package WeepingAngels.Handlers;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import WeepingAngels.WeepingAngelsMod;
import WeepingAngels.Entity.EntityWeepingAngel;
import cpw.mods.fml.common.IPickupNotifier;
import cpw.mods.fml.common.IPlayerTracker;

public class EventHandler implements IPickupNotifier, IPlayerTracker {
	
	// General Events
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

	@ForgeSubscribe
	public void entityDeathEvent(LivingDeathEvent event) {
		if (event.entityLiving != null) {
			EntityLivingBase ent = event.entityLiving;
			if (ent instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) ent;
				if (player.getEntityData().getBoolean("angelConvertActive")) {
					if (!player.worldObj.isRemote) {
						EntityWeepingAngel angel = new EntityWeepingAngel(
								player.worldObj);
						angel.setPositionAndRotation(player.posX, player.posY,
								player.posZ, player.rotationYaw,
								player.rotationPitch);
						player.worldObj.spawnEntityInWorld(angel);
					}
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
		boolean hasAllKeys = player.getEntityData()
				.hasKey("angelConvertActive")
				&& player.getEntityData().hasKey("angelHealth")
				&& player.getEntityData().hasKey("angelHealTick");
		if (!hasAllKeys) {
			player.getEntityData().setBoolean("angelConvertActive", false);
			player.getEntityData().setInteger("angelHealth", 0);
			player.getEntityData().setInteger("angelHealTick", 0);
		}
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

}
