package org.sourcecodede.fallingTrees;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;

public class FallingTrees extends JavaPlugin implements Listener {

    private final Set<Location> placedLogs = new HashSet<>();
    private static final int RADIUS = 5;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        if (isLog(block.getType())) {
            placedLogs.add(block.getLocation());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (isLog(block.getType()) && !placedLogs.contains(block.getLocation())) {
            breakTree(block);
        }
    }

    private void breakTree(Block startBlock) {
        Set<Location> visited = new HashSet<>();
        Set<Block> toBreak = new HashSet<>();

        Location origin = startBlock.getLocation();

        // Scanne alle Blöcke im 11x11x11 Bereich um den Startblock
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dy = -RADIUS; dy <= RADIUS; dy++) {
                for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                    Location checkLoc = origin.clone().add(dx, dy, dz);
                    if (visited.contains(checkLoc)) continue;

                    visited.add(checkLoc);

                    Block current = checkLoc.getBlock();
                    if ((isLog(current.getType()) || isWood(current.getType()))
                            && !placedLogs.contains(current.getLocation())) {
                        toBreak.add(current);
                    }
                }
            }
        }

        // Alle passenden Blöcke abbauen
        for (Block block : toBreak) {
            block.breakNaturally();
        }
    }

    private boolean isLog(Material material) {
        return material.name().endsWith("_LOG");
    }

    private boolean isWood(Material material) {
        return material.name().endsWith("_WOOD");
    }
}
