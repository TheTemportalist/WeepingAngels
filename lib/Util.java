package WeepingAngels.lib;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import WeepingAngels.Entity.EntityStatue;
import WeepingAngels.Handlers.Packet.PacketHandler;
import cpw.mods.fml.common.FMLLog;

public class Util {

	public static Packet250CustomPayload buildTeleportPacket(String channel,
			int dimID, double[] coords) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeInt(dimID);
			outputStream.writeDouble(coords[0]);
			outputStream.writeDouble(coords[1]);
			outputStream.writeDouble(coords[2]);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = channel;
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		return packet;
	}

	public static Packet250CustomPayload buildNBTPacket(String channel,
			ItemStack stack) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			PacketHandler.writeItemStack(stack, outputStream);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = channel;
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		return packet;
	}

	public static EntityStatue getEntityStatue(World world, Class statue) {
		try {
			return (EntityStatue) statue.getDeclaredConstructors()[0]
					.newInstance(new Object[] { world });
		} catch (InstantiationException instantiationexception) {
			FMLLog.log(Level.SEVERE, instantiationexception.getMessage());
		} catch (IllegalAccessException illegalaccessexception) {
			FMLLog.getLogger().log(Level.SEVERE,
					illegalaccessexception.getMessage());
		} catch (IllegalArgumentException illegalargumentexception) {
			FMLLog.getLogger().log(Level.SEVERE,
					illegalargumentexception.getMessage());
		} catch (InvocationTargetException invocationtargetexception) {
			FMLLog.getLogger().log(Level.SEVERE,
					invocationtargetexception.getMessage());
		} catch (SecurityException securityexception) {
			FMLLog.getLogger()
					.log(Level.SEVERE, securityexception.getMessage());
		}
		return null;
	}

}
