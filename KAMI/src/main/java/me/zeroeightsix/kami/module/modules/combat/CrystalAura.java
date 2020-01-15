package me.zeroeightsix.kami.module.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.event.events.RenderEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.BlockInteractionHelper;
import me.zeroeightsix.kami.util.Friends;
import me.zeroeightsix.kami.util.GeometryMasks;
import me.zeroeightsix.kami.util.KamiTessellator;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static me.zeroeightsix.kami.util.EntityUtil.calculateLookAt;

// TODO: note to self - this is a really big mess and needs a full hands on!

/**
 * Created by hub
 * Updated 27 November 2019 by 3vt
 * Updated by Darki on 08/01/2020
 */
@Module.Info(name = "CrystalAura", category = Module.Category.COMBAT)
public class CrystalAura extends Module {

    private static boolean togglePitch = false;
    private static boolean isSpoofingAngles;
    private static double yaw;
    private static double pitch;

    private Setting<Boolean> place = register(Settings.b("Place", true));
    private Setting<Boolean> explode = register(Settings.b("Explode", true));
    private Setting<Boolean> autoSwitch = register(Settings.b("Auto Switch", true));
    private Setting<Boolean> antiWeakness = register(Settings.b("Anti Weakness", true));
    private Setting<Integer> hitTickDelay = register(Settings.integerBuilder("Hit Delay").withMinimum(0).withValue(4).withMaximum(20).build());
    public Setting<Double> hitRange = register(Settings.doubleBuilder("Hit Range").withMinimum(0.0).withValue(5.5).build());
    private Setting<Double> placeRange = register(Settings.doubleBuilder("Place Range").withMinimum(0.0).withValue(3.5).build());
    private Setting<Double> minDamage = register(Settings.doubleBuilder("Min Damage").withMinimum(0.0).withValue(2.0).withMaximum(20.0).build());
    private Setting<Boolean> spoofRotations = register(Settings.b("Spoof Rotations", false));
    private Setting<Boolean> rayTraceHit = register(Settings.b("RayTraceHit", false));
    private Setting<RenderMode> renderMode = register(Settings.e("Render Mode", RenderMode.UP));
    private Setting<Integer> red = register(Settings.integerBuilder("Red").withMinimum(0).withValue(104).withMaximum(255).build());
    private Setting<Integer> green = register(Settings.integerBuilder("Green").withMinimum(0).withValue(12).withMaximum(255).build());
    private Setting<Integer> blue = register(Settings.integerBuilder("Blue").withMinimum(0).withValue(35).withMaximum(255).build());
    private Setting<Integer> alpha = register(Settings.integerBuilder("Alpha").withMinimum(0).withValue(169).withMaximum(255).build());
    private Setting<Boolean> announceUsage = register(Settings.b("Announce Usage", true));

    public static CrystalAura instace = new CrystalAura();

    public CrystalAura() {
        instace = this;
    }

    private BlockPos renderBlock;
    private EntityPlayer target;

    // we need this cooldown to not place from old hotbar slot, before we have switched to crystals.
    // otherwise we will attempt to click with whatever was in the last slot.
    private boolean switchCooldown = false;
    private boolean isAttacking = false;
    private int oldSlot = -1;
    private int newSlot;

    private int hitDelayCounter;

