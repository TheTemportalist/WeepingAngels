package WeepingAngels.World;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

public class WorldGenerator implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		switch (world.provider.dimensionId) {
		case 0:
			this.genOverworld(world, random, chunkX * 16, chunkZ * 16);
			break;
		default:
			break;
		}
	}

	private void genOverworld(World world, Random rand, int blockX, int blockZ) {
		int xCoord = blockX + rand.nextInt(16);
		int zCoord = blockZ + rand.nextInt(16);
		int yCoord = rand.nextInt(80);
		//(new WorldGenAngelDungeon()).generate(world, rand, xCoord, yCoord,
		//		zCoord);
	}

}
