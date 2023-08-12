package fr.robotv2.bukkit.quest.conditions.impl.player;

import fr.robotv2.bukkit.enums.QuestType;
import fr.robotv2.bukkit.quest.conditions.Condition;
import fr.robotv2.bukkit.util.NumberUtil;
import fr.robotv2.bukkit.util.text.PlaceholderUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.*;
import java.util.function.BiFunction;

public class PlaceholdersCondition implements Condition {

    private final List<PlaceholderCondition> placeholderConditions = new ArrayList<>();

    public PlaceholdersCondition(ConfigurationSection parent, String key) {

        final ConfigurationSection child = Objects.requireNonNull(parent.getConfigurationSection(key));

        for(String placeholderKey : child.getKeys(false)) {
            final ConfigurationSection placeholderSection = child.getConfigurationSection(placeholderKey);
            if(placeholderSection == null) continue;
            final PlaceholderCondition placeholderCondition = new PlaceholderCondition(placeholderSection);
            placeholderConditions.add(placeholderCondition);
        }
    }

    @Override
    public boolean matchCondition(Player value, QuestType type, Event event) {

        for(PlaceholderCondition condition : placeholderConditions) {

            final String placeholder = PlaceholderUtil.parsePlaceholders(value, condition.placeholder);

            if(condition.type == PlaceholderValueType.NUMERICAL && NumberUtil.isNumber(placeholder)) {
                final PlaceholderValueComparator comparator = condition.comparator == null ? PlaceholderValueComparator.EQUAL : condition.comparator;
                final double playerValue = NumberUtil.toNumber(placeholder).doubleValue();
                if(!comparator.function.apply(playerValue, condition.matchValue)) {
                    return false;
                }
            } else {
                if(!condition.match.equals(placeholder)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public EnumSet<QuestType> referencedType() {
        return EnumSet.allOf(QuestType.class);
    }

    private enum PlaceholderValueType {
        NUMERICAL,
        STRING,
        ;
    }

    private enum PlaceholderValueComparator {

        MORE((playerValue, matchValue) -> playerValue > matchValue),
        MORE_EQUAL((playerValue, matchValue) -> playerValue >= matchValue),
        EQUAL(Objects::equals),
        LESS_EQUAL((playerValue, matchValue) -> playerValue <= matchValue),
        LESS(((playerValue, matchValue) -> playerValue < matchValue)),
        ;

        private final BiFunction<Double, Double, Boolean> function;
        private final static PlaceholderValueComparator[] VALUES = values();

        PlaceholderValueComparator(BiFunction<Double, Double, Boolean> function) {
            this.function = function;
        }

        private static PlaceholderValueComparator fromName(String value) {
            return Arrays.stream(VALUES)
                    .filter(valueComparator -> valueComparator.name().equalsIgnoreCase(value))
                    .findFirst().orElse(null);
        }
    }

    // data class
    private final static class PlaceholderCondition {

        private final String placeholder;
        private final PlaceholderValueComparator comparator;

        private final String match;
        private final double matchValue;

        private final PlaceholderValueType type;

        private PlaceholderCondition(ConfigurationSection child) {
            this(
                    child.getString("placeholder"),
                    PlaceholderValueComparator.fromName(child.getString("comparator")),
                    child.getString("match")
            );
        }

        private PlaceholderCondition(String placeholder, PlaceholderValueComparator comparator, String match) {
            this.placeholder = placeholder;
            this.comparator = comparator;
            this.match = match;

            this.type = NumberUtil.isNumber(match) ? PlaceholderValueType.NUMERICAL : PlaceholderValueType.STRING;
            this.matchValue = type == PlaceholderValueType.NUMERICAL ? NumberUtil.toNumber(match).doubleValue() : 0;
        }
    }
}
