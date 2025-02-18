package top.xujiayao.mcdiscordchat.minecraft.mixins;

import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static top.xujiayao.mcdiscordchat.Main.CHANNEL;
import static top.xujiayao.mcdiscordchat.Main.CONFIG;
import static top.xujiayao.mcdiscordchat.Main.MULTI_SERVER;
import static top.xujiayao.mcdiscordchat.Main.TEXTS;

/**
 * @author Xujiayao
 */
@Mixin(PlayerAdvancementTracker.class)
public abstract class MixinPlayerAdvancementTracker {

	@Shadow
	private ServerPlayerEntity owner;

	@Shadow
	public abstract AdvancementProgress getProgress(Advancement advancement);

	@Inject(method = "grantCriterion", at = @At("RETURN"))
	private void grantCriterion(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
		if (CONFIG.generic.announceAdvancements
				&& getProgress(advancement).isDone()
				&& advancement.getDisplay() != null
				&& advancement.getDisplay().shouldAnnounceToChat()
				&& owner.world.getGameRules().getBoolean(GameRules.ANNOUNCE_ADVANCEMENTS)) {
			switch (advancement.getDisplay().getFrame()) {
				case GOAL -> {
					CHANNEL.sendMessage(TEXTS.advancementGoal()
							.replace("%playerName%", MarkdownSanitizer.escape(owner.getEntityName()))
							.replace("%advancement%", advancement.getDisplay().getTitle().getString())).queue();
					if (CONFIG.multiServer.enable) {
						MULTI_SERVER.sendMessage(false, false, false, null, TEXTS.advancementGoal()
								.replace("%playerName%", MarkdownSanitizer.escape(owner.getEntityName()))
								.replace("%advancement%", advancement.getDisplay().getTitle().getString()));
					}
				}
				case TASK -> {
					CHANNEL.sendMessage(TEXTS.advancementTask()
							.replace("%playerName%", MarkdownSanitizer.escape(owner.getEntityName()))
							.replace("%advancement%", advancement.getDisplay().getTitle().getString())).queue();
					if (CONFIG.multiServer.enable) {
						MULTI_SERVER.sendMessage(false, false, false, null, TEXTS.advancementTask()
								.replace("%playerName%", MarkdownSanitizer.escape(owner.getEntityName()))
								.replace("%advancement%", advancement.getDisplay().getTitle().getString()));
					}
				}
				case CHALLENGE -> {
					CHANNEL.sendMessage(TEXTS.advancementChallenge()
							.replace("%playerName%", MarkdownSanitizer.escape(owner.getEntityName()))
							.replace("%advancement%", advancement.getDisplay().getTitle().getString())).queue();
					if (CONFIG.multiServer.enable) {
						MULTI_SERVER.sendMessage(false, false, false, null, TEXTS.advancementChallenge()
								.replace("%playerName%", MarkdownSanitizer.escape(owner.getEntityName()))
								.replace("%advancement%", advancement.getDisplay().getTitle().getString()));
					}
				}
			}
		}
	}
}
