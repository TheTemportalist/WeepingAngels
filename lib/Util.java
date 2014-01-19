package WeepingAngels.lib;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonMoving;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.Facing;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import CountryGamer_Core.lib.CoreUtil;
import WeepingAngels.WeepingAngelsMod;
import WeepingAngels.Entity.EntityStatue;
import WeepingAngels.Handlers.Packet.PacketHandler;
import WeepingAngels.World.Structure.ComponentAngelDungeon;
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

	public static void generateAngelDungeon(World world,
			ComponentAngelDungeon com, StructureBoundingBox box, int xOffset,
			int yOffset, int zOffset) {
		// place generation house code here
		CoreUtil.fillBlocks(world, -1 + xOffset, -10 + yOffset, 0 + zOffset,
				1 + xOffset, 4 + yOffset, 2 + zOffset, 0, 0, com, box);
		CoreUtil.placeBlock(world, 0 + xOffset, 1 + yOffset, 1 + zOffset,
				WeepingAngelsMod.plinthBlock.blockID, 1, com, box);
		CoreUtil.placeBlock(world, 0, 4, 0, Block.dirt.blockID, 0, com, box);
		// 0 == left, 1 == right, 2 == back, 3 == front
		CoreUtil.placeBlock(world, -1 + xOffset, +0 + yOffset, +1 + zOffset,
				Block.stairsCobblestone.blockID, 0, com, box);
		CoreUtil.placeBlock(world, +1 + xOffset, +0 + yOffset, +1 + zOffset,
				Block.stairsCobblestone.blockID, 1, com, box);
		CoreUtil.placeBlock(world, +0 + xOffset, +0 + yOffset, +2 + zOffset,
				Block.stairsCobblestone.blockID, 2, com, box);
		CoreUtil.placeBlock(world, +0 + xOffset, +0 + yOffset, +0 + zOffset,
				Block.stairsCobblestone.blockID, 3, com, box);

		CoreUtil.placeBlock(world, +1 + xOffset, +0 + yOffset, +0 + zOffset,
				Block.stoneSingleSlab.blockID, 5, com, box);
		CoreUtil.placeBlock(world, -1 + xOffset, +0 + yOffset, +0 + zOffset,
				Block.stoneSingleSlab.blockID, 5, com, box);
		CoreUtil.placeBlock(world, +1 + xOffset, +0 + yOffset, +2 + zOffset,
				Block.stoneSingleSlab.blockID, 5, com, box);
		CoreUtil.placeBlock(world, -1 + xOffset, +0 + yOffset, +2 + zOffset,
				Block.stoneSingleSlab.blockID, 5, com, box);

		CoreUtil.fillVariedStoneBlocks(world, -1 + xOffset, -6 + yOffset,
				0 + zOffset, 1 + xOffset, -1 + yOffset, 2 + zOffset, com, box);
		CoreUtil.fillVariedStoneBlocks(world, -1 + xOffset, -10 + yOffset,
				0 + zOffset, 1 + xOffset, -7 + yOffset, 4 + zOffset, com, box);
		CoreUtil.fillVariedStoneBlocks(world, -5 + xOffset, -10 + yOffset,
				4 + zOffset, 5 + xOffset, -6 + yOffset, 14 + zOffset, com, box);

		CoreUtil.fillBlocks(world, 0 + xOffset, -7 + yOffset, 1 + zOffset,
				0 + xOffset, -1 + yOffset, 1 + zOffset, 0, 0, com, box);
		CoreUtil.fillBlocks(world, 0 + xOffset, -9 + yOffset, 1 + zOffset,
				0 + xOffset, -8 + yOffset, 5 + zOffset, 0, 0, com, box);
		CoreUtil.fillBlocks(world, -4 + xOffset, -9 + yOffset, 5 + zOffset,
				4 + xOffset, -7 + yOffset, 13 + zOffset, 0, 0, com, box);

		int corX = 0 + xOffset, corY = -9 + yOffset, corZ = 9 + zOffset;
		CoreUtil.placeBlock(world, corX - 3, corY - 1, corZ - 3,
				Block.glowStone.blockID, 0, com, box);
		CoreUtil.placeBlock(world, corX - 3, corY - 1, corZ + 3,
				Block.glowStone.blockID, 0, com, box);
		CoreUtil.placeBlock(world, corX + 3, corY - 1, corZ - 3,
				Block.glowStone.blockID, 0, com, box);
		CoreUtil.placeBlock(world, corX + 3, corY - 1, corZ + 3,
				Block.glowStone.blockID, 0, com, box);

		CoreUtil.placeBlock(world, corX - 4, corY, corZ + 1,
				WeepingAngelsMod.plinthBlock.blockID, 1, com, box);
		CoreUtil.placeBlock(world, corX - 4, corY, corZ - 1,
				WeepingAngelsMod.plinthBlock.blockID, 1, com, box);
		CoreUtil.placeBlock(world, corX + 4, corY, corZ + 1,
				WeepingAngelsMod.plinthBlock.blockID, 1, com, box);
		CoreUtil.placeBlock(world, corX + 4, corY, corZ - 1,
				WeepingAngelsMod.plinthBlock.blockID, 1, com, box);

		CoreUtil.placeBlock(world, corX - 1, corY, corZ + 4,
				WeepingAngelsMod.plinthBlock.blockID, 1, com, box);
		CoreUtil.placeBlock(world, corX - 1, corY, corZ - 4,
				WeepingAngelsMod.plinthBlock.blockID, 1, com, box);
		CoreUtil.placeBlock(world, corX + 1, corY, corZ + 4,
				WeepingAngelsMod.plinthBlock.blockID, 1, com, box);
		CoreUtil.placeBlock(world, corX + 1, corY, corZ - 4,
				WeepingAngelsMod.plinthBlock.blockID, 1, com, box);

		CoreUtil.placeBlock(world, corX - 3, corY, corZ + 3,
				WeepingAngelsMod.plinthBlock.blockID, 1, com, box);
		CoreUtil.placeBlock(world, corX - 3, corY, corZ - 3,
				WeepingAngelsMod.plinthBlock.blockID, 1, com, box);
		CoreUtil.placeBlock(world, corX + 3, corY, corZ + 3,
				WeepingAngelsMod.plinthBlock.blockID, 1, com, box);
		CoreUtil.placeBlock(world, corX + 3, corY, corZ - 3,
				WeepingAngelsMod.plinthBlock.blockID, 1, com, box);

		CoreUtil.placeBlock(world, corX, corY + 0, corZ - 5,
				Block.fenceIron.blockID, 0, com, box);
		CoreUtil.placeBlock(world, corX, corY + 1, corZ - 5,
				Block.fenceIron.blockID, 0, com, box);

		for (int l = -1; l > -9; l--) {
			CoreUtil.placeBlock(world, 0 + xOffset, l + yOffset, 1 + zOffset,
					Block.ladder.blockID, 2, com, box);
		}
		CoreUtil.placeBlock(world, 0 + xOffset, 0 + yOffset, 1 + zOffset,
				Block.trapdoor.blockID, 5 | 8, com, box);

	}
	
	private void redstoneSonicStuff(World world, int x, int y, int z) {
		if (world.getBlockId(x, y, z) == Block.redstoneWire.blockID) {
			int meta = world.getBlockMetadata(x, y, z);
			world.setBlockMetadataWithNotify(x, y, z,
					meta >= 15 ? 0 : meta + 1, 2);
			ArrayList blocksToUpdate = new ArrayList();
			blocksToUpdate.clear();
			blocksToUpdate.add(new ChunkPosition(x + 0, y + 0, z + 0));
			// blocksToUpdate.add(new ChunkPosition(x - 1, y + 0, z + 0));
			// blocksToUpdate.add(new ChunkPosition(x + 1, y + 0, z + 0));
			// blocksToUpdate.add(new ChunkPosition(x + 0, y - 1, z + 0));
			// blocksToUpdate.add(new ChunkPosition(x + 0, y + 1, z + 0));
			// blocksToUpdate.add(new ChunkPosition(x + 0, y + 0, z - 1));
			// blocksToUpdate.add(new ChunkPosition(x + 0, y + 0, z + 1));
			for (int l = 0; l < blocksToUpdate.size(); ++l) {
				ChunkPosition chunkposition = (ChunkPosition) blocksToUpdate
						.get(l);
				world.notifyBlocksOfNeighborChange(chunkposition.x,
						chunkposition.y, chunkposition.z,
						world.getBlockId(x, y, z));
			}
		}
		
	}

}
