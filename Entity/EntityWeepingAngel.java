package com.countrygamer.weepingangels.Entity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

import com.countrygamer.core.lib.CoreUtil;
import com.countrygamer.weepingangels.WeepingAngelsMod;
import com.countrygamer.weepingangels.Handlers.Player.ExtendedPlayer;
import com.countrygamer.weepingangels.lib.Reference;
import com.countrygamer.weepingangels.lib.Util;

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
				.setBaseValue(WeepingAngelsMod.maxHealth);
		// Follow Range - default 32.0D - min 0.0D - max 2048.0D
		// this.getEntityAttribute(SharedMonsterAttributes.followRange)
		// .setAttribute(32.0D);
		// Knockback Resistance - default 0.0D - min 0.0D - max 1.0D
		// this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance)
		// .setAttribute(0.0D);
		// Movement Speed - default 0.699D - min 0.0D - max Double.MAX_VALUE
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
				.setBaseValue(this.minSpeed);
		// Attack Damage - default 2.0D - min 0.0D - max Doubt.MAX_VALUE
		this.getAttributeMap().registerAttribute(
				SharedMonsterAttributes.attackDamage);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage)
				.setBaseValue(WeepingAngelsMod.attackStrength);

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

	protected boolean isAIEnabled() {
		return false;
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
		if (!this.worldObj.isRemote
				&& this.worldObj.difficultySetting.getDifficultyId() <= EnumDifficulty.PEACEFUL
						.getDifficultyId()) {
			this.setDead();
		}

		// Set angel to slowest speed
		this.moveStrafing = (this.moveForward = 0.0F);
		this.moveSpeed = this.minSpeed;
		this.isJumping = false;
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
			this.isQuantumLocked = Util.canBeSeenMulti(this.worldObj,
					this.boundingBox, this.closestPlayerRadius, this);

		// if (this.canBeSeenByAngel()) {
		// WeepingAngelsMod.log.info("Seen by angel");
		// this.isLockedByAngel = true;
		// this.isQuantumLocked = true;
		// }

		if (this.entityToAttack == null) { // Find an entity to target
			// find closest player
			EntityPlayer player = (EntityPlayer) this
					.getClosestPlayer(EntityPlayer.class);
			if (player != null) {
				this.entityToAttack = player; // set player to target
			}
		}

		// Speed setting
		if (this.entityToAttack != null) // if angel has target
			this.moveSpeed = this.maxSpeed; // set speed to the max
		else
			this.moveSpeed = this.minSpeed; // set speed to the minimum

		// Check for Quantum Lock from players
		if (this.isQuantumLocked) {
			this.moveSpeed = 0.0F; // quantum lock angel
			if (this.entityToAttack instanceof EntityPlayer) {
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
		if (this.entityToAttack != null && !this.isQuantumLocked)
			this.faceEntity(this.entityToAttack, 100F, 100F);

		// set the speed of the angel to the calcualted new speed
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
				.setBaseValue(this.moveSpeed);

		// if any lava block is within radius r of angel, heal angel 0.5 heart
		int r = 10;
		double x = this.posX, y = this.posY, z = this.posZ;
		for (int i = (int) x - r; i < x + r; i++) {
			for (int k = (int) z - r; k < z + r; k++) {
				for (int j = (int) y - r; j < y + r; j++) {
					if (this.worldObj.getBlock(i, j, k) == Blocks.flowing_lava
							|| this.worldObj.getBlock(i, j, k) == Blocks.lava) {
						this.heal(0.05F);
					}
				}
			}
		}

		// Visual arm and face render setting
		this.renderMovement();

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
						Block block = worldObj.getBlock(i1, j1, k1);
						if (block != null && block == Blocks.torch) {
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

	private void renderMovement() {
		if (!this.isQuantumLocked) {
			if (this.getAngelsNear().size() > 0) {
				this.dataWatcher.updateObject(17, (byte) 0);
			}
			if (this.entityToAttack == null) {
				return;
			} else if (this.entityToAttack instanceof EntityPlayer) {
				double distance = this.getDistancetoEntityToAttack();
				if (distance <= 5.0D) {
					this.dataWatcher.updateObject(16, (byte) 1);
					this.dataWatcher.updateObject(17, (byte) 2);
				} else {
					this.dataWatcher.updateObject(16, (byte) 0);
					this.dataWatcher.updateObject(17, (byte) 0);
					if (this.rand.nextInt(100) > 80) {
						this.dataWatcher.updateObject(17, (byte) 1);
					}
				}
			}

		}
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
			boolean canHurt = false;
			EntityPlayer entityplayer = (EntityPlayer) source
					.getSourceOfDamage();
			ItemStack itemStack = entityplayer.inventory.getCurrentItem();

			if (itemStack != null
					&& itemStack.getItem() == WeepingAngelsMod.sonicScrew) {
				super.attackEntityFrom(source, 0.25F * this.getMaxHealth());
				return true;
			}

			if (WeepingAngelsMod.pickOnly) {
				if (itemStack == null)
					canHurt = false;
				else {
					canHurt = itemStack.getItem()
							.canHarvestBlock(Blocks.obsidian, itemStack);
					if (this.worldObj.difficultySetting.getDifficultyId() >= EnumDifficulty.NORMAL
							.getDifficultyId())
						canHurt = canHurt
								|| itemStack.getItem().func_150897_b(
										Blocks.gold_ore);
				}
			} else
				canHurt = true;

			if (canHurt) {
				super.attackEntityFrom(source, damage);
				return true;
			}
		}
		return false;
	}

	@Override
	protected void attackEntity(Entity entity, float f) {
		if (entity != null
				&& (!Util.canBeSeenMulti(this.worldObj, this.boundingBox,
						this.closestPlayerRadius, this))) {
			if (entity instanceof EntityPlayer) {
				EntityPlayer entityPlayer = (EntityPlayer) entity;
				if (!entityPlayer.capabilities.isCreativeMode
						&& this.getDistancetoEntityToAttack() <= 2) {
					if (WeepingAngelsMod.canPoison
							&& this.rand.nextInt(100) < WeepingAngelsMod.poisonChance) {
						ExtendedPlayer playerProps = ExtendedPlayer
								.get(entityPlayer);
						playerProps.setConvert(1);
						playerProps.setAngelHealth(0.0F);
						playerProps
								.setTicksTillAngelHeal(ExtendedPlayer.ticksPerHalfHeart);
						// if (CG_Core.DEBUG)
						// WeepingAngelsMod.log.info("Infected Player");
					} else if (WeepingAngelsMod.canTeleport
							&& this.rand.nextInt(100) < WeepingAngelsMod.teleportChance) {
						CoreUtil.teleportPlayer(entityPlayer, 0,
								WeepingAngelsMod.teleportRangeMax,
								entityPlayer.posX, entityPlayer.posZ, true,
								true);
						this.worldObj.playSoundAtEntity(entityPlayer,
								Reference.BASE_TEX + "teleport_activate", 1.0F,
								1.0F);
						entity = null;
						// if (CG_Core.DEBUG)
						// WeepingAngelsMod.log.info("Teleported Player");
					} else {
						this.attackEntityAsMob(entity);
						// if (CG_Core.DEBUG)
						// WeepingAngelsMod.log.info("Attacked Player");
					}

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

		boolean entityMobCanSpawn = this.worldObj.difficultySetting != EnumDifficulty.EASY
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

	private Entity getClosestPlayer(Class entity) {
		List list = worldObj.getEntitiesWithinAABB(entity, boundingBox.expand(
				this.closestPlayerRadius, 20D, this.closestPlayerRadius));
		if (!list.isEmpty())
			return (Entity) list.get(0);
		return null;
	}

	protected void dropRareDrop(int par1) {
		if (WeepingAngelsMod.addonVortex)
			this.dropItem(WeepingAngelsMod.chronon, 1);
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

	private List getAngelsNear() {
		return worldObj.getEntitiesWithinAABB(EntityWeepingAngel.class,
				boundingBox.expand(20D, 20D, 20D));
	}

	private boolean canBeSeenByAngel() {
		List list = this.getAngelsNear();
		int angelsWatching = 0;
		// WeepingAngelsMod.log.info("" + list.size());
		for (int j = 0; j < list.size(); j++) {
			EntityWeepingAngel angel = (EntityWeepingAngel) list.get(j);
			boolean same = this.posX == angel.posX && this.posY == angel.posY
					&& this.posZ == angel.posZ;
			same = angel == this;
			if (!same && angel.canSeeAngel(this)) {
				angelsWatching++;
			}
		}
		return angelsWatching > 0;
	}

	private boolean canSeeAngel(EntityWeepingAngel entity) {
		if (this.worldObj.getFullBlockLightValue(
				MathHelper.floor_double(this.posX),
				MathHelper.floor_double(this.posY),
				MathHelper.floor_double(this.posZ)) <= 1.0) {
			return false;
		} else if (this.dataWatcher.getWatchableObjectByte(17) >= 2) {
			return Util.isInFieldOfVision(this.worldObj, entity, this);
		} else
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
