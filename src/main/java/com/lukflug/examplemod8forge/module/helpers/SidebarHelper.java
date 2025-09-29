package com.lukflug.examplemod8forge.module.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SidebarHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Pattern COLOR_ENCODING_PATTERN = Pattern.compile("(?i)[§�][0-9A-FK-OR]");

    private boolean streakActive = false;
    private long baseCoins = 0;
    private long gainedCoins = 0;

    private SidebarHelper(boolean register) {
        if (register) MinecraftForge.EVENT_BUS.register(this);
    }

    private static final SidebarHelper INSTANCE = new SidebarHelper(true);
    public static SidebarHelper getInstance() {
        return INSTANCE;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1);
        if (objective == null) return;

        List<String> lines = getSidebarLines(scoreboard, objective);

        String streakLine = lines.stream().filter(l -> l.contains("Streak:")).findFirst().orElse(null);
        boolean hasStreak = streakLine != null;

        if (hasStreak && !streakActive) {
            streakStarted(lines);
        } else if (!hasStreak && streakActive) {
            streakEnded(lines);
        } else if (streakActive) {
            updateDuringStreak(lines);
        }
    }

    private void streakStarted(List<String> lines) {
        streakActive = true;
        baseCoins = extractCoins(lines);
        gainedCoins = 0;
    }

    private void streakEnded(List<String> lines) {
        streakActive = false;
        long finalCoins = extractCoins(lines);
        gainedCoins = finalCoins - baseCoins;
    }

    private void updateDuringStreak(List<String> lines) {
        long currentCoins = extractCoins(lines);
        gainedCoins = currentCoins - baseCoins;
    }

    private long extractCoins(List<String> lines) {
        return lines.stream()
                .filter(l -> l.contains("Gold:") || l.contains("Coins:"))
                .map(l -> l.replaceAll("[^0-9]", ""))
                .filter(s -> !s.isEmpty())
                .mapToLong(Long::parseLong)
                .findFirst()
                .orElse(0);
    }

    private List<String> getSidebarLines(Scoreboard scoreboard, ScoreObjective objective) {
        List<Score> scores = new ArrayList<>(scoreboard.getScores());
        scores.sort(Comparator.comparingInt(Score::getScorePoints));

        return scores.stream()
                .filter(score -> score.getObjective().getName().equals(objective.getName()))
                .map(score -> {
                    String entry = score.getPlayerName();
                    ScorePlayerTeam team = scoreboard.getPlayersTeam(entry);

                    String prefix = (team != null && team.getColorPrefix() != null) ? team.getColorPrefix() : "";
                    String suffix = (team != null && team.getColorSuffix() != null) ? team.getColorSuffix() : "";

                    String full = prefix + entry + suffix;
                    return COLOR_ENCODING_PATTERN.matcher(full).replaceAll("");
                })
                .collect(Collectors.toList());
    }

    public boolean isStreakActive() {
        return streakActive;
    }

    public long getGainedCoins() {
        return gainedCoins;
    }
}
