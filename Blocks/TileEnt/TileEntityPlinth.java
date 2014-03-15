package com.countrygamer.weepingangels.Blocks.TileEnt;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;

import com.countrygamer.weepingangels.WeepingAngelsMod;
import com.countrygamer.weepingangels.Entity.EntityWeepingAngel;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityPlinth extends TileEntity {
	public Item statueType = WeepingAngelsMod.statue;
	//public String signText[] = { "", "" };
	public int lineBeingEdited;
	private boolean isEditable;
	public Entity statueEntity;
	public int rotation;
	public boolean canBeActivated;

	public TileEntityPlinth() {
		lineBeingEdited = -1;
		isEditable = true;
		canBeActivated = true;
	}

	public void updateEntity() {
		EntityPlayer player = null;
		int r = 10;
		List list = worldObj.getEntitiesWithinAABB(EntityPlayer.class,
				AxisAlignedBB.getBoundingBox(this.xCoord - r, this.yCoord - r,
						this.zCoord - r, this.xCoord + r, this.yCoord + r,
						this.zCoord + r));
		if (!list.isEmpty())
			player = (EntityPlayer) list.get(0);
		EntityWeepingAngel angel = null;
		r /= 2;
		list = worldObj.getEntitiesWithinAABB(EntityWeepingAngel.class,
				AxisAlignedBB.getBoundingBox(this.xCoord - r, this.yCoord - r,
						this.zCoord - r, this.xCoord + r, this.yCoord + r,
						this.zCoord + r));
		if (!list.isEmpty())
			angel = (EntityWeepingAngel) list.get(0);

		if (player != null && angel != null) {
			this.ComeToLife(this.worldObj, this.xCoord, this.yCoord,
					this.zCoord);
		}

		super.updateEntity();
	}

	// Client Server Sync
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		NBTTagCompound tagCom = pkt.func_148857_g();
		this.readFromNBT(tagCom);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tagCom = new NBTTagCompound();
		this.writeToNBT(tagCom);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord,
				this.zCoord, this.blockMetadata, tagCom);
	}

	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setByte("rotation", (byte) (this.rotation & 255));
		//nbttagcompound.setString("Text1", signText[0]);
		//nbttagcompound.setString("Text2", signText[1]);
		nbttagcompound.setInteger("Type", Item.getIdFromItem(statueType));
		nbttagcompound.setBoolean("activated", canBeActivated);
		if (statueEntity != null) {
			NBTTagCompound var1 = new NBTTagCompound();
			this.statueEntity.writeToNBT(var1);
			nbttagcompound.setTag("entityStored", var1);
		}
	}

	public void readFromNBT(NBTTagCompound nbttagcompound) {
		isEditable = false;
		super.readFromNBT(nbttagcompound);
		/*
		for (int i = 0; i < 2; i++) {
			signText[i] = nbttagcompound.getString((new StringBuilder())
					.append("Text").append(i + 1).toString());
			if (signText[i].length() > 15) {
				signText[i] = signText[i].substring(0, 15);
			}
		}
		*/

		this.statueType = Item.getItemById(nbttagcompound.getInteger("Type"));
		this.rotation = nbttagcompound.getByte("rotation");
		this.canBeActivated = nbttagcompound.getBoolean("activated");
		if (nbttagcompound.hasKey("entityStored")) {
			NBTTagCompound var1 = nbttagcompound.getCompoundTag("entityStored");
			this.statueEntity.readFromNBT(var1);
		}
		// if (CG_Core.DEBUG)
		// WeepingAngelsMod.log.info("rotation: " + this.rotation
		// + " Active: " + this.canBeActivated);
	}

	public void setRotation(int par1) {
		this.rotation = par1;
	}

	public void setActivated(boolean var1) {
		this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord,
				this.zCoord, var1 ? 1 : 0, 3);
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
	}

	public int getRotation() {
		return this.rotation;
	}

	@SideOnly(Side.CLIENT)
	public boolean getActivated() {
		return this.canBeActivated;
	}

	@SideOnly(Side.CLIENT)
	public Entity getStatueEntity() {
		return this.statueEntity;
	}

	@SideOnly(Side.CLIENT)
	public Item getStatueType() {
		return this.statueType;
	}

	public void ComeToLife(World world, int i, int j, int k) {
		TileEntityPlinth tileentityplinth = (TileEntityPlinth) world
				.getTileEntity(i, j, k);
		int var = tileentityplinth.getBlockMetadata();
		if (var == 1) {
			EntityLiving entityliving = null;
			Class class1 = EntityWeepingAngel.class;
			try {
				entityliving = (EntityLiving) class1.getDeclaredConstructors()[0]
						.newInstance(new Object[] { world });
			} catch (InstantiationException instantiationexception) {
				//FMLLog.log(Level.SEVERE, instantiationexception.getMessage());
			} catch (IllegalAccessException illegalaccessexception) {
				//FMLLog.log(Level.SEVERE, illegalaccessexception.getMessage());
			} catch (IllegalArgumentException illegalargumentexception) {
				//FMLLog.log(Level.SEVERE, illegalargumentexception.getMessage());
			} catch (InvocationTargetException invocationtargetexception) {
				//FMLLog.log(Level.SEVERE, invocationtargetexception.getMessage());
			} catch (SecurityException securityexception) {
				//FMLLog.log(Level.SEVERE, securityexception.getMessage());
			}
			entityliving.setPositionAndRotation(i + 0.5, j + 0.5, k + 0.5,
					(float) (tileentityplinth.getRotation() * 360) / 16f, 0.0F);
			if (!world.isRemote)
				world.spawnEntityInWorld(entityliving);
			tileentityplinth.setActivated(false);
			tileentityplinth.validate();
			world.setTileEntity(i, j, k, tileentityplinth);
			world.setBlock(i, j, k, Blocks.stone_slab,
					world.getBlockMetadata(i, j, k), 3);
		}

	}

}
