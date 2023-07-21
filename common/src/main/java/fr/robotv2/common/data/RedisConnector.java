package fr.robotv2.common.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

/*
 * This file is part of helper, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 */

public class RedisConnector {

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);
    private final Logger redisLogger = Logger.getLogger("redis-service");

    private final JedisPool jedisPool;
    private final Set<String> channels = new HashSet<>();

    private AbstractMessenger messenger = null;
    private PubSubListener listener = null;

    public RedisConnector(@NotNull String address, int port, @Nullable String password) {
        this(new HostAndPort(address, port), password);
    }

    public RedisConnector(@NotNull HostAndPort hostAndPort, @Nullable String password) {

        final JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(16);

        if(password == null || password.trim().isEmpty()) {
            this.jedisPool = new JedisPool(config, hostAndPort.getHost(), hostAndPort.getPort());
        } else {
            this.jedisPool = new JedisPool(config, hostAndPort.getHost(), hostAndPort.getPort(), 2000, password);
        }

        try(Jedis jedis = this.jedisPool.getResource()) {
            jedis.ping();
        }

        executorService.submit(new Runnable() {

            private boolean broken = false;

            @Override
            public void run() {
                if (this.broken) {
                    redisLogger.info("Retrying subscription...");
                    this.broken = false;
                }

                try (Jedis jedis = getJedis()) {
                    try {
                        RedisConnector.this.listener = new PubSubListener();
                        jedis.subscribe(RedisConnector.this.listener, "helper-redis-dummy".getBytes(StandardCharsets.UTF_8));
                    } catch (Exception e) {
                        // Attempt to unsubscribe this instance and try again.
                        new RuntimeException("Error subscribing to listener", e).printStackTrace();
                        try {
                            RedisConnector.this.listener.unsubscribe();
                        } catch (Exception ignored) {

                        }
                        RedisConnector.this.listener = null;
                        this.broken = true;
                    }
                }

                if (this.broken) {
                    // reschedule the runnable
                    executorService.schedule(this, 50L, TimeUnit.MILLISECONDS);
                }
            }
        });

        executorService.scheduleAtFixedRate(() -> {

            final PubSubListener listener = RedisConnector.this.listener;

            if (listener == null || !listener.isSubscribed()) {
                return;
            }

            for (String channel : this.channels) {
                listener.subscribe(channel.getBytes(StandardCharsets.UTF_8));
            }

        }, 100L, 100L, TimeUnit.MILLISECONDS);
    }

    public void publish(String channel, String message) {
        try(Jedis jedis = getJedis()) {
            jedis.publish(channel.getBytes(StandardCharsets.UTF_8), message.getBytes(StandardCharsets.UTF_8));
        }
    }

    public void subscribe(String... channels) {
        for (String channel : channels) {
            subscribe(channel);
        }
    }

    public void subscribe(String channel) {
        redisLogger.info("Subscribing to channel: " + channel);
        this.channels.add(channel);
        this.listener.subscribe(channel.getBytes(StandardCharsets.UTF_8));
    }

    public void unsubscribe(String channel) {
        redisLogger.info("Unsubscribing from channel: " + channel);
        this.channels.remove(channel);
        this.listener.unsubscribe(channel.getBytes(StandardCharsets.UTF_8));
    }

    @NotNull
    public JedisPool getJedisPool() {
        Objects.requireNonNull(this.jedisPool, "jedisPool");
        return this.jedisPool;
    }

    @NotNull
    public Jedis getJedis() {
        return getJedisPool().getResource();
    }

    public void close() {
        if (this.listener != null) {
            this.listener.unsubscribe();
            this.listener = null;
        }

        if (this.jedisPool != null) {
            this.jedisPool.close();
        }
    }

    public void setMessenger(@Nullable AbstractMessenger messenger) {
        this.messenger = messenger;
    }

    public interface AbstractMessenger {
        void registerIncomingMessage(String channel, byte[] bytes);
    }

    private final class PubSubListener extends BinaryJedisPubSub {
        private final ReentrantLock lock = new ReentrantLock();
        private final Set<String> subscribed = ConcurrentHashMap.newKeySet();

        @Override
        public void subscribe(byte[]... channels) {
            this.lock.lock();
            try {
                for (byte[] channel : channels) {
                    String channelName = new String(channel, StandardCharsets.UTF_8);
                    if (this.subscribed.add(channelName)) {
                        super.subscribe(channel);
                    }
                }
            } finally {
                this.lock.unlock();
            }
        }

        @Override
        public void unsubscribe(byte[]... channels) {
            this.lock.lock();
            try {
                super.unsubscribe(channels);
            } finally {
                this.lock.unlock();
            }
        }

        @Override
        public void onSubscribe(byte[] channel, int subscribedChannels) {
            redisLogger.info("Subscribed to channel: " + new String(channel, StandardCharsets.UTF_8));
        }

        @Override
        public void onUnsubscribe(byte[] channel, int subscribedChannels) {
            String channelName = new String(channel, StandardCharsets.UTF_8);
            redisLogger.info("Unsubscribed from channel: " + channelName);
            this.subscribed.remove(channelName);
        }

        @Override
        public void onMessage(byte[] channel, byte[] message) {

            if(RedisConnector.this.messenger == null) {
                return;
            }

            String channelName = new String(channel, StandardCharsets.UTF_8);
            redisLogger.info("Message received on channel " + channelName);

            try {
                RedisConnector.this.messenger.registerIncomingMessage(channelName, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}