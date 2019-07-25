/*  
 *  WeatherMan, Minecraft bukkit plugin
 *  ©2012-2018, fromgate, fromgate@gmail.com
 *  https://www.spigotmc.org/resources/weatherman.43379/
 *    
 *  This file is part of WeatherMan.
 *  
 *  WeatherMan is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  WeatherMan is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with WeatherMan.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package me.fromgate.weatherman.util;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class WMWorldEdit {

    private static WorldEditPlugin worldedit;
    private static WorldGuardPlugin worldguard;
    private static boolean worldeditActive = false;
    private static boolean worldguardActive = false;

    public static void init() {
        worldeditActive = ConnectWorldEdit();
        worldguardActive = ConnectWorldGuard();
    }


    public static boolean isWE() {
        return worldeditActive;
    }

    public static boolean isWG() {
        return worldguardActive;
    }


    public static boolean ConnectWorldEdit() {
        Plugin worldEdit = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if ((worldEdit != null) && (worldEdit instanceof WorldEditPlugin)) {
            worldedit = (WorldEditPlugin) worldEdit;
            return true;
        }
        return false;
    }

    public static boolean ConnectWorldGuard() {
        Plugin worldGuard = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        if ((worldGuard != null) && (worldGuard instanceof WorldGuardPlugin)) {
            worldguard = (WorldGuardPlugin) worldGuard;
            return true;
        }
        return false;
    }

    public static Location getMinPoint(World world, String rg) {
        if (!worldguardActive) return null;
        if (world == null) return null;
        if (rg.isEmpty()) return null;
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager manager = container.get(BukkitAdapter.adapt(world));
        ProtectedRegion region = manager.getRegion(rg);
        if (region == null) return null;
        return new Location(world, region.getMinimumPoint().getBlockX(), region.getMinimumPoint().getBlockY(), region.getMinimumPoint().getBlockZ());
    }

    public static Location getMaxPoint(World world, String rg) {
        if (!worldguardActive) return null;
        if (world == null) return null;
        if (rg.isEmpty()) return null;
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager manager = container.get(BukkitAdapter.adapt(world));
        ProtectedRegion region = manager.getRegion(rg);
        if (region == null) return null;
        return new Location(world, region.getMaximumPoint().getBlockX(), region.getMaximumPoint().getBlockY(), region.getMaximumPoint().getBlockZ());
    }

    public static boolean isRegionExists(World world, String rg) {
        if (!WMWorldEdit.isWG()) return false;
        if (world == null) return false;
        if (rg.isEmpty()) return false;
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager manager = container.get(BukkitAdapter.adapt(world));
        ProtectedRegion region = manager.getRegion(rg);
        return (region != null);
    }

    public static boolean isRegionExists(String region) {
        if (!WMWorldEdit.isWG()) return false;
        for (World w : Bukkit.getWorlds()) {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager manager = container.get(BukkitAdapter.adapt(w));
            ProtectedRegion rg = manager.getRegion(region);
            if (rg != null) return true;
        }
        return false;
    }

    public static List<String> getRegions(Location loc) {
        List<String> rgList = new ArrayList<>();
        if (!WMWorldEdit.isWG()) return rgList;
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet regionSet = query.getApplicableRegions(BukkitAdapter.adapt(loc));
        if (regionSet.size() == 0) return rgList;
        for (ProtectedRegion rg : regionSet) rgList.add(rg.getId());
        return rgList;
    }

    //WorldEdit
    public static boolean isSelected(Player player) {
        if (!worldeditActive) return false;
        LocalSession session = worldedit.getSession((Player) player);
        Region sel;
        try {
             sel = session.getSelection(session.getSelectionWorld());
        } catch(Exception e) {
            return false;
        }

        return (sel != null);
    }

    public static Location getSelectionMinPoint(Player player) {
        if (!worldeditActive) return null;
        LocalSession session = worldedit.getSession((Player) player);
        Region sel;
        try {
            sel = session.getSelection(session.getSelectionWorld());
        } catch(Exception e) {
            return null;
        }
        if (sel == null) return null;
        return BukkitAdapter.adapt(player.getWorld(), sel.getMinimumPoint());
    }

    public static Location getSelectionMaxPoint(Player player) {
        if (!worldeditActive) return null;
        LocalSession session = worldedit.getSession((Player) player);
        Region sel;
        try {
            sel = session.getSelection(session.getSelectionWorld());
        } catch(Exception e) {
            return null;
        }
        return BukkitAdapter.adapt(player.getWorld(), sel.getMaximumPoint());
    }

    public static boolean isPlayerInRegion(Player player, String region) {
        return region != null && !region.isEmpty() && worldguardActive && getRegions(player.getLocation()).contains(region);
    }
}