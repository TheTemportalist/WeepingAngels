package WeepingAngels.Handlers;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import WeepingAngels.WeepingAngelsMod;
import WeepingAngels.Entity.EntityWeepingAngel;
import WeepingAngels.Handlers.Player.ExtendedPlayer;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ServerTickHandler implements ITickHandler {

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		if (type.contains(TickType.PLAYER)
				&& tickData[0] instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) tickData[0];
			ExtendedPlayer playerProperties = ExtendedPlayer.get(player);
			
			if (player.capabilities.isCreativeMode) {
				playerProperties.setConvert(0);
				playerProperties.setAngelHealth(0.0F);
				playerProperties.setTicksTillAngelHeal(0);
			}
			
			if (playerProperties.isConvertActive() == 1) {
				if (playerProperties.getAngelHealth() >= 20) {
					// WeepingAngelsMod.log.info("Kill Player now");
					if (!player.capabilities.disableDamage
							|| !player.capabilities.isCreativeMode) {
						EntityWeepingAngel angel = new EntityWeepingAngel(
								player.worldObj);
						angel.setPositionAndRotation(player.posX, player.posY,
								player.posZ, player.rotationYaw,
								player.rotationPitch);
						player.attackEntityFrom(
								DamageSource.causeMobDamage(angel),
								player.getMaxHealth());
					}
				} else if (playerProperties.getTicksTillAngelHeal() <= 0) {
					playerProperties.setAngelHealth(playerProperties.getAngelHealth() + 1);
					playerProperties.setTicksTillAngelHeal(ExtendedPlayer.ticksPerHalfHeart);
				} else {
					playerProperties.setTicksTillAngelHeal(playerProperties.getTicksTillAngelHeal() - 1);
				}
				WeepingAngelsMod.log.info("Angel Convert Health: "
						+ playerProperties.getAngelHealth());
			} else {
				// WeepingAngelsMod.log.info("angelConvertHealth == false");
			}

		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {

	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.PLAYER, TickType.SERVER);
	}

	@Override
	public String getLabel() {
		return null;
	}

}
