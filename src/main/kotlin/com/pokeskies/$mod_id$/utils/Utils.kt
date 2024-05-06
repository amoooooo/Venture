package com.pokeskies.$mod_id$.utils

import com.google.gson.*
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import com.pokeskies.$mod_id$.$mod_name$
import com.pokeskies.$mod_id$.config.ConfigManager
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.network.protocol.game.ClientboundSoundPacket
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import java.lang.reflect.Type

object Utils {
    // Useful logging functions
    fun printDebug(message: String, bypassCheck: Boolean = false) {
        if (bypassCheck || ConfigManager.CONFIG.debug)
            $mod_name$.LOGGER.info("[${$mod_name$.MOD_NAME}] DEBUG: $message")
    }

    fun printError(message: String) {
        $mod_name$.LOGGER.error("[${$mod_name$.MOD_NAME}] ERROR: $message")
    }

    fun printInfo(message: String) {
        $mod_name$.LOGGER.info("[${$mod_name$.MOD_NAME}] $message")
    }


    // Sends a player a sound packet
    fun sendPlayerSound(player: ServerPlayer, sound: SoundEvent, volume: Float, pitch: Float) {
        player.connection.send(
            ClientboundSoundPacket(
                Holder.direct(sound),
                SoundSource.MASTER,
                player.x,
                player.y,
                player.z,
                volume,
                pitch,
                player.level().getRandom().nextLong()
            )
        )
    }


    // Formats a time in seconds to the format "xd yh zm zs", but truncates unncessary parts
    fun getFormattedTime(time: Long): String {
        if (time <= 0) return "0s"
        val timeFormatted: MutableList<String> = ArrayList()
        val days = time / 86400
        val hours = time % 86400 / 3600
        val minutes = time % 86400 % 3600 / 60
        val seconds = time % 86400 % 3600 % 60
        if (days > 0) {
            timeFormatted.add(days.toString() + "d")
        }
        if (hours > 0) {
            timeFormatted.add(hours.toString() + "h")
        }
        if (minutes > 0) {
            timeFormatted.add(minutes.toString() + "m")
        }
        if (seconds > 0) {
            timeFormatted.add(seconds.toString() + "s")
        }
        return java.lang.String.join(" ", timeFormatted)
    }


    // Useful GSON seralizers for Minecraft Codecs. Thank you to Patbox for these
    data class RegistrySerializer<T>(val registry: Registry<T>) : JsonSerializer<T>, JsonDeserializer<T> {
        @Throws(JsonParseException::class)
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): T? {
            var parsed = if (json.isJsonPrimitive) registry.get(ResourceLocation.tryParse(json.asString)) else null
            if (parsed == null)
                printError("There was an error while deserializing a Registry Type: $registry")
            return parsed
        }
        override fun serialize(src: T, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(registry.getId(src).toString())
        }
    }

    data class CodecSerializer<T>(val codec: Codec<T>) : JsonSerializer<T>, JsonDeserializer<T> {
        @Throws(JsonParseException::class)
        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): T? {
            return try {
                codec.decode(JsonOps.INSTANCE, json).getOrThrow(false) { }.first
            } catch (e: Throwable) {
                printError("There was an error while deserializing a Codec: $codec")
                null
            }
        }

        override fun serialize(src: T?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
            return try {
                if (src != null)
                    codec.encodeStart(JsonOps.INSTANCE, src).getOrThrow(false) { }
                else
                    JsonNull.INSTANCE
            } catch (e: Throwable) {
                printError("There was an error while serializing a Codec: $codec")
                JsonNull.INSTANCE
            }
        }
    }
}