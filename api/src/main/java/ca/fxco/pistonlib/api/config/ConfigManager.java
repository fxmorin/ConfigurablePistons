package ca.fxco.pistonlib.api.config;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Config Options should never be changed async.
 * They should be changed near the end of the tick,
 * highly recommended that you do this during the MinecraftServer tickables using
 * {@code minecraftServer.addTickable()} or during the network tick.
 * Such as during a packet.
 *
 * @author FX
 * @since 1.0.4
 */
public interface ConfigManager {

    /**
     * Initial Initialization, maps and parses the config fields.
     * Add {@code pistonlib-configmanager} entrypoint to your ConfigManager instance
     * which would call this method after all Config Options from other mods are collected
     *
     * @param modId mod id of the entrypoint provider
     * @param fieldProvider config fields added by other mods to your configManager
     * @since 1.0.4
     */
    void init(String modId, Map<String, List<Field>> fieldProvider);

    /**
     * Initializes the config.
     * Loading the values from the config and saving them.
     *
     * @since 1.0.4
     */
    void initializeConfig();

    /**
     * Resets all values to their default value. Called when leaving a server.
     * It's not recommended to call this on the server, since all the config changes are done in individual packets.
     *
     * @since 1.0.4
     */
    void resetAllToDefault();

    /**
     * Add a TypeConverter to ConfigManager
     *
     * @see TypeConverter
     * @param converter the TypeConverter to add
     * @since 1.0.4
     */
    void addConverter(TypeConverter converter);

    /**
     * Try to load value using type converters
     *
     * @param value default value
     * @param parsedValue parsedValue to load from
     * @return new value or {@code null} if config manager doesn't have converters
     * @since 1.0.4
     */
    <T> T tryLoadingValue(Object value, ParsedValue<T> parsedValue);

    /**
     * Try to load value using type converters
     *
     * @param value value to save
     * @param parsedValue parsed value to which the value is saved
     * @return new value or {@code null} if config manager doesn't have converters
     * @since 1.0.4
     */
    <T> Object trySavingValue(T value, ParsedValue<T> parsedValue);

    /**
     * Generates all values from a config class,
     * and then loads the config file and sets all there values
     *
     * @param fields fields to turn into ConfigValue and then add to parsedValues
     * @since 1.0.4
     */
    void loadConfigFields(Field[] fields);

    /**
     * Resets the parsed value to its default state,
     * then writes all the values to the config file
     *
     * @since 1.0.4
     */
    void resetAndSaveValue(ParsedValue<?> value);

    /**
     * Sets and saves a value, which was set using the config command.
     *
     * @since 1.0.4
     */
    void saveValueFromCommand(ParsedValue<?> value, CommandSourceStack source, String inputValue);

    /**
     * Gets a config value from its name.
     *
     * @return The parsed value associated with this name, or {@code null} if no value was found.
     * @since 1.0.4
     */
    ParsedValue<?> getParsedValue(String valueName);

    /**
     * Gets all the config values.
     *
     * @return A collection of all the parsed values.
     * @since 1.0.4
     */
    Collection<ParsedValue<?>> getParsedValues();

    /**
     * Creates subcommand to edit config manager's values in game
     *
     * @param configManager config manager to create command for
     * @return Subcommand with literal arguments of the options
     */
    static LiteralArgumentBuilder<CommandSourceStack> createConfigSubCommand(
            ConfigManager configManager
    ) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("config")
                .requires(source -> source.hasPermission(4));
        configManager.getParsedValues().forEach(parsedValue ->
                builder.then(Commands.literal(parsedValue.getName())
                        .executes(ctx -> {
                            ctx.getSource().sendSuccess(() -> Component.translatable("commands.pistonlib.config.value",
                                    parsedValue.getName(), parsedValue.getValue()), false);
                            return 1;
                        })
                        .then(Commands.literal("set")
                                .then(Commands.argument("new value", StringArgumentType.word())
                                        .suggests((context, builder1) -> {
                                            Object value = parsedValue.getValue();
                                            String[] suggestions;

                                            if (parsedValue.getSuggestions().length != 0) {
                                                suggestions = parsedValue.getSuggestions();
                                            } else if (value instanceof Boolean) {
                                                suggestions = new String[]{"true", "false"};
                                            } else if (value instanceof Enum<?> enumValue) {
                                                Enum<?>[] enums = enumValue.getClass().getEnumConstants();
                                                suggestions = new String[enums.length];
                                                for (int i = 0; i < enums.length; i++) {
                                                    suggestions[i] = enums[i].toString();
                                                }
                                            } else {
                                                return Suggestions.empty();
                                            }
                                            return SharedSuggestionProvider.suggest(suggestions, builder1);
                                        }).executes(ctx -> {
                                            configManager.saveValueFromCommand(parsedValue, ctx.getSource(),
                                                    StringArgumentType.getString(ctx, "new value"));
                                            ctx.getSource().sendSuccess(() -> Component.translatable(
                                                    "commands.pistonlib.config.success"
                                                            + (parsedValue.requiresRestart() ? ".restart" : ""),
                                                    parsedValue.getName(), parsedValue.getValueToSave()), true);
                                            return 1;
                                        })))
                        .then(Commands.literal("default").executes(ctx -> {
                            configManager.resetAndSaveValue(parsedValue);
                            ctx.getSource().sendSuccess(() -> Component.translatable(
                                    "commands.pistonlib.config.success"
                                            + (parsedValue.requiresRestart() ? ".restart" : ""),
                                    parsedValue.getName(), parsedValue.getValueToSave()), true);
                            return 1;
                        }))
                )
        );
        return builder;
    }
}
