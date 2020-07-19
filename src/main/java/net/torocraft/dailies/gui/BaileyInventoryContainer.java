package net.torocraft.dailies.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.torocraft.dailies.entities.EntityRegistryHandler;

import javax.annotation.Nullable;

public class BaileyInventoryContainer extends Container {

    private PlayerEntity player;
    private PlayerInventory inventory;

    public BaileyInventoryContainer(int id, PlayerInventory inventory, PlayerEntity player) {
        super(null, id);
    }

    protected BaileyInventoryContainer(@Nullable ContainerType<?> type, int id) {
        super(type, id);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return false;
    }
}
