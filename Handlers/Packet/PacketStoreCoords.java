package com.countrygamer.weepingangels.Handlers.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import com.countrygamer.countrygamer_core.Handler.AbstractPacket;

public class PacketStoreCoords extends AbstractPacket {
	
	NBTTagCompound coords = new NBTTagCompound();
	
	public PacketStoreCoords(){}
	public PacketStoreCoords(NBTTagCompound coordsCom){
		this.coords = coordsCom;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		// set that from this
		ByteBufOutputStream bos = new ByteBufOutputStream(buffer);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			byte[] abyte = CompressedStreamTools.compress(this.coords);
			outputStream.writeShort((short) abyte.length);
			outputStream.write(abyte);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		// set this from that
		ByteBufInputStream bbis = new ByteBufInputStream(buffer);
		DataInputStream inputStream = new DataInputStream(bbis);
		short short1;
		try {
			short1 = inputStream.readShort();
			if (short1 >= 0) {
				byte[] abyte = new byte[short1];
				inputStream.readFully(abyte);
				this.coords = CompressedStreamTools.decompress(abyte);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		ItemStack playerStack = player.getHeldItem();
		NBTTagCompound tagCom = playerStack.getTagCompound();
		tagCom.setTag("Coords", this.coords);
		playerStack.setTagCompound(tagCom);
		player.setCurrentItemOrArmor(0, playerStack);
		
	}
	
	
	
}
