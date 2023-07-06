package fr.robotv2.bungeecord.util;

import com.google.common.base.Charsets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

// From https://www.spigotmc.org/threads/getting-offline-player-in-bungeecord.527164/

public class UUIDFetcher {

    private UUIDFetcher() { }

    private static final Map<String, UUID> uuids = new HashMap<>();
    private static final String API_URL = "https://playerdb.co/api/player/minecraft/%s";

    public static @NotNull CompletableFuture<UUID> getUUID(@NotNull String name, boolean cracked) {

        CompletableFuture<UUID> future = new CompletableFuture<>();

        if (uuids.containsKey(name)) { // IF UUID IS CACHED
            UUID cachedUUID = uuids.get(name);
            future.complete(cachedUUID);
            return future;
        }

        final ProxiedPlayer player = ProxyServer.getInstance().getPlayer(name); // IF PLAYER IS CONNECTED

        if(player != null  && player.isConnected()) {
            UUID uuid = player.getUniqueId();
            uuids.put(player.getName(), uuid);
            future.complete(uuid);
            return future;
        }

        if (cracked) { // IF SERVER IS CRACKED
            UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));
            uuids.put(name, uuid);
            future.complete(uuid);
            return future;
        }

        CompletableFuture.supplyAsync(() -> getUUIDFromExtern(name)) // THEN
                .thenAccept(uuid -> {
                    if (uuid != null) {
                        uuids.put(name, uuid);
                    }
                    future.complete(uuid);
                });

        return future;
    }

    @Nullable
    private static UUID getUUIDFromExtern(@NotNull String name) {
        name = name.toLowerCase(); // Had some issues with upper-case letters in the username, so I added this to make sure that doesn't happen.

        try {
            HttpURLConnection connection =
                    (HttpURLConnection) new URL(String.format(API_URL, name)).openConnection();

            connection.setUseCaches(false);
            connection.setDefaultUseCaches(false);
            connection.addRequestProperty("User-Agent", "Mozilla/5.0");
            connection.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
            connection.addRequestProperty("Pragma", "no-cache");
            connection.setReadTimeout(5000);

            // These connection parameters need to be set or the API won't accept the connection.

            try (BufferedReader bufferedReader =
                         new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) response.append(line);

                final JsonElement parsed = JsonParser.parseString(response.toString());

                if (parsed == null || !parsed.isJsonObject()) {
                    return null;
                }

                JsonObject data = parsed.getAsJsonObject(); // Read the returned JSON data.

                return UUID.fromString(
                        data.get("data")
                                .getAsJsonObject()
                                .get("player")
                                .getAsJsonObject()
                                .get("id") // Grab the UUID.
                                .getAsString());
            }
        } catch (Exception ignored) {
            // Ignoring exception since this is usually caused by non-existent usernames.
        }

        return null;
    }

}
