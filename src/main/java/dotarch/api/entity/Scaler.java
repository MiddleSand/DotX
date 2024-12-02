package dotarch.api.entity;

import dotarch.api.DotAPI;
import dotarch.api.data.inmemory.AttributeModifier;
import lombok.RequiredArgsConstructor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
public class Scaler {

    public void setSize(@NotNull LivingEntity entity, double newScale, boolean transition) {
        var currentScale = Objects.requireNonNull(
                entity.getAttribute(Attribute.GENERIC_SCALE)).getBaseValue();
        if(currentScale == newScale) return;

        if(transition) {
            applyTransition(entity, currentScale, newScale);
        } else {
            applyDirectScale(entity, newScale);
        }

        applyAttributeModifiers(entity, newScale);
    }

    private void applyTransition(LivingEntity entity, double currentScale, double newScale) {
        double stepSize = Math.abs(newScale - currentScale) / 5;
        boolean bigger = newScale > currentScale;

        AtomicReference<Double> scale = new AtomicReference<>(currentScale);

        DotAPI.instance().getServer().getScheduler().runTaskTimer(DotAPI.instance(), task -> {
            scale.updateAndGet(v -> bigger ? v + stepSize : v - stepSize);
            if(scale.get() == currentScale) return;

            if ((bigger && scale.get() >= newScale) ||
                    (!bigger && scale.get() <= newScale)) {
                task.cancel();
            }

            Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_SCALE))
                    .setBaseValue(scale.get());
        }, 0, 1);
    }

    private void applyDirectScale(LivingEntity entity, double newScale) {
        Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_SCALE))
                .setBaseValue(newScale);
    }

    private void applyAttributeModifiers(LivingEntity entity, double newScale) {
        List<AttributeModifier> modifiers = List.of(

                new AttributeModifier(
                        Attribute.GENERIC_JUMP_STRENGTH,
                        0.42D,
                        32.0D,
                        true,
                        true,
                        1
                ),

                new AttributeModifier(
                        Attribute.PLAYER_BLOCK_INTERACTION_RANGE,
                        4.5D,
                        64.0D,
                        true,
                        true,
                        1
                ),

                new AttributeModifier(
                        Attribute.PLAYER_ENTITY_INTERACTION_RANGE,
                        3.0D,
                        64.0D,
                        true,
                        true,
                        1
                ),

                new AttributeModifier(
                        Attribute.GENERIC_MOVEMENT_SPEED,
                        0.1D,
                        1024.0D,
                        true,
                        true,
                        1
                ),

                new AttributeModifier(
                        Attribute.GENERIC_STEP_HEIGHT,
                        0.6D,
                        10.0D,
                        true,
                        true,
                        1
                ),

                new AttributeModifier(
                        Attribute.GENERIC_SAFE_FALL_DISTANCE,
                        3.0D,
                        1024.0D,
                        true,
                        true,
                        1
                )
        );

        modifiers.forEach(modifier -> modifier.apply(entity, newScale));
    }

    public void resetSize(Player player) {
        List.of(
                new AttributeModifier(Attribute.GENERIC_SCALE, 1.0D),
                new AttributeModifier(Attribute.GENERIC_JUMP_STRENGTH, 0.42D),
                new AttributeModifier(Attribute.PLAYER_BLOCK_INTERACTION_RANGE, 4.5D),
                new AttributeModifier(Attribute.PLAYER_ENTITY_INTERACTION_RANGE, 3.0D),
                new AttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, 0.1D),
                new AttributeModifier(Attribute.GENERIC_STEP_HEIGHT, 0.6D),
                new AttributeModifier(Attribute.GENERIC_SAFE_FALL_DISTANCE, 3.0D)
        ).forEach(modifier -> modifier.reset(player));
    }

    public Optional<LivingEntity> getEntity(Player player, int range) {
        Vector playerLookDir = player.getEyeLocation().getDirection();
        Vector playerEyeLocation = player.getEyeLocation().toVector();

        return player.getNearbyEntities(range, range, range).stream()
                .filter(e -> player.hasLineOfSight(e) && e instanceof LivingEntity)
                .map(e -> (LivingEntity) e)
                .min((e1, e2) -> {
                    Vector v1 = e1.getLocation().toVector().subtract(playerEyeLocation);
                    Vector v2 = e2.getLocation().toVector().subtract(playerEyeLocation);
                    return Double.compare(
                            playerLookDir.angle(v1),
                            playerLookDir.angle(v2)
                    );
                })
                .filter(e -> playerLookDir.angle(
                        e.getLocation().toVector().subtract(playerEyeLocation)) < 0.4f);
    }
}
