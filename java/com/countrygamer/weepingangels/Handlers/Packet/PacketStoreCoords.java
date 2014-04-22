package com.countrygamer.weepingangels.Handlers.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import com.countrygamer.core.Base.packet.AbstractPacket;
import com.countrygamer.weepingangels.WeepingAngelsMod;

import cpw.mods.fml.common.network.ByteBufUtils;

public class PacketStoreCoords extends AbstractPacket {
	
	NBTTagCompound coords = new NBTTagCompound();
	
	public PacketStoreCoords(){}
	public PacketStoreCoords(NBTTagCompound coordsCom){
		this.coords = coordsCom;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		// set that from this
		ByteBufUtils.writeTag(buffer, this.coords);
		
		/*
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
		*/
		
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		// set this from that
		this.coords = ByteBufUtils.readTag(buffer);
		
		/*
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
		*/
		
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		this.storeCoords(player);
		
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		this.storeCoords(player);
		
	}
	
	private void storeCoords(EntityPlayer player) {
		ItemStack playerStack = player.getHeldItem().copy();
		NBTTagCompound tagCom = playerStack.getTagCompound();
		tagCom.setTag("Coords", this.coords);
		playerStack.setTagCompound(tagCom);
		player.setCurrentItemOrArmor(0, playerStack);
		
		Minecraft.getMinecraft().displayGuiScreen(null);
		
	}
	
	
	
}
