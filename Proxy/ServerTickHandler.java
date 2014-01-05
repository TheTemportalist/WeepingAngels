package WeepingAngels.Proxy;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import WeepingAngels.WeepingAngelsMod;
import WeepingAngels.Entity.EntityWeepingAngel;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ServerTickHandler implements ITickHandler {

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		if (type.contains(TickType.PLAYER)
				&& tickData[0] instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) tickData[0];
			NBTTagCompound pData = player.getEntityData();
			if (pData.getBoolean("angelConvertActive")) {
				if (pData.getInteger("angelHealTick") <= 0) {
					pData.setInteger("angelHealth",
							pData.getInteger("angelHealth") + 1);
					pData.setInteger("angelHealTick",
							WeepingAngelsMod.maxConvertTicks);
				}
				if (pData.getInteger("angelHealth") >= 20) {
					//WeepingAngelsMod.log.info("Kill Player now");
					if (!player.capabilities.disableDamage ||
							!player.capabilities.isCreativeMode) {
						EntityWeepingAngel angel = new EntityWeepingAngel(
								player.worldObj);
						angel.setPositionAndRotation(player.posX, player.posY,
								player.posZ, player.rotationYaw,
								player.rotationPitch);
						player.attackEntityFrom(
								DamageSource.causeMobDamage(angel),
								player.getMaxHealth());
					}
				} else {
					pData.setInteger("angelHealTick",
							pData.getInteger("angelHealTick") - 1);
				}
				//WeepingAngelsMod.log.info("Angel Convert Health: "
				//		+ pData.getInteger("angelHealth"));
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
