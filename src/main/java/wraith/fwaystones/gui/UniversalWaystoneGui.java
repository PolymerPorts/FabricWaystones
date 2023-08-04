package wraith.fwaystones.gui;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import wraith.fwaystones.FabricWaystones;
import wraith.fwaystones.access.PlayerAccess;
import wraith.fwaystones.access.PlayerEntityMixinAccess;
import wraith.fwaystones.block.WaystoneBlock;
import wraith.fwaystones.block.WaystoneBlockEntity;
import wraith.fwaystones.item.AbyssWatcherItem;
import wraith.fwaystones.util.Config;
import wraith.fwaystones.util.TeleportSources;
import wraith.fwaystones.util.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class UniversalWaystoneGui extends PagedGui {
    private final Predicate<UniversalWaystoneGui> keepOpen;
    private final TeleportSources source;
    protected boolean teleported = false;
    private ArrayList<String> sortedWaystones;

    protected UniversalWaystoneGui(ServerPlayerEntity player, Text title, TeleportSources source, Predicate<UniversalWaystoneGui> keepOpen, @Nullable Consumer<UniversalWaystoneGui> closeCallback) {
        super(player, closeCallback);
        this.keepOpen = keepOpen;
        this.source = source;
        this.setTitle(title);
    }

    public static void open(ServerPlayerEntity user, Text title, TeleportSources source) {
        var ui = new UniversalWaystoneGui(user, title, source, source == TeleportSources.ABYSS_WATCHER ? t -> {
            for (var hand : Hand.values()) {
                if (user.getStackInHand(hand).getItem() instanceof AbyssWatcherItem) {
                    return true;
                }
            }
            return false;
        } : t -> true, (gui) -> {});

        ui.updateDisplay();
        ui.open();
    }

    public static void open(ServerPlayerEntity user, WaystoneBlockEntity waystone) {
        UniversalWaystoneGui ui;

        if (user.getUuid().equals(waystone.getOwner()) || user.hasPermissionLevel(2)) {
            ui = new UniversalWaystoneGui(user, Text.literal(waystone.getWaystoneName()), TeleportSources.WAYSTONE,
                    t -> !waystone.isRemoved() && ((PlayerEntityMixinAccess) user).hasDiscoveredWaystone(waystone),
                    (gui) -> {}
            ) {
                @Override
                protected boolean isSelf(String hash) {
                    return waystone.getHash().equals(hash);
                }

                @Override
                protected DisplayElement getNavElement(int id) {
                    return switch (id) {
                        case 1 -> DisplayElement.previousPage(this);
                        case 3 -> DisplayElement.nextPage(this);
                        case 5 -> getCost();
                        case 7 -> Permissions.check(this.player, "waystones.can_edit_any", 3) || this.player.getUuid().equals(waystone.getOwner()) ? DisplayElement.of(
                                new GuiElementBuilder(Items.REDSTONE)
                                        .setName(Text.translatable("fwaystones.config.tooltip.config"))
                                        .setCallback((x, y, z) -> {
                                            playClickSound(this.player);
                                            WaystoneSettingsGui.open(user, waystone);
                                        })
                        ) : DisplayElement.filler();
                        default -> DisplayElement.filler();
                    };
                }
            };
        } else {
            ui = new UniversalWaystoneGui(user, Text.literal(waystone.getWaystoneName()), TeleportSources.WAYSTONE,
                    t -> !waystone.isRemoved() && ((PlayerEntityMixinAccess) user).hasDiscoveredWaystone(waystone),
                    (gui) -> {}
            ) {
                @Override
                protected boolean isSelf(String hash) {
                    return waystone.getHash().equals(hash);
                }
            };
        }

        ui.updateDisplay();
        ui.open();
    }


    public TeleportSources getSource() {
        return this.source;
    }

    @Override
    public void onTick() {
        if (!this.keepOpen.test(this)) {
            this.close();
        }
    }

    protected boolean isSelf(String hash) {
        return false;
    }

    @Override
    protected void updateDisplay() {
        this.updateWaystones();
        super.updateDisplay();
    }

    private void updateWaystones() {
        this.sortedWaystones = new ArrayList<>();
        if (((PlayerEntityMixinAccess) player).shouldViewDiscoveredWaystones()) {
            this.sortedWaystones.addAll(((PlayerEntityMixinAccess) player).getDiscoveredWaystones());
        }
        if (((PlayerEntityMixinAccess) player).shouldViewGlobalWaystones()) {
            for (String waystone : FabricWaystones.WAYSTONE_STORAGE.getGlobals()) {
                if (!this.sortedWaystones.contains(waystone)) {
                    this.sortedWaystones.add(waystone);
                }
            }
        }
        this.sortedWaystones.sort(Comparator.comparing(a -> {
            var data = FabricWaystones.WAYSTONE_STORAGE.getWaystoneData(a);
            return data != null ? data.getWaystoneName() : "";
        }));
    }

    @Override
    protected int getPageAmount() {
        return MathHelper.ceil(this.sortedWaystones.size() / (double) PAGE_SIZE);
    }

    @Override
    protected DisplayElement getElement(int id) {
        if (this.sortedWaystones.size() > id) {
            var hash = this.sortedWaystones.get(id);
            var tmpWaystone = FabricWaystones.WAYSTONE_STORAGE.getWaystoneData(hash);

            var builder =  new GuiElementBuilder(tmpWaystone.isGlobal() ? Items.ENDER_EYE : Items.ENDER_PEARL)
                    .setName(Text.literal(tmpWaystone.getWaystoneName()))
                    .hideFlags()
                    .setCallback((x, y, z) -> this.handleSelection(x, y, z, hash));

            if (this.isSelf(hash)) {
                builder.enchant(Enchantments.LURE, 1);
            }

            return DisplayElement.of(builder);
        }

        return DisplayElement.empty();
    }

    private void handleSelection(int i, ClickType type, SlotActionType actionType, String hash) {
        var waystone = FabricWaystones.WAYSTONE_STORAGE.getWaystoneEntity(hash);
        if (waystone == null) {
            return;
        }
        if (type.shift && type.isRight) {
            if (waystone.isGlobal()) {
                return;
            }
            if (player.getUuid().equals(waystone.getOwner())) {
                waystone.setOwner(null);
            }
            ((PlayerEntityMixinAccess) player).forgetWaystone(hash);
            playClickSound(this.player);
            this.updateDisplay();
        } else if (type.isLeft) {
            if (waystone.getWorld() != null && !(waystone.getWorld().getBlockState(waystone.getPos()).getBlock() instanceof WaystoneBlock)) {
                FabricWaystones.WAYSTONE_STORAGE.removeWaystone(hash);
                waystone.getWorld().removeBlockEntity(waystone.getPos());
            } else {
                if (Utils.canTeleport(player,  hash, false) && !this.isSelf(hash)) {
                    playClickSound(this.player);
                    this.teleported = waystone.teleportPlayer(player, true);
                    this.close();
                }
            }
        }

    }

    @Override
    protected DisplayElement getNavElement(int id) {
        return switch (id) {
            case 1 -> DisplayElement.previousPage(this);
            case 3 -> DisplayElement.nextPage(this);
            case 6 -> getCost();
            default -> DisplayElement.filler();
        };
    }

    protected DisplayElement getCost() {
            String cost = Config.getInstance().teleportType();
            int amount = Config.getInstance().baseTeleportCost();

            Item item;
            String type;

            switch (cost) {
                case "hp":
                case "health":
                    item = Items.RED_DYE;
                    type = "health";
                    break;
                case "hunger":
                case "saturation":
                    item = Items.PORKCHOP;
                    type = "hunger";
                    break;
                case "xp":
                case "experience":
                    item = Items.EXPERIENCE_BOTTLE;
                    type = "xp";
                    break;
                case "level":
                    item = Items.EXPERIENCE_BOTTLE;
                    type = "level";
                    break;
                case "item":
                    item = Registries.ITEM.get(Config.getInstance().teleportCostItem());
                    type = "item";
                    break;
                default:
                    return DisplayElement.filler();
            }

            return DisplayElement.of(new GuiElementBuilder(item)
                    .setName(Text.translatable("polyport.waystones.cost", amount, type.equals("item") ? item.getName() : Text.translatable("fwaystones.cost." + type)) )
                    .setCount(amount));
    }
}
