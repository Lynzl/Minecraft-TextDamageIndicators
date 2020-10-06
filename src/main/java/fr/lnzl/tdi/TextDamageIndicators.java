package fr.lnzl.tdi;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.WeakHashMap;

@Mod(TextDamageIndicators.MODID)
@Mod.EventBusSubscriber
public class TextDamageIndicators {

    public static final String MODID = "textdamageindicators";

    protected static final Logger LOGGER = LogManager.getLogger();

    public static final WeakHashMap<LivingEntity, EntityData> ENTITY_TRACKER = new WeakHashMap<>();

    public TextDamageIndicators() {
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingUpdateEvent(final LivingEvent.LivingUpdateEvent event) {
        LivingEntity livingEntity = event.getEntityLiving();

        int entityId = livingEntity.getEntityId();
        EntityData entityData = ENTITY_TRACKER.get(livingEntity);

        if (entityData == null) {
            entityData = new EntityData(livingEntity);
            ENTITY_TRACKER.put(livingEntity, entityData);
        } else {
            entityData.update(livingEntity);
        }

        if (entityData.damage != 0) {
            onEntityDamaged(livingEntity, entityData);
        }

    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityJoin(final EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        Entity player = Minecraft.getInstance().player;
        if (player == null) return;
        if (entity.equals(player)) ENTITY_TRACKER.clear();
    }

    @OnlyIn(Dist.CLIENT)
    public static void onEntityDamaged(LivingEntity livingEntity, EntityData entityData) {
        if (entityData.damage < 0) return; // TODO: Handle regen

        ClientWorld clientWorld = Minecraft.getInstance().world;
        if (clientWorld == null) return;

        Entity entity = clientWorld.getEntityByID(livingEntity.getEntityId());
        if (entity == null) return;

        Entity player = Minecraft.getInstance().player;
        if (player == null) return;
        if (livingEntity.equals(player)) return;

        String damageString = Util.formatDamageText(entityData.damage);

        double posX = livingEntity.getPosX();
        double posY = (livingEntity.getFireTimer() > 0) ?
                livingEntity.getPosYHeight(1) + 1.24 :
                livingEntity.getPosYHeight(1) + 0.24;
        double posZ = livingEntity.getPosZ();

        boolean considerCritical =
                (entityData.lastDamage != 0)
                        && (entityData.damage / (entityData.lastDamage) >= 1.5)
                        && (livingEntity.getEntityWorld().getGameTime() - entityData.lastDamageStamp < 240);

        int goldColor = TextFormatting.GOLD.getColor();
        int darkRedColor = TextFormatting.DARK_RED.getColor();

        TextParticle.DamageParticle damageParticle = new TextParticle.DamageParticle(clientWorld, posX, posY, posZ);
        damageParticle.setText(damageString);
        damageParticle.setColor(goldColor);
        if (considerCritical)
            damageParticle.setAnimationColor(goldColor, darkRedColor);
        damageParticle.setAnimationSize(1, 1.5);
        if (considerCritical)
            damageParticle.setAnimationSize(1.5, 2.5);
        damageParticle.setAnimationFade(true);
        damageParticle.setMaxAge(20);

        Minecraft.getInstance().particles.addEffect(damageParticle);

        if (!considerCritical) return;

        String onoText = Util.fetchOnomatopoeia();

        TextParticle.OnoParticle onoParticle = new TextParticle.OnoParticle(clientWorld, posX, livingEntity.getPosYHeight(0.5D), posZ);
        onoParticle.setText(onoText);
        onoParticle.setColor(goldColor);
        onoParticle.setAnimationSize(0.75, 1);
        onoParticle.setAnimationFade(true);
        onoParticle.setMaxAge(20);

        Minecraft.getInstance().particles.addEffect(onoParticle);
    }

    @OnlyIn(Dist.CLIENT)
    public static class EntityData {
        public float health;
        public long healthStamp;
        public float lastHealth;
        public long lastHealthStamp;
        public float damage;
        public long damageStamp;
        public float lastDamage;
        public long lastDamageStamp;
        public long lastUpdate;

        public EntityData(LivingEntity livingEntity) {
            long gameTimeNow = livingEntity.getEntityWorld().getGameTime();

            this.lastUpdate = gameTimeNow;

            this.health = livingEntity.getHealth();
            this.healthStamp = gameTimeNow;
        }

        public void update(LivingEntity livingEntity) {
            long gameTimeNow = livingEntity.getEntityWorld().getGameTime();

            this.lastUpdate = gameTimeNow;

            this.lastHealth = this.health;
            this.lastHealthStamp = this.healthStamp;
            this.health = livingEntity.getHealth();
            this.lastHealthStamp = gameTimeNow;

            if (this.health != this.lastHealth) {
                this.lastDamageStamp = this.damageStamp;
                this.lastDamage = this.damage;
                this.damage = this.lastHealth - this.health;
            } else {
                this.damage = 0;
            }
            this.damageStamp = gameTimeNow;
        }
    }
}