package WeepingAngels.Entity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import CountryGamer_Core.lib.CoreUtil;
import WeepingAngels.WeepingAngelsMod;
import WeepingAngels.Handlers.Player.ExtendedPlayer;
import WeepingAngels.lib.Reference;

public class EntityWeepingAngel extends EntityCreature {

	private boolean canSeeSkyAndDay;
	private int torchTimer;
	private int torchNextBreak;
	private boolean breakOnePerTick;
	private boolean didBreak;
	public boolean armMovement;
	public boolean aggressiveArmMovement;

	private float moveSpeed;
	private float maxSpeed = 50.0F, minSpeed = 0.3F;
	private final double closestPlayerRadius = 64D;
	private double distanceToSeen = 5D;
	private double minLight = 1.0;

	private boolean isQuantumLocked;
	private boolean isLockedByAngel;

	public EntityWeepingAngel(World world) {
		super(world);
		this.experienceValue = 50;
		this.isImmuneToFire = true;

		this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, true));

		this.isQuantumLocked = false;
		this.isLockedByAngel = false;
	}

	// Init & Attribute Methods
	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(16, Byte.valueOf((byte) 0)); // Angry
		this.dataWatcher.addObject(17, Byte.valueOf((byte) 0)); // ArmMovement
		this.dataWatcher.addObject(18, 0); // TorchTicks
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		// Max Health - default 20.0D - min 0.0D - max Double.MAX_VALUE
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
				.setAttribute(WeepingAngelsMod.maxHealth);
		// Follow Range - default 32.0D - min 0.0D - max 2048.0D
		// this.getEntityAttribute(SharedMonsterAttributes.followRange)
		// .setAttribute(32.0D);
		// Knockback Resistance - default 0.0D - min 0.0D - max 1.0D
		// this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance)
		// .setAttribute(0.0D);
		// Movement Speed - default 0.699D - min 0.0D - max Double.MAX_VALUE
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
				.setAttribute(this.minSpeed);
		// Attack Damage - default 2.0D - min 0.0D - max Doubt.MAX_VALUE
		this.getAttributeMap().func_111150_b(
				SharedMonsterAttributes.attackDamage);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage)
				.setAttribute(WeepingAngelsMod.attackStrength);

	}

	protected String getLivingSound() {
		return Reference.BASE_TEX + "stone";
	}

	protected String getHurtSound() {
		return Reference.BASE_TEX + "light";
	}

	protected String getDeathSound() {
		return Reference.BASE_TEX + "crumble";
	}

	// Update Methods
	@Override
	public void onLivingUpdate() {
		this.updateArmSwingProgress();
		float f = this.getBrightness(1.0F);

		if (f > 0.5F) {
			this.entityAge += 2;
		}

		super.onLivingUpdate();
	}

	@Override
	public void onUpdate() {
		// Kill angel if conditions are right
		if (!this.worldObj.isRemote && this.worldObj.difficultySetting == 0) {
			this.setDead();
		}

		// Set angel to slowest speed
		this.moveStrafing = (this.moveForward = 0.0F);
		this.moveSpeed = this.minSpeed;
		// this.setJumping(false);

		// Check for daytime and if the angel can see the sky
		if (this.worldObj.isDaytime()) {
			float f = getBrightness(1.0F);
			if (f > 0.5F
					&& this.worldObj.canBlockSeeTheSky(
							MathHelper.floor_double(this.posX),
							MathHelper.floor_double(this.posY),
							MathHelper.floor_double(this.posZ))
					&& this.rand.nextFloat() * 30F < (f - 0.4F) * 2.0F)
				this.canSeeSkyAndDay = true;
			else
				this.canSeeSkyAndDay = false;
		}

		if (this.worldObj.getFullBlockLightValue(
				MathHelper.floor_double(this.posX),
				MathHelper.floor_double(this.posY),
				MathHelper.floor_double(this.posZ)) > 1.0)
			this.isQuantumLocked = this.canBeSeenMulti();

		if (!this.isLockedByAngel) {
			// Find an entity to target
			EntityPlayer player = this.getClosestPlayer(); // find closest
			// player
			if (player != null) {
				this.entityToAttack = player; // set player to target
			}

			// Speed setting
			if (this.entityToAttack != null) // if angel has target
				this.moveSpeed = this.maxSpeed; // set speed to the max
			else
				this.moveSpeed = this.minSpeed; // set speed to the minimum
		}

		if (this.isQuantumLocked)

			// Check for Quantum Lock from players
			if (this.isQuantumLocked) {
				this.moveSpeed = 0.0F; // quantum lock angel
				if (!this.isLockedByAngel) {
					// If seen, try to destroy light sources
					int maxTorchTicks = 20 * 10;
					// zero check
					if (this.dataWatcher.getWatchableObjectInt(18) < 0) {
						this.dataWatcher.updateObject(18, 0);
					}
					if (this.getLightValue() > 1.0D && !this.canSeeSkyAndDay
							&& this.dataWatcher.getWatchableObjectInt(18) <= 0
							&& this.entityToAttack != null) {
						// if torch destroy worked
						if (this.findNearestTorch()) {
							// reset torchTicks
							this.dataWatcher.updateObject(18, maxTorchTicks);
						} else {
							// if (WeepingAngelsMod.DEBUG)
							// WeepingAngelsMod.log.info("No Torches Found");
						}
					}
					// torchTick countdown
					if (this.dataWatcher.getWatchableObjectInt(18) > 0) {
						this.dataWatcher.updateObject(18,
								this.dataWatcher.getWatchableObjectInt(18) - 1);
					}

					// if (WeepingAngelsMod.DEBUG) // Torch Tick to Console
					// WeepingAngelsMod.log.info("Torch Ticks: "
					// + this.dataWatcher.getWatchableObjectInt(18));
				}

			}

		// Teleportation
		/*
		 * if (this.entityToAttack != null && this.entityToAttack instanceof
		 * EntityPlayer && (!this.isQuantumLocked)) { if
		 * (this.getDistancetoEntityToAttack() > 3D) {
		 * worldObj.playSoundAtEntity(this, Reference.BASE_TEX + "stone",
		 * this.getSoundVolume(), 1.0F); } }
		 */

		// set the speed of the angel to the calcualted new speed
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
				.setAttribute(this.moveSpeed);

		// if any lava block is within radius r of angel, heal angel 0.5 heart
		int r = 10;
		double x = this.posX, y = this.posY, z = this.posZ;
		for (int i = (int) x - r; i < x + r; i++) {
			for (int k = (int) z - r; k < z + r; k++) {
				for (int j = (int) y - r; j < y + r; j++) {
					if (this.worldObj.getBlockId(i, j, k) == Block.lavaMoving.blockID
							|| this.worldObj.getBlockId(i, j, k) == Block.lavaStill.blockID) {
						this.heal(0.05F);
					}
				}
			}
		}

		// Visual arm and face render setting
		if (this.entityToAttack != null) {
			double d1 = this.entityToAttack.posX - this.posX;
			double d2 = this.entityToAttack.posY - this.posY;
			double d3 = this.entityToAttack.posZ - this.posZ;
			double distance = MathHelper.sqrt_double(d1 * d1 + d2 * d2 + d3
					* d3);
			double closeDis = 13.856D;
			double farDis = 20.785D;
			// if(!this.worldObj.isRemote)
			// if(WeepingAngelsMod.DEBUG) WeepingAngelsMod.log.info(
			// "Distance to: " + distance);
			if (distance >= farDis) {
				// if(!this.worldObj.isRemote)
				// if(WeepingAngelsMod.DEBUG) WeepingAngelsMod.log.info(
				// "Calm + Closed");
				this.dataWatcher.updateObject(16, Byte.valueOf((byte) 0));
				this.dataWatcher.updateObject(17, Byte.valueOf((byte) 0));
			} else {
				if (distance < farDis) {
					// if(!this.worldObj.isRemote)
					// if(WeepingAngelsMod.DEBUG) WeepingAngelsMod.log.info(
					// "Calm + Open");
					this.dataWatcher.updateObject(16, Byte.valueOf((byte) 0));
					this.dataWatcher.updateObject(17, Byte.valueOf((byte) 1));
					if (distance < closeDis) {
						// if(!this.worldObj.isRemote)
						// if(WeepingAngelsMod.DEBUG) WeepingAngelsMod.log.info(
						// "Angry + Open");
						this.dataWatcher.updateObject(16,
								Byte.valueOf((byte) 1));
						this.dataWatcher.updateObject(17,
								Byte.valueOf((byte) 2));
					}
				}
			}
		}

		super.onUpdate(); // run the extended classes onUpdate()
	}

	private boolean findNearestTorch() {
		int i = (int) this.posX;
		int j = (int) this.posY;
		int k = (int) this.posZ;
		int radius = 10;
		int maxRadius = 100;
		for (int i1 = i - radius; i1 < i + radius; i1++) {
			for (int k1 = k - radius; k1 < k + radius; k1++) {
				for (int j1 = j - (radius * 2); j1 < j + (radius / 2); j1++) {
					if (this.getDistance(i, j, k, i1, j1, k1) < (double) maxRadius) {
						int j3 = worldObj.getBlockId(i1, j1, k1);
						Block block = j3 > 0 ? Block.blocksList[j3] : null;
						if (block != null && block == Block.torchWood) {
							block.dropBlockAsItem(worldObj, i1, j1, k1, 1, 1);
							worldObj.setBlockToAir(i1, j1, k1);
							// worldObj.playSoundAtEntity(
							// this,
							// "weepingangels:light",
							// getSoundVolume(),
							// ((rand.nextFloat() - rand.nextFloat())
							// * 0.2F + 1.0F) * 1.8F);
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	// Attack Methods
	/**
	 * From EntityMob.class
	 */
	public boolean attackEntityAsMob(Entity par1Entity) {
		float f = (float) this.getEntityAttribute(
				SharedMonsterAttributes.attackDamage).getAttributeValue();

		boolean flag = par1Entity.attackEntityFrom(
				DamageSource.causeMobDamage(this), f);

		return flag;
	}

	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (source == null) {
			return false;
		}
		if (source.getSourceOfDamage() instanceof EntityPlayer) {
			if (!WeepingAngelsMod.pickOnly)
				super.attackEntityFrom(source, damage);
			else {
				EntityPlayer entityplayer = (EntityPlayer) source
						.getSourceOfDamage();
				ItemStack itemstack = entityplayer.inventory.getCurrentItem();
				if (worldObj.difficultySetting > 2) {
					if (itemstack != null
							&& (itemstack.itemID == Item.pickaxeDiamond.itemID || itemstack
									.canHarvestBlock(Block.obsidian))) {
						super.attackEntityFrom(source, damage);
					}
				} else if (itemstack != null
						&& (itemstack.itemID == Item.pickaxeDiamond.itemID
								|| itemstack.itemID == Item.pickaxeIron.itemID || (itemstack
								.canHarvestBlock(Block.oreDiamond) && (itemstack.itemID != Item.pickaxeGold.itemID)))) {
					super.attackEntityFrom(source, damage);
				}
			}
		}
		return false;
	}

	@Override
	protected void attackEntity(Entity entity, float f) {
		if (entity != null && (entity instanceof EntityPlayer)
				&& (!this.canBeSeenMulti())) {
			EntityPlayer entityPlayer = (EntityPlayer) entity;
			if (!entityPlayer.capabilities.isCreativeMode
					&& this.getDistancetoEntityToAttack() <= 2) {
				if (WeepingAngelsMod.canPoison
						&& Math.random() * 100 <= WeepingAngelsMod.poisonChance / 100) {
					ExtendedPlayer playerProps = ExtendedPlayer
							.get(entityPlayer);
					playerProps.setConvert(1);
					playerProps.setAngelHealth(0.0F);
					playerProps
							.setTicksTillAngelHeal(ExtendedPlayer.ticksPerHalfHeart);
					if (WeepingAngelsMod.DEBUG)
						WeepingAngelsMod.log.info("Infected Player");
				} else if (WeepingAngelsMod.canTeleport
						&& Math.random() * 100 <= WeepingAngelsMod.teleportChance / 100) {
					CoreUtil.teleportPlayer(entityPlayer, 0,
							WeepingAngelsMod.teleportRangeMax, true, true);
					this.worldObj.playSoundAtEntity(entityPlayer,
							Reference.BASE_TEX + "teleport_activate", 1.0F,
							1.0F);
					entity = null;
					if (WeepingAngelsMod.DEBUG)
						WeepingAngelsMod.log.info("Teleported Player");
				} else {
					this.attackEntityAsMob(entity);
					if (WeepingAngelsMod.DEBUG)
						WeepingAngelsMod.log.info("Attacked Player");
				}

			}

		}
	}

	// Get Methods
	public float getBlockPathWeight(int par1, int par2, int par3) {
		return 0.5F - this.worldObj.getLightBrightness(par1, par2, par3);
	}

	public boolean getCanSpawnHere() {
		if (WeepingAngelsMod.spawnRate == 0)
			return false;

		boolean validYLevel = false;
		int x = MathHelper.floor_double(this.posX);
		int j2 = MathHelper.floor_double(this.boundingBox.minY);
		int y = MathHelper.floor_double(this.posY + j2);
		int z = MathHelper.floor_double(this.posZ);

		if (y <= WeepingAngelsMod.maxSpawnHeight)
			validYLevel = true;

		boolean entityMobCanSpawn = this.worldObj.difficultySetting > 0
				&& this.isValidLightLevel() && super.getCanSpawnHere();

		return entityMobCanSpawn && validYLevel;
	}

	private double getLightValue() {
		int i = MathHelper.floor_double(this.posX);
		int j = MathHelper.floor_double(this.boundingBox.minY);
		int k = MathHelper.floor_double(this.posZ);
		return this.worldObj.getBlockLightValue(i, j, k);
	}

	public int angryState() {
		return this.dataWatcher.getWatchableObjectByte(16);
	}

	public int armState() {
		return this.dataWatcher.getWatchableObjectByte(17);
	}

	private EntityPlayer getClosestPlayer() {
		List list = worldObj.getEntitiesWithinAABB(EntityPlayer.class,
				boundingBox.expand(this.closestPlayerRadius, 20D,
						this.closestPlayerRadius));
		if (!list.isEmpty())
			return (EntityPlayer) list.get(0);
		return null;
	}

	protected void dropRareDrop(int par1) {
		this.dropItem(WeepingAngelsMod.chronon.itemID, 1);
	}

	// Boolean Methods
	/**
	 * Checks to make sure the light is not too bright where the mob is spawning
	 * Taken from EntityMob
	 */
	protected boolean isValidLightLevel() {
		int i = MathHelper.floor_double(this.posX);
		int j = MathHelper.floor_double(this.boundingBox.minY);
		int k = MathHelper.floor_double(this.posZ);

		if (this.worldObj.getSavedLightValue(EnumSkyBlock.Sky, i, j, k) > this.rand
				.nextInt(32)) {
			return false;
		} else {
			int l = this.worldObj.getBlockLightValue(i, j, k);

			if (this.worldObj.isThundering()) {
				int i1 = this.worldObj.skylightSubtracted;
				this.worldObj.skylightSubtracted = 10;
				l = this.worldObj.getBlockLightValue(i, j, k);
				this.worldObj.skylightSubtracted = i1;
			}

			return l <= this.rand.nextInt(8);
		}
	}

	private boolean isInFieldOfVision(EntityLivingBase entity) {
		if (entity == null)
			return false;

		if (entity instanceof EntityPlayer) {
			Vec3 vec3 = entity.getLookVec();
			Vec3 vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool(
					this.posX - entity.posX,
					this.boundingBox.minY + (double) (this.height)
							- (entity.posY + (double) entity.getEyeHeight()),
					this.posZ - entity.posZ);
			double d0 = vec31.lengthVector();
			vec31 = vec31.normalize();
			double d1 = vec3.dotProduct(vec31);
			return d1 > ((1.0D - 0.025D) / d0) ? entity.canEntityBeSeen(this)
					: false;
		} else if (entity instanceof EntityWeepingAngel) {

		}
		return false;
	}

	private boolean canBeSeenMulti() {
		List list = worldObj.getEntitiesWithinAABB(EntityPlayer.class,
				boundingBox.expand(this.closestPlayerRadius, 20D,
						this.closestPlayerRadius));
		int playersWatching = 0;
		for (int j = 0; j < list.size(); j++) {
			EntityPlayer player = (EntityPlayer) list.get(j);
			if (this.isInFieldOfVision(player)) {
				playersWatching++;
			}
		}
		if (playersWatching > 0)
			return true;
		return false;
	}

	private boolean canBeSeenByAngel() {
		List list = worldObj.getEntitiesWithinAABB(EntityWeepingAngel.class,
				boundingBox.expand(this.closestPlayerRadius, 20D,
						this.closestPlayerRadius));
		int angelsWatching = 0;
		for (int j = 0; j < list.size(); j++) {
			EntityWeepingAngel angel = (EntityWeepingAngel) list.get(j);
			boolean same = angel.posX == this.posX && angel.posY == this.posY
					&& angel.posZ == this.posZ;
			if (!same && this.isEntityFacing(angel)) {
				if (this.isInFieldOfVision(angel)) {
					angelsWatching++;
				}
			}
		}
		if (angelsWatching > 0)
			return true;
		return false;
	}

	private boolean isEntityFacing(EntityLivingBase entity) {
		int directionOfEntity = MathHelper
				.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		// 0 = south (+Z)
		// 1 = west (-X)
		// 2 = north (-Z)
		// 3 = east (+X)
		double thisZ = this.posZ;
		double thisX = this.posX;
		double entZ = entity.posZ;
		double entX = entity.posX;
		if (directionOfEntity == 0 && thisZ > entZ)
			return true;
		if (directionOfEntity == 1 && thisX < entX)
			return true;
		if (directionOfEntity == 2 && thisZ < entZ)
			return true;
		if (directionOfEntity == 3 && thisX > entX)
			return true;

		return false;
	}

	// Utility Methods
	public double getDistance(int i, int j, int k, int l, int i1, int j1) {
		int k1 = l - i;
		int l1 = i1 - j;
		int i2 = j1 - k;
		return Math.sqrt(k1 * k1 + l1 * l1 + i2 * i2);
	}

	public double getDistancetoEntityToAttack() {
		if (entityToAttack instanceof EntityPlayer) {
			double d = entityToAttack.posX - posX;
			double d2 = entityToAttack.posY - posY;
			double d4 = entityToAttack.posZ - posZ;
			return (double) MathHelper.sqrt_double(d * d + d2 * d2 + d4 * d4);
		}
		EntityPlayer entityplayer = worldObj.getClosestPlayerToEntity(this,
				this.closestPlayerRadius);
		if (entityplayer != null) {
			double d1 = entityplayer.posX - posX;
			double d3 = entityplayer.posY - posY;
			double d5 = entityplayer.posZ - posZ;
			return (double) MathHelper.sqrt_double(d1 * d1 + d3 * d3 + d5 * d5);
		} else {
			return 40000D;
		}
	}

}