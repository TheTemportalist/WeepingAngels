package com.countrygamer.angel.common.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.countrygamer.core.Base.common.network.AbstractMessage;

import cpw.mods.fml.common.network.ByteBufUtils;

public class PacketStoreCoords extends AbstractMessage {
	
	NBTTagCompound coords = new NBTTagCompound();
	
	public PacketStoreCoords() {
	}
	
	public PacketStoreCoords(NBTTagCompound coordsCom) {
		this.coords = coordsCom;
	}
	
	@Override
	public void writeTo(ByteBuf buffer) {
		ByteBufUtils.writeTag(buffer, this.coords);
		
	}
	
	@Override
	public void readFrom(ByteBuf buffer) {
		this.coords = ByteBufUtils.readTag(buffer);
		
	}
	
	@Override
	public void handleOnClient(EntityPlayer player) {
		this.storeCoords(player);
	}
	
	@Override
	public void handleOnServer(EntityPlayer player) {
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
