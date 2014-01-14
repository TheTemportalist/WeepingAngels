package WeepingAngels.lib;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import CountryGamer_Core.lib.TeleporterCore;
import WeepingAngels.WeepingAngelsMod;
import WeepingAngels.Handlers.Packet.PacketHandler;

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

	

}
