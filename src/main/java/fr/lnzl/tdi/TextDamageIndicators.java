package fr.lnzl.tdi;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(TextDamageIndicators.MODID)
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TextDamageIndicators {

    public static final String MODID = "textdamageindicators";

    protected static final Logger LOGGER = LogManager.getLogger();

    public TextDamageIndicators() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamageEvent(final LivingDamageEvent event) {
        LivingEntity entityLiving = event.getEntityLiving();

        ClientWorld clientWorld = Minecraft.getInstance().world;
        if (clientWorld == null) return;

        Entity entity = clientWorld.getEntityByID(entityLiving.getEntityId());
        if (entity == null) return;

        Entity player = Minecraft.getInstance().player.getEntity();
        if (player == null) return;
        if (entity.equals(player)) return;

        String damageString = Util.formatDamageText(event.getAmount());

        double posX = entity.getPosX();
        double posY = (entityLiving.getFireTimer() > 0) ?
                entity.getPosYHeight(1) + 1.24 :
                entity.getPosYHeight(1) + 0.24;
        double posZ = entity.getPosZ();

        boolean considerCritical =
                (entityLiving.lastDamage != 0)
                        && (event.getAmount() / (entityLiving.lastDamage) >= 1.5)
                        && (clientWorld.getGameTime() - entityLiving.lastDamageStamp < 240);

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

        TextParticle.OnoParticle onoParticle = new TextParticle.OnoParticle(clientWorld, posX, entity.getPosYHeight(0.5D), posZ);
        onoParticle.setText(onoText);
        onoParticle.setColor(goldColor);
        onoParticle.setAnimationSize(0.75, 1);
        onoParticle.setAnimationFade(true);
        onoParticle.setMaxAge(20);

        Minecraft.getInstance().particles.addEffect(onoParticle);
    }
}