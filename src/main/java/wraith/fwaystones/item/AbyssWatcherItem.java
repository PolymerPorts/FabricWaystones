package wraith.fwaystones.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import wraith.fwaystones.FabricWaystones;
import wraith.fwaystones.gui.UniversalWaystoneGui;
import wraith.fwaystones.util.TeleportSources;

public class AbyssWatcherItem extends Item implements PolymerItem {

    private static final Text TITLE = Text.translatable("container." + FabricWaystones.MOD_ID + ".abyss_watcher");

    public AbyssWatcherItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity serverPlayerEntity) {
            UniversalWaystoneGui.open(serverPlayerEntity, TITLE, TeleportSources.ABYSS_WATCHER);
        }
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return Items.ENDER_EYE;
    }

    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipContext context, @Nullable ServerPlayerEntity player) {
        var stack = PolymerItem.super.getPolymerItemStack(itemStack, context, player);
        stack.addEnchantment(Enchantments.LURE, 2);
        return stack;
    }
}
