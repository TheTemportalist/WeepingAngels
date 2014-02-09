package com.countrygamer.weepingangels.Handlers.Packet;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import com.countrygamer.countrygamer_core.Handler.AbstractPacket;

public class VortexNBTPacket extends AbstractPacket {

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		// TODO Auto-generated method stub

	}

	// Util
	private void handleVortex(DataInputStream inputStream, EntityPlayer player) {
		try {
			ItemStack stack = this.readItemStack(inputStream);
			NBTTagCompound tagCom = stack.getTagCompound();
			player.setCurrentItemOrArmor(0, stack);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

	}

	public static ItemStack readItemStack(DataInput par0DataInput)
			throws IOException {
		ItemStack itemstack = null;
		short short1 = par0DataInput.readShort();
		System.out.println("[RECIEVING]itemstack id: " + short1);

		if (short1 >= 0) {
			byte b0 = par0DataInput.readByte();
			short short2 = par0DataInput.readShort();
			itemstack = new ItemStack(Item.getItemById(short1), b0, short2);
			itemstack.stackTagCompound = readNBTTagCompound(par0DataInput);

			System.out.println("[RECIEVING]itemstack stacksize: " + b0);
			System.out.println("[RECIEVING]itemstack damage: " + short2);

		}

		return itemstack;
	}

	public static NBTTagCompound readNBTTagCompound(DataInput par0DataInput)
			throws IOException {
		short short1 = par0DataInput.readShort();
		System.out.println("[RECIEVING]abyte length: " + short1);

		if (short1 < 0) {
			return null;
		} else {
			byte[] abyte = new byte[short1];
			par0DataInput.readFully(abyte);
			return CompressedStreamTools.decompress(abyte);
		}
	}

	public static void writeItemStack(ItemStack par0ItemStack,
			DataOutput par1DataOutput) throws IOException {
		if (par0ItemStack == null) {
			par1DataOutput.writeShort(-1);
		} else {
			par1DataOutput.writeShort(Item.getIdFromItem(par0ItemStack
					.getItem()));
			par1DataOutput.writeByte(par0ItemStack.stackSize);
			par1DataOutput.writeShort(par0ItemStack.getItemDamage());
			NBTTagCompound nbttagcompound = null;

			if (par0ItemStack.getItem().isDamageable()
					|| par0ItemStack.getItem().getShareTag()) {
				nbttagcompound = par0ItemStack.stackTagCompound;
			}

			writeNBTTagCompound(nbttagcompound, par1DataOutput);

			System.out.println("[SENDING]itemstack item: "
					+ par0ItemStack.getItem().getUnlocalizedName());
			System.out.println("[SENDING]itemstack stacksize: "
					+ par0ItemStack.stackSize);
			System.out.println("[SENDING]itemstack damage: "
					+ par0ItemStack.getItemDamage());
		}
	}

	protected static void writeNBTTagCompound(
			NBTTagCompound par0NBTTagCompound, DataOutput par1DataOutput)
			throws IOException {
		if (par0NBTTagCompound == null) {
			par1DataOutput.writeShort(-1);
		} else {
			byte[] abyte = CompressedStreamTools.compress(par0NBTTagCompound);
			par1DataOutput.writeShort((short) abyte.length);
			par1DataOutput.write(abyte);

			System.out
					.println("[SENDING]abyte length: " + (short) abyte.length);
			System.out.println("[SENDING]abyte: " + abyte);
		}
	}

}
