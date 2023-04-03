package top.theillusivec4.diet.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.diet.DietMod;
import top.theillusivec4.diet.api.DietCapability;
import top.theillusivec4.diet.common.util.DietRegeneration;
import top.theillusivec4.diet.common.util.PlayerSensitive;

@SuppressWarnings("unused")
@Mixin(FoodData.class)
public class DietMixinFoodData implements PlayerSensitive {

    Player diet_player;

    @Inject(at = @At("TAIL"), method = "eat(IF)V")
    public void diet$eat(int healing, float saturationModifier, CallbackInfo ci) {

        if (diet_player == null) {
            DietMod.LOGGER.error("Attempted to add food stats to a null player!");
            return;
        }
        DietCapability.get(diet_player).ifPresent(tracker -> {
            ItemStack captured = tracker.getCapturedStack();

            if (!captured.isEmpty()) {
                tracker.consume(captured, healing, saturationModifier);
                tracker.captureStack(ItemStack.EMPTY);
            }
        });
    }

    @Redirect(
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/world/level/GameRules.getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"),
        method = "tick(Lnet/minecraft/world/entity/player/Player;)V")
    public boolean diet$tick(GameRules instance, GameRules.Key<GameRules.BooleanValue> pKey) {
        boolean flag = instance.getBoolean(pKey);
        return diet_player != null ? DietRegeneration.hasRegen(diet_player, flag) : flag;
    }

    @Override
    public void setPlayer(Player playerIn) {
        diet_player = playerIn;
    }
}