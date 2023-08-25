package fr.robotv2.bukkit.quest.requirements;

import fr.robotv2.bukkit.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class LocationQuestRequirement extends QuestRequirement<Location> {

    private final Location location;
    private final double distanceFromLocation;

    public LocationQuestRequirement(Quest quest) {
        super(quest);

        final String world = quest.getSection().getString("required_location.world");
        final double x = quest.getSection().getDouble("required_location.x");
        final double y = quest.getSection().getDouble("required_location.y");
        final double z = quest.getSection().getDouble("required_location.z");

        if(world == null || Bukkit.getWorld(world) == null) {
            throw new NullPointerException("world");
        }

        this.location = new Location(Bukkit.getWorld(world), x, y, z);
        this.distanceFromLocation = quest.getSection().getDouble("required_location.distance_from_location", 5D);
    }

    @Override
    public Class<? extends Location> classGeneric() {
        return Location.class;
    }

    @Override
    public boolean isTarget(@NotNull Location targetLocation) {
        final World world = location.getWorld();
        final World targetWorld = targetLocation.getWorld();

        if (world != targetWorld) {
            return false;
        }

        return targetLocation.distanceSquared(location) < (distanceFromLocation * distanceFromLocation);
    }
}
