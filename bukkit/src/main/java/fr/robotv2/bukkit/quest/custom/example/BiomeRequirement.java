package fr.robotv2.bukkit.quest.custom.example;

import com.google.common.base.Enums;
import fr.robotv2.bukkit.quest.Quest;
import fr.robotv2.bukkit.quest.requirements.QuestRequirement;
import org.apache.commons.lang.Validate;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

public class BiomeRequirement extends QuestRequirement<Biome> {

    private final boolean all;
    private final EnumSet<Biome> biomes = EnumSet.noneOf(Biome.class);

    public BiomeRequirement(Quest quest) {
        super(quest);

        final List<String> biomeList = quest.getSection().getStringList("required_biomes");

        this.all = biomeList.contains("*");

        for(String biomeString : quest.getSection().getStringList("required_biomes")) {
            final Biome biome = Enums.getIfPresent(Biome.class, biomeString.toUpperCase(Locale.ROOT)).orNull();
            Validate.notNull(biome, biomeString + " is not a valid biome.");
            this.biomes.add(biome);
        }
    }

    @Override
    public Class<? extends Biome> classGeneric() {
        return Biome.class;
    }

    @Override
    public boolean isTarget(@NotNull Biome aBiome) {
        return all || biomes.contains(aBiome);
    }
}
