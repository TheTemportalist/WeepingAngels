package WeepingAngels.lib;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import cpw.mods.fml.common.FMLLog;

public class Util {

	public static void teleportPlayer(World world, EntityPlayer player,
			int minimumRange, int maximumRange, boolean fallDamage,
			boolean particles) {
		double[] newPos = Util.teleportBase(world, player, minimumRange,
				maximumRange);
		newPos[1] -= 2;
		if (!fallDamage)
			player.fallDistance = 0.0F;
		// Set the location of the player, on the final position.

		player.setLocationAndAngles(newPos[0], newPos[1], newPos[2],
				player.rotationYaw, player.rotationPitch);
		//FMLLog.info("Succesfully teleported to: "+(int)player.posX+" "+(int)player.posY+" "+(int)player.posZ);
		Random rand = new Random();
		double d3 = newPos[0];
		double d4 = newPos[1];
		double d5 = newPos[2];
		int l = 128;
		if (particles) {
			for (int j1 = 0; j1 < l; j1++) {
				double d6 = (double) j1 / ((double) l - 1.0D);
				float f = (rand.nextFloat() - 0.5F) * 0.2F;
				float f1 = (rand.nextFloat() - 0.5F) * 0.2F;
				float f2 = (rand.nextFloat() - 0.5F) * 0.2F;
				double d7 = d3 + (player.posX - d3) * d6
						+ (rand.nextDouble() - 0.5D) * (double) player.width
						* 2D;
				double d8 = d4 + (player.posY - d4) * d6 + rand.nextDouble()
						* (double) player.height;
				double d9 = d5 + (player.posZ - d5) * d6
						+ (rand.nextDouble() - 0.5D) * (double) player.width
						* 2D;
				world.spawnParticle("portal", d7, d8 - 1, d9, f, f1, f2);
			}
		}
	}

	private static double[] teleportBase(World world, EntityPlayer player,
			int minimumRange, int maximumRange) {
		Random rand = new Random();

		int rangeDifference = 2 * (maximumRange - minimumRange);
		int offsetX = rand.nextInt(rangeDifference) - rangeDifference / 2
				+ minimumRange;
		int offsetZ = rand.nextInt(rangeDifference) - rangeDifference / 2
				+ minimumRange;

		// Center the values on a block, to make the boundingbox
		// calculations match less.
		double newX = MathHelper.floor_double(player.posX) + offsetX + 0.5;
		double newY = rand.nextInt(128);
		double newZ = MathHelper.floor_double(player.posZ) + offsetZ + 0.5;

		double bbMinX = newX - player.width / 2.0;
		double bbMinY = newY - player.yOffset + player.ySize;
		double bbMinZ = newZ - player.width / 2.0;
		double bbMaxX = newX + player.width / 2.0;
		double bbMaxY = newY - player.yOffset + player.ySize + player.height;
		double bbMaxZ = newZ + player.width / 2.0;

		// FMLLog.info("Teleporting from: "+(int)player.posX+" "+(int)player.posY+" "+(int)player.posZ);
		// FMLLog.info("Teleporting with offsets: "+offsetX+" "+newY+" "+offsetZ);
		// FMLLog.info("Starting BB Bounds: "+bbMinX+" "+bbMinY+" "+bbMinZ+" "+bbMaxX+" "+bbMaxY+" "+bbMaxZ);

		// Use a testing boundingBox, so we don't have to move the player
		// around to test if it is a valid location
		AxisAlignedBB boundingBox = AxisAlignedBB.getBoundingBox(bbMinX,
				bbMinY, bbMinZ, bbMaxX, bbMaxY, bbMaxZ);

		// Make sure you are trying to teleport to a loaded chunk.
		Chunk teleportChunk = world.getChunkFromBlockCoords((int) newX,
				(int) newZ);
		if (!teleportChunk.isChunkLoaded) {
			world.getChunkProvider().loadChunk(teleportChunk.xPosition,
					teleportChunk.zPosition);
		}

		// Move up, until nothing intersects the player anymore
		while (newY > 0
				&& newY < 128
				&& !world.getCollidingBoundingBoxes(player, boundingBox)
						.isEmpty()) {
			++newY;

			bbMinY = newY - player.yOffset + player.ySize;
			bbMaxY = newY - player.yOffset + player.ySize + player.height;

			boundingBox.setBounds(bbMinX, bbMinY, bbMinZ, bbMaxX, bbMaxY,
					bbMaxZ);

			// FMLLog.info("Failed to teleport, retrying at height: "+(int)newY);
		}

		// If we could place it, could we have placed it lower? To prevent
		// teleports really high up.
		do {
			--newY;

			bbMinY = newY - player.yOffset + player.ySize;
			bbMaxY = newY - player.yOffset + player.ySize + player.height;

			boundingBox.setBounds(bbMinX, bbMinY, bbMinZ, bbMaxX, bbMaxY,
					bbMaxZ);

			// FMLLog.info("Trying a lower teleport at height: "+(int)newY);
		} while (newY > 0
				&& newY < 128
				&& world.getCollidingBoundingBoxes(player, boundingBox)
						.isEmpty());
		// Set Y one higher, as the last lower placing test failed.
		++newY;

		// Check for placement in lava
		// NOTE: This can potentially hang the game indefinitely, due to
		// random recursion
		// However this situation is highly unlikelely
		// My advice: Dont encounter Weeping Angels in seas of lava
		// NOTE: This can theoretically still teleport you to a block of
		// lava with air underneath, but gladly lava spreads ;)
		int blockIdUnder = world.getBlockId(MathHelper.floor_double(newX),
				MathHelper.floor_double(newY), MathHelper.floor_double(newZ));
		int blockIdAt = world.getBlockId(MathHelper.floor_double(newX),
				MathHelper.floor_double(newY + 1),
				MathHelper.floor_double(newZ));
		int blockIdAbove = world.getBlockId(MathHelper.floor_double(newX),
				MathHelper.floor_double(newY + 2),
				MathHelper.floor_double(newZ));
		ArrayList<Integer> blocks = new ArrayList<Integer>();
		blocks.add(Block.lavaStill.blockID);
		blocks.add(Block.lavaMoving.blockID);
		blocks.add(Block.waterStill.blockID);
		blocks.add(Block.waterMoving.blockID);
		if (blocks.contains(blockIdUnder) || blocks.contains(blockIdAt)
				|| blocks.contains(blockIdAbove)) {
			return Util.teleportBase(world, player, minimumRange, maximumRange);
		}
		// if (world.getBlockId((int)newX, (int)newY, (int)newZ) == 0 ||
		// world.getBlockId((int)newX, (int)newY + 1, (int)newZ) != 0) {
		// return Util.teleportBase(world, player);
		// }
		return new double[] { newX, newY, newZ };
	}

}