    @EventHandler
    private Listener<PacketEvent.Send> packetListener = new Listener<>(event -> {
        if (!spoofRotations.getValue()) {
            return;
        }
        Packet packet = event.getPacket();
        if (packet instanceof CPacketPlayer) {
            if (isSpoofingAngles) {
                ((CPacketPlayer) packet).yaw = (float) yaw;
                ((CPacketPlayer) packet).pitch = (float) pitch;
            }
        }
    });

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 6.0F * 2.0F;
        double distancedsize = entity.getDistance(posX, posY, posZ) / (double) doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        double v = (1.0D - distancedsize) * blockDensity;
        float damage = (float) ((int) ((v * v + v) / 2.0D * 7.0D * (double) doubleExplosionSize + 1.0D));
        double finald = 1;
        if (entity instanceof EntityLivingBase) {
            // we pass null as the exploder here
            //noinspection ConstantConditions
            finald = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(mc.world, null, posX, posY, posZ, 6F, false, true));
        }
        return (float) finald;
    }

    private static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

            int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            float f = MathHelper.clamp(k, 0.0F, 20.0F);
            damage = damage * (1.0F - f / 25.0F);

            if (entity.isPotionActive(MobEffects.RESISTANCE)) {
                damage = damage - (damage / 4);
            }

            return damage;
        }
        damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
    }

    private static float getDamageMultiplied(float damage) {
        int diff = mc.world.getDifficulty().getId();
        return damage * (diff == 0 ? 0 : (diff == 2 ? 1 : (diff == 1 ? 0.5f : 1.5f)));
    }

    //this modifies packets being sent so no extra ones are made. NCP used to flag with "too many packets"
    private static void setYawAndPitch(float yaw1, float pitch1) {
        yaw = yaw1;
        pitch = pitch1;
        isSpoofingAngles = true;
    }

    private static void resetRotation() {
        if (isSpoofingAngles) {
            yaw = mc.player.rotationYaw;
            pitch = mc.player.rotationPitch;
            isSpoofingAngles = false;
        }
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        if (renderBlock != null && !renderMode.getValue().equals(RenderMode.NONE)) {
            drawBlock(renderBlock, red.getValue(), green.getValue(), blue.getValue());
        }
    }

    private void drawBlock(BlockPos blockPos, int r, int g, int b) {
        Color color = new Color(r, g, b, alpha.getValue());
        KamiTessellator.prepare(GL11.GL_QUADS);
        if (renderMode.getValue().equals(RenderMode.UP)) {
            KamiTessellator.drawBox(blockPos, color.getRGB(), GeometryMasks.Quad.UP);
        } else if (renderMode.getValue().equals(RenderMode.BLOCK)) {
            KamiTessellator.drawBox(blockPos, color.getRGB(), GeometryMasks.Quad.ALL);
        }
        KamiTessellator.release();
    }

    @Override
    public void onUpdate() {

        if (mc.player == null) {
            return;
        }

        // TODO: if we place before we hit, can we do it in 1 tick? instead of exploding closest crystal, explode crystal that was placed last tick.
        // if last tick crystal is null, calculate optimal crystal to hit.
        EntityEnderCrystal crystal = mc.world.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityEnderCrystal)
                .map(entity -> (EntityEnderCrystal) entity)
                .min(Comparator.comparing(c -> mc.player.getDistance(c)))
                .orElse(null);

        if (explode.getValue() && crystal != null && mc.player.getDistance(crystal) <= hitRange.getValue() && rayTraceHitCheck(crystal)) {

            //tick delay to stop ncp from flagging "hitting too fast"
            if (hitDelayCounter >= hitTickDelay.getValue()) {
                hitDelayCounter = 0;
            } else {
                hitDelayCounter++;
                return;
            }

            if (antiWeakness.getValue() && mc.player.isPotionActive(MobEffects.WEAKNESS)) {
                if (!isAttacking) {
                    // save initial player hand
                    oldSlot = mc.player.inventory.currentItem;
                    isAttacking = true;
                }
                // search for sword and tools in hotbar
                newSlot = -1;
                for (int i = 0; i < 9; i++) {
                    ItemStack stack = mc.player.inventory.getStackInSlot(i);
                    if (stack == ItemStack.EMPTY) {
                        continue;
                    }
                    if ((stack.getItem() instanceof ItemSword)) {
                        newSlot = i;
                        break;
                    }
                    if ((stack.getItem() instanceof ItemTool)) {
                        newSlot = i;
                        break;
                    }
                }
                // check if any swords or tools were found
                if (newSlot != -1) {
                    mc.player.inventory.currentItem = newSlot;
                    switchCooldown = true;
                }
            }

            lookAtPacket(crystal.posX, crystal.posY, crystal.posZ, mc.player);
            mc.playerController.attackEntity(mc.player, crystal);
            mc.player.swingArm(EnumHand.MAIN_HAND);

            return;

        } else {

            resetRotation();
            if (oldSlot != -1) {
                mc.player.inventory.currentItem = oldSlot;
                oldSlot = -1;
            }
            isAttacking = false;

        }

        int crystalSlot = mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL ? mc.player.inventory.currentItem : -1;
        if (crystalSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (mc.player.inventory.getStackInSlot(l).getItem() == Items.END_CRYSTAL) {
                    crystalSlot = l;
                    break;
                }
            }
        }

        boolean offhand = false;
        if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            offhand = true;
        } else if (crystalSlot == -1) {
            return;
        }

        List<Entity> entities = mc.world.playerEntities.stream().filter(entityPlayer -> !Friends.isFriend(entityPlayer.getName())).sorted(
                (Entity entity1, Entity entity2) -> Float.compare(mc.player.getDistance(entity1), mc.player.getDistance(entity2))
        ).collect(Collectors.toList());

        List<BlockPos> blocks = findCrystalBlocks();

        BlockPos targetBlock = null;
        double targetBlockDamage = 0;

        target = null;

        for (Entity entity : entities) {

            // ignore self
            if (entity == mc.player) {
                continue;
            }

            // players are our only concern
            if (!(entity instanceof EntityPlayer)) {
                continue;
            }

            EntityPlayer testTarget = (EntityPlayer) entity;

            // ignore dead
            if (testTarget.isDead || testTarget.getHealth() <= 0) {
                continue;
            }

            for (BlockPos blockPos : blocks) {

                // TODO: make this a setting, also find better way of doing this!
                // ignore out of range
                if (testTarget.getDistanceSq(blockPos) >= 169) {
                    continue; // If this block is further than 13 (3.6^2, less calc) blocks, ignore it. It'll take no or very little damage
                }

                double targetDamage = calculateDamage(blockPos.x + .5, blockPos.y + 1, blockPos.z + .5, testTarget);
                double selfDamage = calculateDamage(blockPos.x + .5, blockPos.y + 1, blockPos.z + .5, mc.player);
                float healthTarget = testTarget.getHealth() + testTarget.getAbsorptionAmount();
                float healthSelf = mc.player.getHealth() + mc.player.getAbsorptionAmount();

                // skip below mindamage
                if (targetDamage < minDamage.getValue()) {
                    continue;
                }

                // we dont wanna suicide ever
                if (selfDamage >= healthSelf - 0.5) {
                    continue;
                }

                // if we get more damage then the target and cant kill the target, skip
                if (selfDamage > targetDamage && targetDamage < healthTarget) {
                    continue;
                }

                // use best position
                if (targetDamage > targetBlockDamage) {
                    targetBlock = blockPos;
                    targetBlockDamage = targetDamage;
                    target = testTarget;
                }

            }

            // we do this to just calculate damage for the first found, valid entity
            // otherwise this will be laggy on slow machines
            if (target != null) {
                break;
            }

        }

        if (target == null) {
            renderBlock = null;
            resetRotation();
            return;
        }

        renderBlock = targetBlock;

        if (ModuleManager.getModuleByName("AutoGG").isEnabled()) {
            AutoGG autoGG = (AutoGG) ModuleManager.getModuleByName("AutoGG");
            autoGG.addTargetedPlayer(target.getName());
        }

        if (place.getValue()) {

            if (!offhand && mc.player.inventory.currentItem != crystalSlot) {

                if (autoSwitch.getValue()) {
                    mc.player.inventory.currentItem = crystalSlot;
                    resetRotation();
                    switchCooldown = true;
                }

                return;

            }

            lookAtPacket(targetBlock.x + .5, targetBlock.y - .5, targetBlock.z + .5, mc.player);
            RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(targetBlock.x + .5, targetBlock.y - .5d, targetBlock.z + .5));
            EnumFacing f;

            if (result == null || result.sideHit == null) {
                f = EnumFacing.UP;
            } else {
                f = result.sideHit;
            }

            // return after we did an autoswitch
            if (switchCooldown) {
                switchCooldown = false;
                return;
            }

            //mc.playerController.processRightClickBlock(mc.player, mc.world, q, f, new Vec3d(0, 0, 0), EnumHand.MAIN_HAND);
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(targetBlock, f, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));

        }

        //this sends a constant packet flow for default packets
        if (spoofRotations.getValue() && isSpoofingAngles) {
            if (togglePitch) {
                mc.player.rotationPitch += 0.0004;
                togglePitch = false;
            } else {
                mc.player.rotationPitch -= 0.0004;
                togglePitch = true;
            }
        }

    }

    private boolean rayTraceHitCheck(EntityEnderCrystal crystal) {
        if (!rayTraceHit.getValue()) {
            return true;
        }
        return mc.player.canEntityBeSeen(crystal);
    }

    private void lookAtPacket(double px, double py, double pz, EntityPlayer me) {
        double[] v = calculateLookAt(px, py, pz, me);
        setYawAndPitch((float) v[0], (float) v[1]);
    }

    // TODO: this is a mess, need cleanup
    // TODO: could use raytracing check option
    private boolean canPlaceCrystal(BlockPos blockPos) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        return (mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK
                || mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN)
                && mc.world.getBlockState(boost).getBlock() == Blocks.AIR
                && mc.world.getBlockState(boost2).getBlock() == Blocks.AIR
                && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty()
                && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
    }

    private List<BlockPos> findCrystalBlocks() {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(BlockInteractionHelper.getSphere(getPlayerPos(), placeRange.getValue().floatValue(), placeRange.getValue().intValue(), false, true, 0).stream().filter(this::canPlaceCrystal).collect(Collectors.toList()));
        return positions;
    }

    @Override
    public void onEnable() {
        if (announceUsage.getValue()) {
            Command.sendChatMessage("[CrystalAura] " + ChatFormatting.GREEN.toString() + "Enabled!");
        }
        hitDelayCounter = 0;
    }

    @Override
    public void onDisable() {
        renderBlock = null;
        target = null;
        resetRotation();
        if (announceUsage.getValue()) {
            Command.sendChatMessage("[CrystalAura] " + ChatFormatting.RED.toString() + "Disabled!");
        }
    }

    @Override
    public String getHudInfo() {
        if (target == null) {
            return "";
        } else {
            return target.getName().toUpperCase();
        }
    }

    private enum RenderMode {
        UP, BLOCK, NONE
    }

}
